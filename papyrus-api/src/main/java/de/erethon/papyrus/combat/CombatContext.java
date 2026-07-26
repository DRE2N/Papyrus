package de.erethon.papyrus.combat;

import de.erethon.spellbook.api.SpellEffect;
import de.erethon.spellbook.api.SpellTrait;
import de.erethon.spellbook.api.SpellbookSpell;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Scoped source metadata for synchronous damage, with stack inference as a
 * fallback for delayed callbacks and listeners.
 */
public final class CombatContext {

    private static final ThreadLocal<Deque<CombatSource>> SOURCES = new ThreadLocal<>();
    private static final StackWalker STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    private CombatContext() {
    }

    public static Scope enter(CombatSource source) {
        Deque<CombatSource> sources = SOURCES.get();
        if (sources == null) {
            sources = new ArrayDeque<>();
            SOURCES.set(sources);
        }
        sources.push(source);
        return new Scope(source);
    }

    public static void runSpell(SpellbookSpell spell, Runnable action) {
        try (Scope ignored = enter(CombatSource.spell(spell))) {
            action.run();
        }
    }

    public static @Nullable CombatSource current() {
        Deque<CombatSource> sources = SOURCES.get();
        return sources == null ? null : sources.peek();
    }

    public static @Nullable CombatSource capture() {
        CombatSource current = current();
        return current == null ? inferFromStack() : current;
    }

    private static @Nullable CombatSource inferFromStack() {
        return STACK_WALKER.walk(frames -> frames
                .map(StackWalker.StackFrame::getDeclaringClass)
                .map(CombatContext::findSourceClass)
                .filter(SourceClass::known)
                .findFirst()
                .map(source -> CombatSource.inferred(source.type(), source.sourceClass()))
                .orElse(null));
    }

    private static SourceClass findSourceClass(Class<?> frameClass) {
        Class<?> candidate = frameClass;
        while (candidate != null) {
            if (SpellbookSpell.class.isAssignableFrom(candidate)) {
                return new SourceClass(CombatSource.Type.SPELL, candidate);
            }
            if (SpellEffect.class.isAssignableFrom(candidate)) {
                return new SourceClass(CombatSource.Type.EFFECT, candidate);
            }
            if (SpellTrait.class.isAssignableFrom(candidate)) {
                return new SourceClass(CombatSource.Type.TRAIT, candidate);
            }
            candidate = candidate.getEnclosingClass();
        }
        return SourceClass.UNKNOWN;
    }

    private record SourceClass(CombatSource.Type type, Class<?> sourceClass) {
        private static final SourceClass UNKNOWN = new SourceClass(CombatSource.Type.UNKNOWN, Object.class);

        private boolean known() {
            return type != CombatSource.Type.UNKNOWN;
        }
    }

    public static final class Scope implements AutoCloseable {
        private final CombatSource source;
        private boolean closed;

        private Scope(CombatSource source) {
            this.source = source;
        }

        @Override
        public void close() {
            if (closed) {
                return;
            }
            closed = true;
            Deque<CombatSource> sources = SOURCES.get();
            if (sources == null || sources.isEmpty() || sources.peek() != source) {
                throw new IllegalStateException("Combat context scopes must close in LIFO order");
            }
            sources.pop();
            if (sources.isEmpty()) {
                SOURCES.remove();
            }
        }
    }
}

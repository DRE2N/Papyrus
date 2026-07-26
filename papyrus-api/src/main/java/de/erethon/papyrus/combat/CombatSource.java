package de.erethon.papyrus.combat;

import de.erethon.spellbook.api.SpellEffect;
import de.erethon.spellbook.api.SpellTrait;
import de.erethon.spellbook.api.SpellbookSpell;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Objects;

/**
 * Identifies the gameplay system that caused one damage instance.
 */
public record CombatSource(Type type, String id, String displayName, @Nullable CombatSource parent) {

    public CombatSource(Type type, String id, String displayName) {
        this(type, id, displayName, null);
    }

    public CombatSource {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(displayName, "displayName");
    }

    public static CombatSource spell(SpellbookSpell spell) {
        return new CombatSource(Type.SPELL, spell.getId(), humanize(spell.getClass()));
    }

    public static CombatSource effect(SpellEffect effect) {
        return new CombatSource(Type.EFFECT, effect.data.getId(), humanize(effect.getClass()), effect.getOriginSource());
    }

    public static CombatSource trait(SpellTrait trait) {
        return new CombatSource(Type.TRAIT, trait.getData().getId(), humanize(trait.getClass()));
    }

    public static CombatSource inferred(Type type, Class<?> sourceClass) {
        String className = sourceClass.getSimpleName();
        return new CombatSource(type, "class:" + sourceClass.getName(), humanize(className));
    }

    public static CombatSource basicAttack() {
        return new CombatSource(Type.BASIC_ATTACK, "basic_attack", "Basic Attack");
    }

    public @Nullable CombatSource causingSpell() {
        for (CombatSource source = this; source != null; source = source.parent) {
            if (source.type == Type.SPELL) {
                return source;
            }
        }
        return null;
    }

    public String attributionId() {
        StringBuilder result = new StringBuilder(id);
        for (CombatSource source = parent; source != null; source = source.parent) {
            result.append(" <- ").append(source.id);
        }
        return result.toString();
    }

    private static String humanize(Class<?> sourceClass) {
        return humanize(sourceClass.getSimpleName());
    }

    private static String humanize(String value) {
        String withoutSuffix = value
                .replaceFirst("(Spell|Effect|Trait)$", "")
                .replace('_', ' ')
                .replace('-', ' ');
        String separated = withoutSuffix.replaceAll("(?<=[a-z0-9])(?=[A-Z])", " ");
        String[] words = separated.toLowerCase(Locale.ROOT).split("\\s+");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }
            if (!result.isEmpty()) {
                result.append(' ');
            }
            result.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }
        return result.isEmpty() ? "Unknown" : result.toString();
    }

    public enum Type {
        BASIC_ATTACK,
        SPELL,
        EFFECT,
        TRAIT,
        SUMMON,
        UNKNOWN
    }
}

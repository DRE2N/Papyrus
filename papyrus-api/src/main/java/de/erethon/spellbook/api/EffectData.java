package de.erethon.spellbook.api;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class EffectData extends YamlConfiguration {

    enum StackMode {
        INTENSIFY,
        PROLONG,
    }

    SpellbookAPI spellbookAPI;
    SpellQueue queue;
    private final String id;
    private File file;

    private int maxDuration;
    private int maxStacks;

    private StackMode stackMode;

    private boolean isPositive;
    private Class<? extends SpellEffect> effectClass;
    private final ClassLoader classLoader;


    public EffectData(SpellbookAPI spellbookAPI, String id, ClassLoader classLoader) {
        this.spellbookAPI = spellbookAPI;
        this.id = id;
        queue = spellbookAPI.getQueue();
        this.classLoader = classLoader;
    }

    public String getIcon() {
        return getString("icon", "<red>E");
    }

    public int getMaxDuration() {
        return maxDuration;
    }

    public int getMaxStacks() {
        return maxStacks;
    }

    public StackMode getStackMode() {
        return stackMode;
    }

    public boolean isPositive() {
        return isPositive;
    }

    public SpellEffect getActiveEffect(LivingEntity caster, LivingEntity target, int duration, int stack) {
        SpellEffect spellEffect;
        try {
            spellEffect = effectClass.getDeclaredConstructor(EffectData.class, LivingEntity.class, LivingEntity.class, int.class, int.class).newInstance(this, caster, target, duration, stack);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            spellbookAPI.getServer().getLogger().warning("Could not create class for effect " + id + ": " + effectClass.getName());
            throw new RuntimeException(e);
        }
        return spellEffect;
    }

    public File getFile() {
        return file;
    }

    @Override
    public void load(@NotNull File file) throws IOException, InvalidConfigurationException {
        super.load(file);
        this.file = file;
        String className = getString("class");
        try {
            effectClass = (Class<? extends SpellEffect>) Class.forName("de.erethon.spellbook.effects." + className, true, classLoader);
        } catch (ClassNotFoundException e1) {
            spellbookAPI.getServer().getLogger().warning("Could not find class for effect " + id + ": " + className);
            throw new RuntimeException(e1);
        }
        maxStacks = getInt("maxStacks", 1);
        maxDuration = getInt("maxDuration", 10);
        isPositive = getBoolean("positive", true);
        stackMode = StackMode.valueOf(getString("stackMode", "INTENSIFY").toUpperCase());
    }
}

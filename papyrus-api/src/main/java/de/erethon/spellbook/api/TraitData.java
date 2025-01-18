package de.erethon.spellbook.api;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class TraitData extends YamlConfiguration {

    SpellbookAPI spellbookAPI;
    SpellQueue queue;

    private final String id;
    private File file;

    private Class<? extends SpellTrait> traitClass;
    private final ClassLoader classLoader;

    private final Set<SpellData> affectedSpells = new HashSet<>();
    private final Set<TraitData> affectedTraits = new HashSet<>();
    private int descriptionLineCount = 0;

    public TraitData(SpellbookAPI spellbookAPI, String id, ClassLoader classLoader) {
        this.spellbookAPI = spellbookAPI;
        this.id = id;
        queue = spellbookAPI.getQueue();
        this.classLoader = classLoader;
    }

    /**
     * @return the custom model data for the icon of this trait.
     */
    public int getIcon() {
        return getInt("iconModelData", 1);
    }

    public String getId() {
        return id;
    }

    /**
     * @return a Set of SpellbookSpells affected by this trait. <b>If the Set is empty, all Spells will be affected.</b>
     */
    public Set<SpellData> getAffectedSpells() {
        return affectedSpells;
    }

    public boolean affectedBySpell(SpellData spellData) {
        if (affectedSpells.isEmpty()) {
            return true;
        }
        return affectedSpells.contains(spellData);
    }

    public boolean affectedByTrait(TraitData traitData) {
        if (affectedTraits.isEmpty()) {
            return true;
        }
        return affectedTraits.contains(traitData);
    }

    public SpellTrait getActiveTrait(LivingEntity caster) {
        SpellTrait spellTrait;
        try {
            spellTrait = traitClass.getDeclaredConstructor(TraitData.class, LivingEntity.class).newInstance(this, caster);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            spellbookAPI.getServer().getLogger().warning("Could not create class for trait " + id + ": " + traitClass);
            throw new RuntimeException(e);
        }
        return spellTrait;
    }

    public void setDescriptionLineCount(int descriptionLineCount) {
        this.descriptionLineCount = descriptionLineCount;
    }

    public int getDescriptionLineCount() {
        return descriptionLineCount;
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
            traitClass = (Class<? extends SpellTrait>) Class.forName("de.erethon.spellbook.traits." + className, true, classLoader);
        } catch (ClassNotFoundException e1) {
            spellbookAPI.getServer().getLogger().warning("Could not find class for trait " + id + ": " + className);
            throw new RuntimeException(e1);
        }
        List<String> affectedIDs = getStringList("affectedSpells");
        for (String id : affectedIDs) {
            SpellData data = spellbookAPI.getLibrary().getSpellByID(id);
            if (data == null) {
                spellbookAPI.getServer().getLogger().warning("Could not find spell with ID " + id + " (Trait: " + id + ")");
                continue;
            }
            affectedSpells.add(data);
        }
        List<String> affectedTraitIDs = getStringList("affectedTraits");
        for (String id : affectedTraitIDs) {
            TraitData data = spellbookAPI.getLibrary().getTraitByID(id);
            if (data == null) {
                spellbookAPI.getServer().getLogger().warning("Could not find trait with ID " + id + " (Trait: " + id + ")");
                continue;
            }
            affectedTraits.add(data);
        }
    }
}

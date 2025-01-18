package de.erethon.spellbook.api;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.PluginClassLoader;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public class SpellLibrary {

    private final SpellbookAPI spellbookAPI;
    private final HashMap<String, SpellData> loadedSpells = new HashMap<>();
    private final HashMap<String, EffectData> loadedEffects = new HashMap<>();
    private final HashMap<String, TraitData> loadedTraits = new HashMap<>();

    public SpellLibrary(SpellbookAPI spellbookAPI) {
        this.spellbookAPI = spellbookAPI;
    }

    public HashMap<String, SpellData> getLoaded() {
        return loadedSpells;
    }

    public HashMap<String, EffectData> getLoadedEffects() {
        return loadedEffects;
    }

    public HashMap<String, TraitData> getLoadedTraits() {
        return loadedTraits;
    }

    public @Nullable SpellData getSpellByID(String id) {
        return loadedSpells.get(id);
    }

    public @Nullable EffectData getEffectByID(String id) {
        return loadedEffects.get(id);
    }

    public @Nullable TraitData getTraitByID(String id ) {
        return loadedTraits.get(id);
    }

    public void reload() {
        spellbookAPI.getQueue().clear();
        loadedEffects.clear();
        loadedSpells.clear();
        loadedTraits.clear();
        loadSpells(SpellbookAPI.SPELLS);
    }

    public void loadSpells(File spellFolder) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("Hecate");
        if (plugin == null) {
            spellbookAPI.getServer().getLogger().warning("Could not load spells due to Spellbook plugin missing.");
            return;
        }
        ClassLoader classLoader = plugin.getClass().getClassLoader();
        try {
            for (File f : getFilesForFolder(new File(spellFolder, "spells"))) {
                if (!f.getName().endsWith(".yml")) {
                    continue;
                }
                String id = f.getName().replace(".yml", "");

                SpellData spellData = new SpellData(spellbookAPI, id, classLoader);
                try {
                    spellData.load(f);
                } catch (IOException | InvalidConfigurationException e) {
                    throw new RuntimeException(e);
                }
                loadedSpells.put(id, spellData);
            }
            spellbookAPI.getServer().getLogger().info("Loaded " + loadedSpells.size() + " spells.");

            for (File f : getFilesForFolder(new File(spellFolder, "effects"))) {
                if (!f.getName().endsWith(".yml")) {
                    continue;
                }
                String id = f.getName().replace(".yml", "");
                EffectData effectData = new EffectData(spellbookAPI, id, classLoader);
                try {
                    effectData.load(f);
                } catch (IOException | InvalidConfigurationException e) {
                    throw new RuntimeException(e);
                }
                loadedEffects.put(id, effectData);
            }
            spellbookAPI.getServer().getLogger().info("Loaded " + loadedEffects.size() + " effects.");

            for (File f : getFilesForFolder(new File(spellFolder, "traits"))) {
                if (!f.getName().endsWith(".yml")) {
                    continue;
                }
                String id = f.getName().replace(".yml", "");
                TraitData traitData = new TraitData(spellbookAPI, id, classLoader);
                try {
                    traitData.load(f);
                } catch (IOException | InvalidConfigurationException e) {
                    throw new RuntimeException(e);
                }
                loadedTraits.put(id, traitData);
            }
            spellbookAPI.getServer().getLogger().info("Loaded " + loadedTraits.size() + " traits.");

        } catch (Exception e) {
            spellbookAPI.getServer().getLogger().log(Level.SEVERE, "An error occurred while loading spells/effects/traits: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static List<File> getFilesForFolder(File folder) {
        List<File> files = new ArrayList<>();
        if (!folder.isDirectory()) {
            throw new IllegalArgumentException("File \"" + folder.getName() + "\" is not a directory");
        }
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                files.addAll(getFilesForFolder(file));
            } else {
                files.add(file);
            }
        }
        return files;
    }
}

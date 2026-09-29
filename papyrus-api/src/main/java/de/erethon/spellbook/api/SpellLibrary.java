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
            spellbookAPI.getServer().getLogger().warning("Could not load spells because Hecate is missing.");
            return;
        }
        ClassLoader classLoader = plugin.getClass().getClassLoader();

        for (File file : getFilesForFolder(new File(spellFolder, "effects"))) {
            if (!file.getName().endsWith(".yml")) continue;
            String id = file.getName().replace(".yml", "");
            try {
                EffectData data = new EffectData(spellbookAPI, id, classLoader);
                data.load(file);
                loadedEffects.put(id, data);
            } catch (Exception | LinkageError e) {
                logLoadFailure("effect", file, e);
            }
        }

        for (File file : getFilesForFolder(new File(spellFolder, "spells"))) {
            if (!file.getName().endsWith(".yml")) continue;
            String id = file.getName().replace(".yml", "");
            try {
                SpellData data = new SpellData(spellbookAPI, id, classLoader);
                data.load(file);
                loadedSpells.put(id, data);
            } catch (Exception | LinkageError e) {
                logLoadFailure("spell", file, e);
            }
        }

        List<File> traitFiles = getFilesForFolder(new File(spellFolder, "traits"));
        for (File file : traitFiles) {
            if (file.getName().endsWith(".yml")) {
                String id = file.getName().replace(".yml", "");
                loadedTraits.put(id, new TraitData(spellbookAPI, id, classLoader));
            }
        }
        for (File file : traitFiles) {
            if (!file.getName().endsWith(".yml")) continue;
            String id = file.getName().replace(".yml", "");
            try {
                loadedTraits.get(id).load(file);
            } catch (Exception | LinkageError e) {
                loadedTraits.remove(id);
                logLoadFailure("trait", file, e);
            }
        }

        spellbookAPI.getServer().getLogger().info("Loaded " + loadedEffects.size() + " effects, "
                + loadedSpells.size() + " spells and " + loadedTraits.size() + " traits.");
    }

    private void logLoadFailure(String type, File file, Throwable failure) {
        spellbookAPI.getServer().getLogger().log(Level.WARNING,
                "Skipping invalid Spellbook " + type + " '" + file.getName() + "': " + failure, failure);
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

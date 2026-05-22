package de.erethon.spellbook.api;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class SpellQueue {

    SpellbookAPI spellbookAPI;
    private final List<SpellbookSpell> queue = new ArrayList<>();
    private final List<SpellbookSpell> activeSpells = new ArrayList<>();

    private final PluginManager pluginManager = Bukkit.getServer().getPluginManager();

    private int maxActiveSpellsPerTick;
    private int currentActiveSpells;
    private int maxSpellsPerTick;
    private int currentSpells;

    private final Server server;

    // configuration
    private YamlConfiguration config;
    private File configFile;

    public SpellQueue(SpellbookAPI spellbookAPI) {
        this.spellbookAPI = spellbookAPI;
        this.server = spellbookAPI.getServer();
        loadOrCreateConfig();
    }

    private void loadOrCreateConfig() {
        try {
            configFile = new File("spellbook.yml");
            if (!configFile.exists()) {
                // copy default from resources
                InputStream in = getClass().getClassLoader().getResourceAsStream("spellbook.yml");
                if (in != null) {
                    Files.copy(in, configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    spellbookAPI.getServer().getLogger().info("Created default spellbook.yml in server root");
                } else {
                    // no resource bundled, create an empty file
                    configFile.createNewFile();
                    spellbookAPI.getServer().getLogger().info("Created empty spellbook.yml in server root");
                }
            }
            config = YamlConfiguration.loadConfiguration(configFile);
        } catch (IOException e) {
            spellbookAPI.getServer().getLogger().log(Level.SEVERE, "Could not load or create spellbook.yml: " + e.getMessage());
            e.printStackTrace();
            config = null;
        }
    }

    public void reloadConfig() {
        if (configFile == null) configFile = new File("spellbook.yml");
        try {
            config = YamlConfiguration.loadConfiguration(configFile);
            spellbookAPI.getServer().getLogger().info("Reloaded spellbook.yml");
        } catch (Exception e) {
            spellbookAPI.getServer().getLogger().log(Level.WARNING, "Failed to reload spellbook.yml: " + e.getMessage());
        }
    }

    public void run() {
        currentSpells = 0;
        currentActiveSpells = 0;
        updateMaxSpellsPerTick();
        Iterator<SpellbookSpell> activeSpellIterator = activeSpells.iterator();
        while (activeSpellIterator.hasNext()) {
            currentActiveSpells++;
            if (currentActiveSpells >= maxActiveSpellsPerTick) {
                spellbookAPI.getServer().getLogger().warning("Active spell queue overflow! " + activeSpells.size() + "/" + maxActiveSpellsPerTick + " | Tick time: " + server.getAverageTickTime());
                break;
            }
            SpellbookSpell spell = activeSpellIterator.next();
            try {
                if (spell.shouldRemove()) {
                    activeSpellIterator.remove();
                } else if (canTickSpell(spell)){
                    spell.tick();
                }
            } catch (Exception e) {
                server.getLogger().log(Level.SEVERE, "An error occurred while executing " + spell.getId() + " for " + spell.getCaster().getName() + ": " + e.getMessage());
                e.printStackTrace();
            }

        }

        Iterator<SpellbookSpell> queueIterator = queue.iterator();
        while (queueIterator.hasNext()) {
            currentSpells++;
            if (currentSpells >= maxSpellsPerTick) {
                spellbookAPI.getServer().getLogger().warning("New spell queue overflow! " + queue.size() + "/" + maxSpellsPerTick + " | Tick time: " + server.getAverageTickTime());
                break;
            }
            SpellbookSpell spell = queueIterator.next();
            try {
                SpellCastEvent event = new SpellCastEvent(spell.getCaster(), spell, spell.getData());
                pluginManager.callEvent(event);
                if (event.isCancelled() || !canCastSpell(spell)) {
                    queueIterator.remove();
                    continue;
                }
                try {
                    spell.ready();
                } catch (Exception exception) {
                    server.getLogger().log(Level.SEVERE, "An error occurred while readying " + spell.getId() + " for " + spell.getCaster().getName() + ": " + exception.getMessage());
                    exception.printStackTrace();
                }
                queueIterator.remove();
                activeSpells.add(spell);
                spell.getCaster().addActiveSpell(spell);
            } catch (Exception e) {
                server.getLogger().log(Level.SEVERE, "An error occurred while readying " + spell.getId() + " for " + spell.getCaster().getName() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void updateMaxSpellsPerTick() {
        double tickTime = server.getAverageTickTime();

        // Try to read thresholds from config (mspt.thresholds as list of maps)
        try {
            if (config != null) {
                List<Map<String, Object>> thresholds = (List<Map<String, Object>>) (List) config.getMapList("mspt.thresholds");
                if (thresholds != null && !thresholds.isEmpty()) {
                    // sort descending by threshold value
                    Collections.sort(thresholds, new Comparator<Map<String, Object>>() {
                        @Override
                        public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                            double t1 = toDouble(o1.get("threshold"));
                            double t2 = toDouble(o2.get("threshold"));
                            return Double.compare(t2, t1);
                        }
                    });
                    for (Map<String, Object> entry : thresholds) {
                        double thr = toDouble(entry.get("threshold"));
                        if (tickTime > thr) {
                            maxActiveSpellsPerTick = toInt(entry.get("maxActiveSpellsPerTick"), fallbackActiveForThreshold(thr));
                            maxSpellsPerTick = toInt(entry.get("maxSpellsPerTick"), fallbackNewForThreshold(thr));
                            return;
                        }
                    }
                    // no threshold matched -> use default
                    maxActiveSpellsPerTick = config.getInt("mspt.default.maxActiveSpellsPerTick", 128);
                    maxSpellsPerTick = config.getInt("mspt.default.maxSpellsPerTick", 64);
                    return;
                }
            }
        } catch (Exception e) {
            spellbookAPI.getServer().getLogger().log(Level.WARNING, "Invalid spellbook.yml thresholds - falling back to defaults: " + e.getMessage());
        }

        // fallback to hard-coded defaults if config missing or invalid
        if (tickTime > 50.0) {
            maxActiveSpellsPerTick = 8;
            maxSpellsPerTick = 2;
        } else if (tickTime > 40.0) {
            maxActiveSpellsPerTick = 16;
            maxSpellsPerTick = 4;
        } else if (tickTime > 30.0) {
            maxActiveSpellsPerTick = 32;
            maxSpellsPerTick = 8;
        } else if (tickTime > 20.0) {
            maxActiveSpellsPerTick = 64;
            maxSpellsPerTick = 16;
        } else {
            maxActiveSpellsPerTick = 128;
            maxSpellsPerTick = 64;
        }
    }

    private static double toDouble(Object o) {
        if (o == null) return Double.NaN;
        if (o instanceof Number) return ((Number) o).doubleValue();
        try {
            return Double.parseDouble(o.toString());
        } catch (NumberFormatException e) {
            return Double.NaN;
        }
    }

    private static int toInt(Object o, int fallback) {
        if (o == null) return fallback;
        if (o instanceof Number) return ((Number) o).intValue();
        try {
            return Integer.parseInt(o.toString());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private static int fallbackActiveForThreshold(double thr) {
        if (thr >= 50.0) return 8;
        if (thr >= 40.0) return 16;
        if (thr >= 30.0) return 32;
        if (thr >= 20.0) return 64;
        return 128;
    }

    private static int fallbackNewForThreshold(double thr) {
        if (thr >= 50.0) return 2;
        if (thr >= 40.0) return 4;
        if (thr >= 30.0) return 8;
        if (thr >= 20.0) return 16;
        return 64;
    }

    public SpellbookSpell addToQueue(SpellbookSpell spell) {
        queue.add(spell);
        return spell;
    }

    public void clear() {
        queue.clear();
        activeSpells.clear();
    }

    private boolean canCastSpell(SpellbookSpell spell) {
        for (SpellbookSpell spellbookSpell : spell.getCaster().getActiveSpells()) {
            if (!spellbookSpell.onCast(spell)) {
                return false;
            }
        }
        for (SpellEffect effect : spell.getCaster().getEffects()) {
            if (!effect.onCast(spell)) {
                return false;
            }
        }
        return true;
    }

    private boolean canTickSpell(SpellbookSpell spell) {
        for (SpellbookSpell spellbookSpell : spell.getCaster().getActiveSpells()) {
            if (!spellbookSpell.onSpellTick(spell)) {
                return false;
            }
        }
        for (SpellEffect effect : spell.getCaster().getEffects()) {
            if (!effect.onSpellTick(spell)) {
                 return false;
            }
        }
        return true;
    }

}

package de.erethon.spellbook.api;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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

    public SpellQueue(SpellbookAPI spellbookAPI) {
        this.spellbookAPI = spellbookAPI;
        this.server = spellbookAPI.getServer();
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
        if (tickTime > 50.0) {
            maxActiveSpellsPerTick = 8;
            maxSpellsPerTick = 2;
        }
        else if (tickTime > 40.0) {
            maxActiveSpellsPerTick = 16;
            maxSpellsPerTick = 4;
        }
        else if (tickTime > 30.0) {
            maxActiveSpellsPerTick = 32;
            maxSpellsPerTick = 8;
        }
        else if (tickTime > 20.0) {
            maxActiveSpellsPerTick = 64;
            maxSpellsPerTick = 16;
        } else {
            maxActiveSpellsPerTick = 128;
            maxSpellsPerTick = 64;
        }
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

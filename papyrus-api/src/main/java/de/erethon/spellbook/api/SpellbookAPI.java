package de.erethon.spellbook.api;

import org.bukkit.Bukkit;
import org.bukkit.Server;

import java.io.File;

public class SpellbookAPI {
    private static SpellbookAPI instance;
    public static File SPELLS;
    private final SpellQueue queue;
    private final SpellLibrary library;
    private final Server server;

    public SpellbookAPI(Server server) {
        instance = this;
        this.server = server;

        SPELLS = new File(Bukkit.getPluginsFolder(), "Spellbook");
        if (!SPELLS.exists()) {
            SPELLS.mkdir();
        }

        queue = new SpellQueue(this);

        library = new SpellLibrary(this);
    }

    public Server getServer() {
        return server;
    }

    public static SpellbookAPI getInstance() {
        return instance;
    }

    public SpellLibrary getLibrary() {
        return library;
    }

    public SpellQueue getQueue() {
        return queue;
    }

}

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

public class SpellData extends YamlConfiguration {

    SpellbookAPI spellbookAPI;
    SpellQueue queue;
    private int cooldown;
    private final String id;
    private File file;
    private int descriptionLineCount = 0;

    private Class<? extends SpellbookSpell> spellClass;
    private final ClassLoader classLoader;

    public SpellData(SpellbookAPI spellbookAPI, String id, ClassLoader classLoader) {
        this.spellbookAPI = spellbookAPI;
        this.id = id;
        this.classLoader = classLoader;
        queue = spellbookAPI.getQueue();
    }
    /* Casting process:
    Player clicks button -> ActiveSpell is created and queued -> queue calls ActiveSpell#ready -> precast -> cast -> afterCast
    The precast method is called before the cast method. If it returns false, the cast method is not called and the spell
    is not cast. In the same way, the afterCast method will only be called if the cast method returned true.


    A queue is used so that spells are cast in a consistent order and to limit the amount of spells that can be cast per tick globally.
    If we can ensure that all damage is handled using the queue, we could even run the queue in a separate thread. T
    he queue adds a slight delay to the cast process, even when its empty, but that is not noticeable for players and
    could be easily hidden by adding animations, if needed.

    The current implementation only works for spells that are actively cast by a SpellCaster. Passive spells likely
    would be implemented using some sort of "trigger" that listens to events and then runs the usual queue/precast/cast/afterCast process.

    Spells should be implemented by extending this class and overriding the precast, cast and afterCast methods.
     This class will only exist once. Casting will create an instance of ActiveSpell, which should be used to handle all casting-related logic.

     */


    public SpellbookSpell queue(LivingEntity caster) {
        return queue.addToQueue(getActiveSpell(caster));
    }

    public SpellbookSpell getActiveSpell(LivingEntity caster) {
        SpellbookSpell spellbookSpell;
        try {
            spellbookSpell = spellClass.getDeclaredConstructor(LivingEntity.class, SpellData.class).newInstance(caster, this);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            spellbookAPI.getServer().getLogger().warning("Could not create class for spell " + id + ": " + spellClass.getName());
            throw new RuntimeException(e);
        }
        return spellbookSpell;
    }

    public String getId() {
        return id;
    }

    public int getCooldown() {
        return cooldown;
    }

    public SpellbookAPI getSpellbook() {
        return spellbookAPI;
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
    public void save(@NotNull File file) throws IOException {
        super.save(file);
    }

    @Override
    public void load(@NotNull File file) throws IOException, InvalidConfigurationException {
        super.load(file);
        this.file = file;
        String className = getString("class");
        try {
            spellClass = (Class<? extends SpellbookSpell>) Class.forName("de.erethon.spellbook.spells." + className, true, classLoader);
        } catch (ClassNotFoundException e) {
            spellbookAPI.getServer().getLogger().warning("Could not find class for spell " + id + ": " + className);
            throw new RuntimeException(e);
        }
        cooldown = getInt("cooldown", 0);
    }
}

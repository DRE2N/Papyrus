package de.erethon;

import de.erethon.hephaestus.HItem;
import de.erethon.hephaestus.events.MobSpawnEntityCreateEvent;
import de.erethon.hephaestus.events.MobSpawnSettingsEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Main;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.biome.MobSpawnSettings;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class PapyrusTestPlugin extends JavaPlugin implements CommandExecutor, Listener {
    ResourceLocation loc = new ResourceLocation("test", "test");
    HItem registered;

    @Override
    public void onEnable() {
        loc = new ResourceLocation("test", "test");
        if (!Main.itemLibrary.has(loc)) {
            HItem hItem = new HItem.Builder(this, new ResourceLocation("test", "test")).baseItem(Material.DIAMOND_CHESTPLATE).register();
        }
        registered = Main.itemLibrary.get(new ResourceLocation("test:test"));
        registered.setPlugin(this);
        registered.setBehaviour(new TestItemBehaviour(registered));
        Main.itemLibrary.readyBehaviours();
        getCommand("test").setExecutor(this);
        Main.itemLibrary.enableHandler(this);
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        player.getWorld().dropItem(player.getLocation(), registered.getItem().getBukkitStack());
        return true;
    }

    @EventHandler
    public void onTest(MobSpawnEntityCreateEvent event) {
        //event.mob = EntityType.PIG.create(event.world);
    }

    @EventHandler
    public void onTest2(MobSpawnSettingsEvent event) {
        event.spawnerData = java.util.Optional.of(new MobSpawnSettings.SpawnerData(EntityType.PIG, 1, 20, 25));
    }
}

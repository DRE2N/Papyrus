package de.erethon;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import de.erethon.hephaestus.HItem;
import de.erethon.hephaestus.HItemBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;


public class TestItemBehaviour extends HItemBehaviour {

    public TestItemBehaviour(HItem item) {
        super(item);
    }

    @Override
    public void onEquip(ItemStack stack, Player player, PlayerArmorChangeEvent event) {
        Bukkit.broadcastMessage("Equipped");
        super.onEquip(stack, player, event);
    }

    @Override
    public void onLeftClick(ItemStack stack, Player player, PlayerInteractEvent event) {
        Bukkit.broadcastMessage("left");
        super.onLeftClick(stack, player, event);
    }
}

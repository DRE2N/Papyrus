package de.erethon.papyrus.world;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Supplies an entity-scoped block overlay without replacing the underlying level.
 *
 * <p>Implementations must be safe to query from the owning level's tick thread. Papyrus copies
 * supplied states into immutable navigation sections before asynchronous pathfinding begins.</p>
 */
public interface VirtualTerrainProvider {

    /**
     * Returns the virtual state at a position, or {@code null} to use the physical level.
     */
    @Nullable BlockState getBlockState(BlockPos position);

    /**
     * Returns a monotonically increasing geometry revision for a section.
     */
    long getSectionRevision(long sectionKey);

    /**
     * Returns whether this provider still represents an active terrain view.
     */
    default boolean isValid() {
        return true;
    }
}

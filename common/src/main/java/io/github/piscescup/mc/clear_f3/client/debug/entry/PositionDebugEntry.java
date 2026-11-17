//#if MC>=260300
package io.github.piscescup.mc.clear_f3.client.debug.entry;

import io.github.piscescup.mc.clear_f3.client.debug.DebugGroups;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.Locale;
import java.util.Objects;

/**
 * The structured data provider for {@code DebugEntryPosition}.
 *
 * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
 * Name: <b>Yuantong Ren</b>
 * @version 1.0.0
 * @since 1.0.0
 */
public final class PositionDebugEntry implements DebugEntryProvider {
    /**
     * The destination of this entry's structured rows.
     */
    private final DebugEntryOutput output;

    /**
     * Create the data provider with the given row destination.
     *
     * @param output the destination of the collected rows
     */
    public PositionDebugEntry(DebugEntryOutput output) {
        this.output = Objects.requireNonNull(output, "output");
    }

    /**
     * Collect this entry's rows using the data already made available by vanilla.
     *
     * @param entry       the original vanilla entry
     * @param level       the server or client level, or {@code null} outside a world
     * @param clientChunk the cached client chunk, or {@code null} when unavailable
     * @param serverChunk the cached server chunk, or {@code null} when unavailable
     */
    @Override
    public void display(DebugScreenEntry entry, Level level, LevelChunk clientChunk, LevelChunk serverChunk) {
        displayPosition(level);
    }

    /**
     * Display the camera's position, region coordinates and facing direction.
     *
     * @param level the level supplied by vanilla
     */
    private void displayPosition(Level level) {
        Minecraft minecraft = Minecraft.getInstance();
        Entity camera = minecraft.getCameraEntity();
        if (camera == null || minecraft.level == null) {
            return;
        }
        BlockPos block = camera.blockPosition();
        ChunkPos chunk = ChunkPos.containing(block);
        Direction direction = camera.getDirection();
        output.row(DebugGroups.POSITION, "xyz", "%.3f / %.5f / %.3f", camera.getX(), camera.getY(), camera.getZ());
        output.row(DebugGroups.POSITION, "block", "%d %d %d", block.getX(), block.getY(), block.getZ());
        output.row(DebugGroups.POSITION, "chunk", "%d %d %d [%d %d in r.%d.%d.mca]",
            chunk.x(), SectionPos.blockToSectionCoord(block.getY()), chunk.z(),
            chunk.x() & 31, chunk.z() & 31, chunk.x() >> 5, chunk.z() >> 5);
        output.row(DebugGroups.POSITION, "facing", Component.translatable("clear_f3.facing",
                Component.translatable("clear_f3.direction_name." + direction.getName()),
                Component.translatable("clear_f3.direction." + direction.getName()),
                String.format(Locale.ROOT, "%.1f / %.1f", Mth.wrapDegrees(camera.getYRot()), Mth.wrapDegrees(camera.getXRot())))
            .withStyle(ChatFormatting.WHITE));
        output.row(DebugGroups.POSITION, "dimension", "%s", minecraft.level.dimension().identifier());
        if (level instanceof ServerLevel serverLevel) {
            output.row(DebugGroups.POSITION, "forced_chunks", "%d", serverLevel.getForceLoadedChunks().size());
        }
    }
}
//#endif

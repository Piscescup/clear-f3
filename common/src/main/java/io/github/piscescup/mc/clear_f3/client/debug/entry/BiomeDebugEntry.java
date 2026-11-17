//#if MC>=260300
package io.github.piscescup.mc.clear_f3.client.debug.entry;

import io.github.piscescup.mc.clear_f3.client.debug.DebugGroups;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.Objects;

/**
 * The structured data provider for {@code DebugEntryBiome}.
 *
 * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
 * Name: <b>Yuantong Ren</b>
 * @version 1.0.0
 * @since 1.0.0
 */
public final class BiomeDebugEntry implements DebugEntryProvider {
    /**
     * The destination of this entry's structured rows.
     */
    private final DebugEntryOutput output;

    /**
     * Create the data provider with the given row destination.
     *
     * @param output the destination of the collected rows
     */
    public BiomeDebugEntry(DebugEntryOutput output) {
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
        displayBiome(level);
    }

    /**
     * Display client biome information and optional server debug values.
     *
     * @param level the level supplied by vanilla
     */
    private void displayBiome(Level level) {
        Minecraft minecraft = Minecraft.getInstance();
        Entity camera = minecraft.getCameraEntity();
        if (camera == null || minecraft.level == null || !minecraft.level.isInsideBuildHeight(camera.blockPosition().getY())) {
            return;
        }
        BlockPos position = camera.blockPosition();
        output.row(DebugGroups.POSITION, "biome", "%s", minecraft.level.getBiome(position).getRegisteredName());
        if (SharedConstants.DEBUG_SHOW_SERVER_DEBUG_VALUES && level instanceof ServerLevel) {
            output.row(DebugGroups.POSITION, "server_biome", "%s", level.getBiome(position).getRegisteredName());
        }
    }
}
//#endif

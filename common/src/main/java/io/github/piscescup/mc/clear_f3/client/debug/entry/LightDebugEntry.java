//#if MC>=260300
package io.github.piscescup.mc.clear_f3.client.debug.entry;

import io.github.piscescup.mc.clear_f3.client.debug.DebugGroups;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.Objects;

/**
 * The structured data provider for {@code DebugEntryLight}.
 *
 * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
 * Name: <b>Yuantong Ren</b>
 * @version 1.0.0
 * @since 1.0.0
 */
public final class LightDebugEntry implements DebugEntryProvider {
    /**
     * The destination of this entry's structured rows.
     */
    private final DebugEntryOutput output;

    /**
     * Create the data provider with the given row destination.
     *
     * @param output the destination of the collected rows
     */
    public LightDebugEntry(DebugEntryOutput output) {
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
        displayLight(serverChunk);
    }

    /**
     * Display light measurements while respecting the vanilla server-debug flag.
     *
     * @param serverChunk the cached server chunk
     */
    private void displayLight(LevelChunk serverChunk) {
        Minecraft minecraft = Minecraft.getInstance();
        Entity camera = minecraft.getCameraEntity();
        if (camera == null || minecraft.level == null) {
            return;
        }
        BlockPos position = camera.blockPosition();
        output.row(DebugGroups.LIGHT, "client_light", "%d", minecraft.level.getChunkSource().getLightEngine().getRawBrightness(position, 0));
        output.row(DebugGroups.LIGHT, "client_sky_light", "%d", minecraft.level.getBrightness(LightLayer.SKY, position));
        output.row(DebugGroups.LIGHT, "client_block_light", "%d", minecraft.level.getBrightness(LightLayer.BLOCK, position));
        if (SharedConstants.DEBUG_SHOW_SERVER_DEBUG_VALUES) {
            if (serverChunk == null) {
                output.row(DebugGroups.LIGHT, "server_sky_light", Component.translatable("clear_f3.value.unavailable"));
                output.row(DebugGroups.LIGHT, "server_block_light", Component.translatable("clear_f3.value.unavailable"));
            } else {
                var light = serverChunk.getLevel().getLightEngine();
                output.row(DebugGroups.LIGHT, "server_sky_light", "%d", light.getLayerListener(LightLayer.SKY).getLightValue(position));
                output.row(DebugGroups.LIGHT, "server_block_light", "%d", light.getLayerListener(LightLayer.BLOCK).getLightValue(position));
            }
        }
    }
}
//#endif

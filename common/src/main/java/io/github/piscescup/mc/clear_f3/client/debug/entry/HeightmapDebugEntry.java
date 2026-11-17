//#if MC>=260300
package io.github.piscescup.mc.clear_f3.client.debug.entry;

import io.github.piscescup.mc.clear_f3.client.debug.DebugGroups;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Locale;
import java.util.Objects;

/**
 * The structured data provider for {@code DebugEntryHeightmap}.
 *
 * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
 * Name: <b>Yuantong Ren</b>
 * @version 1.0.0
 * @since 1.0.0
 */
public final class HeightmapDebugEntry implements DebugEntryProvider {
    /**
     * The destination of this entry's structured rows.
     */
    private final DebugEntryOutput output;

    /**
     * Create the data provider with the given row destination.
     *
     * @param output the destination of the collected rows
     */
    public HeightmapDebugEntry(DebugEntryOutput output) {
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
        displayHeightmaps(clientChunk, serverChunk);
    }

    /**
     * Display each applicable client and server heightmap as a named measurement.
     *
     * @param clientChunk the cached client chunk
     * @param serverChunk the cached server chunk
     */
    private void displayHeightmaps(LevelChunk clientChunk, LevelChunk serverChunk) {
        Minecraft minecraft = Minecraft.getInstance();
        Entity camera = minecraft.getCameraEntity();
        if (camera == null || minecraft.level == null || clientChunk == null) {
            return;
        }
        BlockPos position = camera.blockPosition();
        for (Heightmap.Types type : Heightmap.Types.values()) {
            Component name = Component.translatable("clear_f3.heightmap." + type.name().toLowerCase(Locale.ROOT));
            if (type.sendToClient()) {
                output.namedRow(DebugGroups.HEIGHTMAP, Component.translatable("clear_f3.row.client_heightmap", name),
                    Component.literal(Integer.toString(clientChunk.getHeight(type, position.getX(), position.getZ()))).withStyle(ChatFormatting.WHITE));
            }
            if (type.keepAfterWorldgen()) {
                output.namedRow(DebugGroups.HEIGHTMAP, Component.translatable("clear_f3.row.server_heightmap", name), serverChunk == null
                    ? Component.translatable("clear_f3.value.unavailable")
                    : Component.literal(Integer.toString(serverChunk.getHeight(type, position.getX(), position.getZ()))).withStyle(ChatFormatting.WHITE));
            }
        }
    }
}
//#endif

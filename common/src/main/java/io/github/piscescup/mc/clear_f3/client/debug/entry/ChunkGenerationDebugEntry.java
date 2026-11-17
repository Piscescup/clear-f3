//#if MC>=260300
package io.github.piscescup.mc.clear_f3.client.debug.entry;

import io.github.piscescup.mc.clear_f3.client.debug.DebugGroups;
import io.github.piscescup.mc.clear_f3.client.debug.DebugRow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.densityfunction.SamplerContext;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The structured data provider for {@code DebugEntryChunkGeneration}.
 *
 * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
 * Name: <b>Yuantong Ren</b>
 * @version 1.0.0
 * @since 1.0.0
 */
public final class ChunkGenerationDebugEntry implements DebugEntryProvider {
    /**
     * The level associated with cached generation data, without retaining the world.
     */
    private WeakReference<ServerLevel> generationLevel = new WeakReference<>(null);

    /**
     * The camera position associated with cached generation data.
     */
    private BlockPos generationPosition;

    /**
     * Whether the cached generation data includes old-noise blending.
     */
    private boolean generationBlending;

    /**
     * The cached rows supplied by the generator and biome source.
     */
    private final List<DebugRow> generationRows = new ArrayList<>();

    /**
     * The destination of this entry's structured rows.
     */
    private final DebugEntryOutput output;

    /**
     * Create the data provider with the given row destination.
     *
     * @param output the destination of the collected rows
     */
    public ChunkGenerationDebugEntry(DebugEntryOutput output) {
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
        displayGeneration(level, serverChunk);
    }

    /**
     * Collect complete generator and biome-source summaries when the camera changes blocks.
     *
     * @param level       the level supplied by vanilla
     * @param serverChunk the cached server chunk
     */
    private void displayGeneration(Level level, LevelChunk serverChunk) {
        Entity camera = Minecraft.getInstance().getCameraEntity();
        if (camera == null || !(level instanceof ServerLevel server)) {
            return;
        }
        BlockPos position = camera.blockPosition();
        boolean blending = serverChunk != null && serverChunk.isOldNoiseGeneration();
        if (generationLevel.get() != server || !position.equals(generationPosition) || blending != generationBlending) {
            generationLevel = new WeakReference<>(server);
            generationPosition = position.immutable();
            generationBlending = blending;
            generationRows.clear();
            var source = server.getChunkSource();
            var context = SamplerContext.builder().enableCaches().build();
            var random = source.randomState();
            var generator = source.getGenerator();
            List<String> lines = new ArrayList<>();
            generator.addDebugScreenInfo(lines, random, position, context);
            for (String line : lines) {
                generationRows.add(new DebugRow(Component.translatable("clear_f3.row.generator"), Component.literal(line)));
            }
            lines.clear();
            generator.getBiomeSource().addDebugInfo(lines, position, random.createClimateSampler(context));
            for (String line : lines) {
                generationRows.add(new DebugRow(Component.translatable("clear_f3.row.biome_source"), Component.literal(line)));
            }
            if (blending) {
                generationRows.add(new DebugRow(Component.translatable("clear_f3.row.blending"),
                    Component.translatable("clear_f3.value.old_noise")));
            }
        }
        generationRows.forEach(row -> output.add(DebugGroups.CHUNK_GENERATION, row));
    }
}
//#endif

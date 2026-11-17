//#if MC>=260300
package io.github.piscescup.mc.clear_f3.client.debug.entry;

import io.github.piscescup.mc.clear_f3.client.debug.DebugGroups;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.Objects;

/**
 * The structured data provider for {@code DebugEntryLocalDifficulty}.
 *
 * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
 * Name: <b>Yuantong Ren</b>
 * @version 1.0.0
 * @since 1.0.0
 */
public final class LocalDifficultyDebugEntry implements DebugEntryProvider {
    /**
     * The destination of this entry's structured rows.
     */
    private final DebugEntryOutput output;

    /**
     * Create the data provider with the given row destination.
     *
     * @param output the destination of the collected rows
     */
    public LocalDifficultyDebugEntry(DebugEntryOutput output) {
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
        displayDifficulty(level, serverChunk);
    }

    /**
     * Display local difficulty only when vanilla server-side inputs are available.
     *
     * @param level       the level supplied by vanilla
     * @param serverChunk the cached server chunk
     */
    private void displayDifficulty(Level level, LevelChunk serverChunk) {
        Entity camera = Minecraft.getInstance().getCameraEntity();
        if (camera != null && serverChunk != null && level instanceof ServerLevel server
            && server.isInsideBuildHeight(camera.blockPosition().getY())) {
            DifficultyInstance difficulty = new DifficultyInstance(server.getDifficulty(), server.getOverworldClockTime(),
                serverChunk.getInhabitedTime(), server.getMoonBrightness(camera.blockPosition()));
            output.row(DebugGroups.POSITION, "local_difficulty", "%.2f / %.2f", difficulty.getEffectiveDifficulty(), difficulty.getSpecialMultiplier());
        }
    }
}
//#endif

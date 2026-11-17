//#if MC>=260300
package io.github.piscescup.mc.clear_f3.client.debug.entry;

import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

/**
 * The data collection contract of an independent {@code Clear F3} debug entry.
 *
 * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
 * Name: <b>Yuantong Ren</b>
 * @version 1.0.0
 * @since 1.0.0
 */
@FunctionalInterface
public interface DebugEntryProvider {
    /**
     * Collect rows for an entry that vanilla has already selected for display.
     *
     * @param entry the original vanilla entry
     * @param level the server or client level, or {@code null} outside a world
     * @param clientChunk the cached client chunk, or {@code null} when unavailable
     * @param serverChunk the cached server chunk, or {@code null} when unavailable
     */
    void display(DebugScreenEntry entry, Level level, LevelChunk clientChunk, LevelChunk serverChunk);
}
//#endif

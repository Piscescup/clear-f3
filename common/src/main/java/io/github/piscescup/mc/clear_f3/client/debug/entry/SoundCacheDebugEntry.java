//#if MC>=260300
package io.github.piscescup.mc.clear_f3.client.debug.entry;

import io.github.piscescup.mc.clear_f3.client.debug.DebugGroups;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.Objects;

/**
 * The structured data provider for {@code DebugEntrySoundCache}.
 *
 * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
 * Name: <b>Yuantong Ren</b>
 * @version 1.0.0
 * @since 1.0.0
 */
public final class SoundCacheDebugEntry implements DebugEntryProvider {
    /**
     * The number of bytes in one mebibyte.
     */
    private static final long MEBIBYTE = 1024L * 1024L;

    /**
     * The destination of this entry's structured rows.
     */
    private final DebugEntryOutput output;

    /**
     * Create the data provider with the given row destination.
     *
     * @param output the destination of the collected rows
     */
    public SoundCacheDebugEntry(DebugEntryOutput output) {
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
        displaySoundCache();
    }

    /**
     * Display the count and total size of cached sound buffers.
     */
    private void displaySoundCache() {
        SoundBufferLibrary.DebugOutput.Counter counter = new SoundBufferLibrary.DebugOutput.Counter();
        Minecraft.getInstance().getSoundManager().getSoundCacheDebugStats(counter);
        output.row(DebugGroups.SOUND, "sound_buffers", "%d", counter.totalCount());
        output.row(DebugGroups.SOUND, "sound_cache_size", "%d MiB", Mth.ceilLong((double) counter.totalSize() / MEBIBYTE));
    }
}
//#endif

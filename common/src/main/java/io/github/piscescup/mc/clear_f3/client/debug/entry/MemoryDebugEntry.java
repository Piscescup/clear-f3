//#if MC>=260300
package io.github.piscescup.mc.clear_f3.client.debug.entry;

import io.github.piscescup.mc.clear_f3.client.debug.DebugGroups;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Objects;

/**
 * The structured data provider for {@code DebugEntryMemory}.
 *
 * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
 * Name: <b>Yuantong Ren</b>
 * @version 1.0.0
 * @since 1.0.0
 */
public final class MemoryDebugEntry implements DebugEntryProvider {
    /**
     * The number of bytes in one mebibyte.
     */
    private static final long MEBIBYTE = 1024L * 1024L;

    /**
     * The garbage collectors used to reject interrupted allocation samples.
     */
    private static final List<GarbageCollectorMXBean> GARBAGE_COLLECTORS =
        ManagementFactory.getGarbageCollectorMXBeans();

    /**
     * The time of the previous allocation sample in nanoseconds.
     */
    private long lastSampleNanos;

    /**
     * The heap usage recorded by the previous allocation sample.
     */
    private long lastHeapUsage;

    /**
     * The collection count recorded by the previous allocation sample.
     */
    private long lastGcCount;

    /**
     * The most recent allocation estimate in bytes per second.
     */
    private long allocationRate;

    /**
     * The destination of this entry's structured rows.
     */
    private final DebugEntryOutput output;

    /**
     * Create the data provider with the given row destination.
     *
     * @param output the destination of the collected rows
     */
    public MemoryDebugEntry(DebugEntryOutput output) {
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
        displayMemory();
    }

    /**
     * Display heap usage and an allocation estimate sampled without garbage collection.
     */
    private void displayMemory() {
        Runtime runtime = Runtime.getRuntime();
        long maximum = runtime.maxMemory();
        long allocated = runtime.totalMemory();
        long used = allocated - runtime.freeMemory();
        output.row(DebugGroups.MEMORY, "heap_used", "%d%% %d / %d MiB", used * 100L / maximum, used / MEBIBYTE, maximum / MEBIBYTE);
        output.row(DebugGroups.MEMORY, "allocation_rate", "%d MiB/s", sampleAllocationRate(used) / MEBIBYTE);
        output.row(DebugGroups.MEMORY, "heap_allocated", "%d%% %d MiB", allocated * 100L / maximum, allocated / MEBIBYTE);
    }

    /**
     * Estimate allocation between samples with no intervening garbage collection.
     *
     * @param used the current heap usage in bytes
     * @return the allocation estimate in bytes per second
     */
    private long sampleAllocationRate(long used) {
        long now = System.nanoTime();
        long elapsed = now - lastSampleNanos;
        if (lastSampleNanos != 0 && elapsed < 500_000_000L) {
            return allocationRate;
        }
        long gcCount = GARBAGE_COLLECTORS.stream().mapToLong(bean -> Math.max(0L, bean.getCollectionCount())).sum();
        allocationRate = lastSampleNanos != 0 && elapsed <= 2_000_000_000L && gcCount == lastGcCount
            ? Math.max(0L, Math.round((used - lastHeapUsage) * 1_000_000_000.0 / elapsed)) : 0L;
        lastSampleNanos = now;
        lastHeapUsage = used;
        lastGcCount = gcCount;
        return allocationRate;
    }
}
//#endif

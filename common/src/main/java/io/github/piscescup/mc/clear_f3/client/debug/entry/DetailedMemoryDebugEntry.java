//#if MC>=260300
package io.github.piscescup.mc.clear_f3.client.debug.entry;

import io.github.piscescup.mc.clear_f3.client.debug.DebugGroups;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.Objects;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.debug.DebugEntryDetailedMemory;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

/**
 * The structured data provider for {@code DebugEntryDetailedMemory}.
 *
 * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
 * Name: <b>Yuantong Ren</b>
 * @version 1.0.0
 * @since 1.0.0
 */
public final class DetailedMemoryDebugEntry implements DebugEntryProvider {
    /**
     * The number of bytes in one mebibyte.
     */
    private static final long MEBIBYTE = 1024L * 1024L;

    /**
     * The bean supplying detailed heap and non-heap usage.
     */
    private final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

    /**
     * The destination of this entry's structured rows.
     */
    private final DebugEntryOutput output;

    /**
     * Create the data provider with the given row destination.
     *
     * @param output the destination of the collected rows
     */
    public DetailedMemoryDebugEntry(DebugEntryOutput output) {
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
        displayMemoryUsage("heap", memoryBean.getHeapMemoryUsage());
        displayMemoryUsage("non_heap", memoryBean.getNonHeapMemoryUsage());
    }

    /**
     * Display the four detailed memory counters without treating undefined values as zero.
     *
     * @param type  the heap or non-heap translation prefix
     * @param usage the memory usage reported by the virtual machine
     */
    private void displayMemoryUsage(String type, MemoryUsage usage) {
        output.row(DebugGroups.MEMORY, type + "_initial", memoryValue(usage.getInit()));
        output.row(DebugGroups.MEMORY, type + "_used_detail", memoryValue(usage.getUsed()));
        output.row(DebugGroups.MEMORY, type + "_committed", memoryValue(usage.getCommitted()));
        output.row(DebugGroups.MEMORY, type + "_maximum", memoryValue(usage.getMax()));
    }

    /**
     * Format a memory counter while preserving an undefined measurement.
     *
     * @param bytes the number of bytes, or a negative value when undefined
     * @return the formatted memory value
     */
    private Component memoryValue(long bytes) {
        return bytes < 0 ? Component.translatable("clear_f3.value.unavailable")
            : Component.literal(bytes / MEBIBYTE + " MiB").withStyle(ChatFormatting.WHITE);
    }
}
//#endif

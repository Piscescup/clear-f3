//#if MC>=260300
package io.github.piscescup.mc.clear_f3.client.debug.entry;

import io.github.piscescup.mc.clear_f3.client.debug.DebugGroups;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.Locale;
import java.util.Objects;

/**
 * The structured data provider for {@code DebugEntrySpawnCounts}.
 *
 * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
 * Name: <b>Yuantong Ren</b>
 * @version 1.0.0
 * @since 1.0.0
 */
public final class SpawnCountsDebugEntry implements DebugEntryProvider {
    /**
     * The destination of this entry's structured rows.
     */
    private final DebugEntryOutput output;

    /**
     * Create the data provider with the given row destination.
     *
     * @param output the destination of the collected rows
     */
    public SpawnCountsDebugEntry(DebugEntryOutput output) {
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
        displaySpawnCounts(level);
    }

    /**
     * Display spawnable chunk count and each mob category's current count.
     *
     * @param level the level supplied by vanilla
     */
    private void displaySpawnCounts(Level level) {
        if (Minecraft.getInstance().getCameraEntity() == null || !(level instanceof ServerLevel server)) {
            return;
        }
        NaturalSpawner.SpawnState spawn = server.getChunkSource().getLastSpawnState();
        if (spawn != null) {
            output.row(DebugGroups.SPAWN_COUNTS, "spawnable_chunks", "%d", spawn.getSpawnableChunkCount());
            for (MobCategory category : MobCategory.values()) {
                output.namedRow(DebugGroups.SPAWN_COUNTS, Component.translatableWithFallback("clear_f3.mob_category." + category.name().toLowerCase(Locale.ROOT), category.getName()),
                    Component.literal(Integer.toString(spawn.getMobCategoryCounts().getInt(category))).withStyle(ChatFormatting.WHITE));
            }
        }
    }
}
//#endif

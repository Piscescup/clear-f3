package io.github.piscescup.mc.clear_f3.client.debug;

import net.minecraft.network.chat.Component;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Row metadata available to settings before collection, plus runtime-discovered rows.
 * Displayed values are not retained and never form part of a setting identifier.
 *
 * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
 * Name: <b>Yuantong Ren</b>
 * @version 1.0.0
 * @since 1.0.0
 */
public final class DebugRowCatalog {
    private static final Map<String, LinkedHashMap<String, Entry>> ROWS = new LinkedHashMap<>();
    static {
        declare(DebugGroups.LEFT_PRIORITY, "fps", "frame_limit", "presentation", "refresh_rate");
        declare(DebugGroups.RIGHT_PRIORITY, "game_version", "client_brand");
        declare(DebugGroups.POSITION, "xyz", "block", "chunk", "section_relative", "facing", "dimension",
            "forced_chunks", "speed", "biome", "server_biome", "day", "local_difficulty");
        declare(DebugGroups.MEMORY, "heap_used", "allocation_rate", "heap_allocated", "heap_initial",
            "heap_used_detail", "heap_committed", "heap_maximum", "non_heap_initial", "non_heap_used_detail",
            "non_heap_committed", "non_heap_maximum");
        declare(DebugGroups.LIGHT, "client_light", "client_sky_light", "client_block_light", "server_light",
            "server_sky_light", "server_block_light");
        declare(DebugGroups.HEIGHTMAP, "client_heightmap_values", "server_heightmap_values");
        declare(DebugGroups.CHUNK_RENDERING, "section_statistics", "entity_statistics", "particles", "post_effect");
        declare(DebugGroups.CHUNK_SOURCE, "client_chunks", "server_chunks");
        declare(DebugGroups.CHUNK_GENERATION, "generator", "biome_source", "blending");
        declare(DebugGroups.SPAWN_COUNTS, "spawnable_chunks", "spawn_statistics");
        declare(DebugGroups.SYSTEM_SPECS, "java", "cpu", "display", "window", "pixel_density", "gpu_device",
            "graphics_backend", "graphics_driver");
        declare(DebugGroups.PERFORMANCE_IMPACTORS, "gpu_utilization", "transparency", "clouds", "biome_blend", "filtering", "terrain_rendering");
        declare(DebugGroups.SERVER, "server", "tick_state", "tick_time", "target_tick_time", "packets_sent", "packets_received");
        declare(DebugGroups.SOUND, "sound_channels", "sound_mood", "sound_buffers", "sound_cache_size");
        declare(DebugGroups.HELP, "help", "edit_debug");
    }

    private DebugRowCatalog() {}

    private static void declare(DebugGroup group, String... names) {
        for (String name : names) remember(group,
            new DebugRow(Component.translatable("clear_f3.row." + name), Component.empty()));
    }

    /** Keep one entry per stable key; continuation lines reuse the named row's metadata. */
    public static Entry remember(DebugGroup group, DebugRow row) {
        if (row.colorKey().isEmpty()) return new Entry("", row.label(), row.alignsWithValues());
        var entries = ROWS.computeIfAbsent(group.id().toString(), ignored -> new LinkedHashMap<>());
        Entry known = entries.get(row.colorKey());
        if (known == null || known.label().getString().isBlank() && row.hasLabel()) {
            known = new Entry(row.colorKey(), row.label().copy(), row.alignsWithValues());
            entries.put(row.colorKey(), known);
        }
        return known;
    }

    public static List<Entry> rows(DebugGroup group) {
        var entries = ROWS.get(group.id().toString());
        return entries == null ? List.of() : List.copyOf(entries.values());
    }

    /** A translated key and rendering policy, without retaining any live debug values. */
    public record Entry(String id, Component label, boolean valueAligned) {
        public boolean hasKey() { return !this.label.getString().isBlank(); }
        public Component title() {
            if (hasKey()) return this.label;
            String number = this.id.matches("line:[0-9]+")
                ? Integer.toString(Integer.parseInt(this.id.substring(5)) + 1) : this.id;
            return Component.translatable("clear_f3.gui.unnamed_row", number);
        }
    }
}

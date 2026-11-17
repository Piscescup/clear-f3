package io.github.piscescup.mc.clear_f3.client.debug;

import io.github.piscescup.mc.clear_f3.ClearF3References;
import io.github.piscescup.mc.clear_f3.client.debug.DebugGroup.Builder;
import io.github.piscescup.mc.clear_f3.client.debug.DebugGroup.Column;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/**
 * The shared, translatable group definitions for the {@code Clear F3} overlay.
 * <p>
 * Core colors and column preferences follow the vanilla {@code Minecraft 26.4}
 * groups. Additional groups retain the server, sound and chunk-cache output.
 *
 * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
 * Name: <b>Yuantong Ren</b>
 * @version 1.0.0
 * @since 1.0.0
 */
public final class DebugGroups {
    /**
     * The default RGB accent color for third-party debug groups.
     */
    public static final int DEFAULT_THIRD_MOD_DEBUG_COLOR = 0x55FFFF;

    /**
     * The group containing vanilla debug shortcut descriptions.
     */
    public static final DebugGroup HELP = Builder.titled(
        ClearF3References.id("help"), Component.translatable("clear_f3.group.help")
    ).build();

    /**
     * The titleless fallback for output without an entry or a group identifier.
     */
    public static final DebugGroup MISC = Builder.titleless(ClearF3References.id("miscellaneous")).build();

    /**
     * The titleless priority group at the top of the left column.
     */
    public static final DebugGroup LEFT_PRIORITY = Builder.titleless(ClearF3References.id("priority_left"))
        .withPreferredColumn(Column.LEFT).build();

    /**
     * The titleless priority group at the top of the right column.
     */
    public static final DebugGroup RIGHT_PRIORITY = Builder.titleless(ClearF3References.id("priority_right"))
        .withPreferredColumn(Column.RIGHT).build();

    /**
     * The group containing client and server light measurements.
     */
    public static final DebugGroup LIGHT = Builder.titled(
        ClearF3References.minecraftId("light"), Component.translatable("clear_f3.group.light")
    ).withAccentColor(0xFFFF00).withPreferredColumn(Column.LEFT).build();

    /**
     * The group containing the targeted block state and tags.
     */
    public static final DebugGroup LOOKING_AT_BLOCK = Builder.titled(
        ClearF3References.minecraftId("looking_at_block"), Component.translatable("clear_f3.group.looking_at_block")
    ).withAccentColor(0xCC00FF).withPreferredColumn(Column.RIGHT).build();

    /**
     * The group containing the targeted fluid state and tags.
     */
    public static final DebugGroup LOOKING_AT_FLUID = Builder.titled(
        ClearF3References.minecraftId("looking_at_fluid"), Component.translatable("clear_f3.group.looking_at_fluid")
    ).withAccentColor(0xFFCC00).withPreferredColumn(Column.RIGHT).build();

    /**
     * The group containing the targeted entity type and tags.
     */
    public static final DebugGroup LOOKING_AT_ENTITY = Builder.titled(
        ClearF3References.minecraftId("looking_at_entity"), Component.translatable("clear_f3.group.looking_at_entity")
    ).withAccentColor(0x00FFCC).withPreferredColumn(Column.RIGHT).build();

    /**
     * The group containing memory usage and allocation measurements.
     */
    public static final DebugGroup MEMORY = Builder.titled(
        ClearF3References.minecraftId("memory"), Component.translatable("clear_f3.group.memory")
    ).withAccentColor(0xFF9B00).withPreferredColumn(Column.RIGHT).build();

    /**
     * The group containing player position and related world measurements.
     */
    public static final DebugGroup POSITION = Builder.titled(
        ClearF3References.minecraftId("position"), Component.translatable("clear_f3.group.position")
    ).withAccentColor(0xFFFFFF).withPreferredColumn(Column.LEFT).build();

    /**
     * The group containing rendering statistics and post-processing information.
     */
    public static final DebugGroup CHUNK_RENDERING = Builder.titled(
        ClearF3References.minecraftId("rendering"), Component.translatable("clear_f3.group.chunk_render_stats")
    ).withAccentColor(0xF0B0A0).withPreferredColumn(Column.LEFT).build();

    /**
     * The group containing GPU utilization and graphics performance settings.
     */
    public static final DebugGroup PERFORMANCE_IMPACTORS = Builder.titled(
        ClearF3References.minecraftId("performance"), Component.translatable("clear_f3.group.simple_performance_impactors")
    ).withAccentColor(0x00FF00).withPreferredColumn(Column.RIGHT).build();

    /**
     * The group containing Java, processor, display and graphics specifications.
     */
    public static final DebugGroup SYSTEM_SPECS = Builder.titled(
        ClearF3References.minecraftId("system"), Component.translatable("clear_f3.group.system")
    ).withAccentColor(0xFF0000).withPreferredColumn(Column.RIGHT).build();

    /**
     * The group containing client and server heightmap measurements.
     */
    public static final DebugGroup HEIGHTMAP = Builder.titled(
        ClearF3References.minecraftId("heightmaps"), Component.translatable("clear_f3.group.heightmaps")
    ).withAccentColor(0x00AAFF).withPreferredColumn(Column.LEFT).build();

    /**
     * The group containing terrain-generator and biome-source diagnostics.
     */
    public static final DebugGroup CHUNK_GENERATION = Builder.titled(
        ClearF3References.minecraftId("chunk_generation"), Component.translatable("clear_f3.group.chunk_generation")
    ).withAccentColor(0x99FFAA).build();

    /**
     * The group containing spawnable chunks and the population of each mob category.
     */
    public static final DebugGroup SPAWN_COUNTS = Builder.titled(
        ClearF3References.minecraftId("spawn_counts"), Component.translatable("clear_f3.group.entity_spawn_counts")
    ).withAccentColor(0xFF4444).build();

    /**
     * The additional group containing client and server chunk-cache statistics.
     */
    public static final DebugGroup CHUNK_SOURCE = Builder.titled(
        ClearF3References.minecraftId("chunk_source"), Component.translatable("clear_f3.group.chunk_source")
    ).withAccentColor(0xF0B0A0).build();

    /**
     * The additional group containing sound channels, cached audio and ambient mood.
     */
    public static final DebugGroup SOUND = Builder.titled(
        ClearF3References.minecraftId("sound"), Component.translatable("clear_f3.group.sound")
    ).withAccentColor(0x00FFCC).build();

    /**
     * The additional group containing server tick timings and packet statistics.
     */
    public static final DebugGroup SERVER = Builder.titled(
        ClearF3References.minecraftId("server"), Component.translatable("clear_f3.group.server")
    ).withAccentColor(0x80B0FF).withPreferredColumn(Column.RIGHT).build();

    /**
     * The shared definitions indexed by group identifiers and legacy entry identifiers.
     */
    private static final Map<Identifier, DebugGroup> GROUPS = createLookup();

    /** Third-party definitions encountered through either vanilla integration path. */
    private static final Map<Identifier, DebugGroup> DISCOVERED = new LinkedHashMap<>();

    /**
     * The original heading prefixes used before vanilla supplied group identifiers.
     */
    private static final Map<String, DebugGroup> LEGACY_HEADINGS = createLegacyHeadings();

    /** Canonical panel order, independent of vanilla's collection order. */
    private static final List<DebugGroup> DISPLAY_ORDER = List.of(
        LEFT_PRIORITY, RIGHT_PRIORITY, CHUNK_RENDERING, POSITION, LIGHT, HEIGHTMAP,
        CHUNK_GENERATION, SPAWN_COUNTS, CHUNK_SOURCE, SOUND, MEMORY, SYSTEM_SPECS,
        PERFORMANCE_IMPACTORS, SERVER, LOOKING_AT_BLOCK, LOOKING_AT_FLUID, LOOKING_AT_ENTITY
    );

    /**
     * Resolve a common display order while preserving third-party insertion order.
     *
     * @param group the group being placed
     * @return the panel's ordering rank; miscellaneous text and help come last
     */
    static int displayOrder(DebugGroup group) {
        for (int i = 0; i < DISPLAY_ORDER.size(); i++) {
            if (DISPLAY_ORDER.get(i).id().equals(group.id())) return i;
        }
        if (group.id().equals(MISC.id())) return DISPLAY_ORDER.size() + 1;
        if (group.id().equals(HELP.id())) return DISPLAY_ORDER.size() + 2;
        return DISPLAY_ORDER.size();
    }

    /**
     * Prevent the creation of this constants class.
     */
    private DebugGroups() {
    }

    /** Remember dynamically supplied components so their colors can also be edited. */
    public static void remember(DebugGroup group) {
        if (!GROUPS.containsKey(group.id())) DISCOVERED.put(group.id(), group);
    }

    /** @return canonical components followed by third-party components seen in-game */
    public static List<DebugGroup> configurableGroups() {
        var groups = new java.util.ArrayList<>(DISPLAY_ORDER);
        groups.add(MISC);
        groups.add(HELP);
        groups.addAll(DISCOVERED.values());
        return List.copyOf(groups);
    }

    /** @return a readable settings label even for titleless priority components */
    public static Component settingsTitle(DebugGroup group) {
        if (!group.title().getString().isEmpty()) return group.title();
        if (group.id().getNamespace().equals(ClearF3References.MOD_ID)) {
            return Component.translatable("clear_f3.group." + group.id().getPath());
        }
        return Component.literal(group.id().toString());
    }

    /**
     * Find a shared group definition without combining third-party namespaces.
     *
     * @param id the group identifier or a known vanilla entry identifier
     * @return the shared group definition, or an empty optional for unknown output
     */
    public static Optional<DebugGroup> find(Identifier id) {
        return Optional.ofNullable(GROUPS.get(id));
    }

    /**
     * Check whether a group contains the state and registry tags of a target.
     *
     * @param group the group whose text is being adapted
     * @return {@code true} for a targeted block, fluid or entity group
     */
    public static boolean isTargetGroup(DebugGroup group) {
        return group.id().equals(LOOKING_AT_BLOCK.id())
            || group.id().equals(LOOKING_AT_FLUID.id())
            || group.id().equals(LOOKING_AT_ENTITY.id());
    }

    /**
     * Resolve an old vanilla heading without interpreting its displayed value.
     *
     * @param line the original line without formatting codes or leading whitespace
     * @return the matching group, or an empty optional for a continuation
     */
    static Optional<DebugGroup> findLegacyGroup(String line) {
        for (var heading : LEGACY_HEADINGS.entrySet()) {
            if (line.startsWith(heading.getKey())) {
                return Optional.of(heading.getValue());
            }
        }
        return Optional.empty();
    }

    /**
     * Collect legacy heading aliases alongside the shared group definitions.
     *
     * @return the lookup of original heading prefixes
     */
    private static Map<String, DebugGroup> createLegacyHeadings() {
        Map<String, DebugGroup> headings = new LinkedHashMap<>();
        addHeadings(headings, LOOKING_AT_BLOCK, "Targeted Block", "Looking at block");
        addHeadings(headings, LOOKING_AT_FLUID, "Targeted Fluid", "Looking at fluid");
        addHeadings(headings, LOOKING_AT_ENTITY, "Targeted Entity", "Looking at entity");
        addHeadings(headings, SYSTEM_SPECS, "Java:", "CPU:", "Display:");
        addHeadings(headings, MEMORY, "Mem:", "Allocation rate:", "Allocated:");
        addHeadings(headings, POSITION, "XYZ:", "Block:", "Chunk:", "Chunk-relative:", "Section-relative:", "Facing:", "Biome:", "Local Difficulty:");
        addHeadings(headings, LIGHT, "Client Light:", "Server Light:");
        addHeadings(headings, HEIGHTMAP, "CH ", "SH ");
        addHeadings(headings, SPAWN_COUNTS, "SC:", "Spawn");
        addHeadings(headings, HELP, "Debug:", "Debug charts:", "For help:", "F3 +");
        addHeadings(headings, CHUNK_RENDERING, "C:", "E:", "P:", "S:", "Shader:");
        addHeadings(headings, CHUNK_GENERATION, "NoiseRouter ", "Biome builder ");
        addHeadings(headings, CHUNK_SOURCE, "Client Chunk Cache:", "ServerChunkCache:");
        addHeadings(headings, SOUND, "Sounds:");
        addHeadings(headings, SERVER, "Integrated server @", "Multiplayer server @");
        return java.util.Collections.unmodifiableMap(headings);
    }

    /**
     * Register the original heading aliases for one group.
     *
     * @param headings the lookup being built
     * @param group the destination of matching lines
     * @param prefixes the original vanilla heading prefixes
     */
    private static void addHeadings(Map<String, DebugGroup> headings, DebugGroup group, String... prefixes) {
        for (String prefix : prefixes) {
            headings.put(prefix, group);
        }
    }

    /**
     * Index the definitions and aliases required by original string-based output.
     *
     * @return the read-only lookup of known groups and entry aliases
     */
    private static Map<Identifier, DebugGroup> createLookup() {
        Map<Identifier, DebugGroup> groups = new LinkedHashMap<>();
        for (DebugGroup group : List.of(HELP, MISC, LEFT_PRIORITY, RIGHT_PRIORITY, LIGHT,
            LOOKING_AT_BLOCK, LOOKING_AT_FLUID, LOOKING_AT_ENTITY, MEMORY, POSITION,
            CHUNK_RENDERING, PERFORMANCE_IMPACTORS, SYSTEM_SPECS, HEIGHTMAP,
            CHUNK_GENERATION, SPAWN_COUNTS, CHUNK_SOURCE, SOUND, SERVER)) {
            groups.put(group.id(), group);
        }
        addAliases(groups, LEFT_PRIORITY, "fps");
        addAliases(groups, RIGHT_PRIORITY, "game_version");
        addAliases(groups, POSITION, "player_position", "player_section_position", "player_speed", "biome", "day_count", "local_difficulty");
        addAliases(groups, MEMORY, "detailed_memory");
        addAliases(groups, LIGHT, "light_levels");
        addAliases(groups, SYSTEM_SPECS, "system_specs");
        addAliases(groups, HEIGHTMAP, "heightmap");
        addAliases(groups, CHUNK_RENDERING, "chunk_render_stats", "entity_render_stats", "particle_render_stats", "post_effect", "post_effects");
        addAliases(groups, CHUNK_SOURCE, "chunk_source_stats");
        addAliases(groups, CHUNK_GENERATION, "chunk_generation_stats");
        addAliases(groups, SPAWN_COUNTS, "entity_spawn_counts");
        addAliases(groups, PERFORMANCE_IMPACTORS, "gpu_utilization", "simple_performance_impactors");
        addAliases(groups, SOUND, "sound_mood", "sound_cache");
        addAliases(groups, SERVER, "tps");
        return Map.copyOf(groups);
    }

    /**
     * Associate original vanilla entry identifiers with a shared display group.
     *
     * @param groups the lookup being built
     * @param group the shared display definition
     * @param paths the vanilla entry paths that belong to the group
     */
    private static void addAliases(Map<Identifier, DebugGroup> groups, DebugGroup group, String... paths) {
        for (String path : paths) {
            groups.put(ClearF3References.minecraftId(path), group);
        }
    }
}

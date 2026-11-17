package io.github.piscescup.fabricmc.clear_f3.datagen.lang;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
//#if MC>=12005
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;
//#endif

/**
 * The American English translations generated for every supported loader and version.
 *
 * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
 * Name: <b>Yuantong Ren</b>
 * @version 1.0.0
 * @since 1.0.0
 */
public final class ENUSLanguageProvider extends FabricLanguageProvider {
    /**
     * Create the language provider for the shared resource pack.
     *
     * @param output the output supplied by Fabric
     * @param registries the registry lookup supplied by the data generator
     */
    //#if MC>=12005
    public ENUSLanguageProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, "en_us", registries);
    }
    //#else
    //$$ public ENUSLanguageProvider(FabricDataOutput output) {
    //$$     super(output, "en_us");
    //$$ }
    //#endif

    /**
     * Add the translations, preserving format arguments used by the shared overlay.
     *
     * @param registries the resolved registry lookup
     * @param translations the builder receiving translation keys and values
     */
    @Override
    //#if MC>=12005
    public void generateTranslations(HolderLookup.Provider registries, TranslationBuilder translations) {
    //#else
    //$$ public void generateTranslations(TranslationBuilder translations) {
    //#endif
        translations.add("clear_f3.gui.tab.colors", "Row colors");
        translations.add("clear_f3.gui.colors_hint", "Expand a group to set each row's key and value colors independently.");
        translations.add("clear_f3.gui.reset_component", "Reset this group");
        translations.add("clear_f3.gui.color_button", "%s: %s");
        translations.add("clear_f3.gui.expand_hint", "Hint: click [+] to expand");
        translations.add("clear_f3.gui.rows_hint", "Open F3 to discover additional rows in this group.");
        translations.add("clear_f3.gui.unnamed_row", "Entry %s");
        translations.add("clear_f3.color.key", "Key Color");
        translations.add("clear_f3.color.value", "Value Color");
        translations.add("clear_f3.gui.tab.general", "General");
        translations.add("clear_f3.gui.shortcuts", "Shortcuts");
        translations.add("clear_f3.gui.reset_colors", "Reset colors");
        translations.add("clear_f3.gui.expand_all", "Expand all");
        translations.add("clear_f3.gui.collapse_all", "Collapse all");
        translations.add("clear_f3.gui.edit_color", "Edit %s");
        translations.add("clear_f3.gui.hex_color", "Hex color");
        translations.add("clear_f3.gui.apply", "Apply");
        translations.add("clear_f3.gui.hex_hint", "RGB: #RRGGBB  ARGB: #AARRGGBB");
        translations.add("clear_f3.gui.hex_invalid", "Enter 6 RGB or 8 ARGB hexadecimal digits.");
        translations.add("clear_f3.group.priority_left", "Top-left information");
        translations.add("clear_f3.group.priority_right", "Top-right information");
        translations.add("clear_f3.group.miscellaneous", "Miscellaneous");
        translations.add("key.category.clear_f3.main", "Clear F3");
        translations.add("key.categories.clear_f3", "Clear F3");
        translations.add("key.clear_f3.toggle", "Toggle Clear F3");
        translations.add("key.clear_f3.open_settings", "Open Clear F3 Settings");
        translations.add("clear_f3.gui.title", "Clear F3 Settings");
        translations.add("clear_f3.gui.enabled", "Clear F3: %s");
        translations.add("clear_f3.gui.toggle_key", "Clear F3 Enable:");
        translations.add("clear_f3.gui.settings_key", "Config Screen:");
        translations.add("clear_f3.gui.press_key", "Press a key...");
        translations.add("clear_f3.gui.keybind_hint", "Click to change; press a key or mouse button. Esc clears the binding.");
        translations.add("clear_f3.message.enabled", "Clear F3 enabled");
        translations.add("clear_f3.message.disabled", "Clear F3 disabled (vanilla F3)");
        translations.add("clear_f3.group.position", "Position");
        translations.add("clear_f3.group.memory", "Memory");
        translations.add("clear_f3.group.light", "Light");
        translations.add("clear_f3.group.system", "System Specs");
        translations.add("clear_f3.group.looking_at_block", "Looking At Block");
        translations.add("clear_f3.group.looking_at_fluid", "Looking At Fluid");
        translations.add("clear_f3.group.looking_at_entity", "Looking At Entity");
        translations.add("clear_f3.group.heightmaps", "Heightmaps");
        translations.add("clear_f3.group.chunk_generation", "Chunk Generation");
        translations.add("clear_f3.group.chunk_render_stats", "Chunk Rendering");
        translations.add("clear_f3.group.chunk_source_stats", "Chunk Source");
        translations.add("clear_f3.group.entity_render_stats", "Entity Rendering");
        translations.add("clear_f3.group.particle_render_stats", "Particle Rendering");
        translations.add("clear_f3.group.entity_spawn_counts", "Entity Spawn Counts");
        translations.add("clear_f3.group.simple_performance_impactors", "Performance Impactors");
        translations.add("clear_f3.group.sound_mood", "Sound Mood");
        translations.add("clear_f3.group.sound_cache", "Sound Cache");
        translations.add("clear_f3.group.post_effects", "Post Effects");
        translations.add("clear_f3.group.help", "Debug Shortcuts");
        translations.add("clear_f3.row.xyz", "XYZ");
        translations.add("clear_f3.row.block", "Block");
        translations.add("clear_f3.row.chunk", "Chunk");
        translations.add("clear_f3.row.facing", "Facing");
        translations.add("clear_f3.row.dimension", "Dimension");
        translations.add("clear_f3.row.forced_chunks", "Forced Chunks");
        translations.add("clear_f3.row.heap_used", "Heap Used");
        translations.add("clear_f3.row.allocation_rate", "Allocation Rate");
        translations.add("clear_f3.row.heap_allocated", "Heap Allocated");
        translations.add("clear_f3.facing", "%s (%s) (%s)");
        translations.add("clear_f3.direction.north", "towards negative Z");
        translations.add("clear_f3.direction.south", "towards positive Z");
        translations.add("clear_f3.direction.west", "towards negative X");
        translations.add("clear_f3.direction.east", "towards positive X");
        translations.add("clear_f3.direction.up", "towards positive Y");
        translations.add("clear_f3.direction.down", "towards negative Y");
        translations.add("clear_f3.direction_name.north", "North");
        translations.add("clear_f3.direction_name.south", "South");
        translations.add("clear_f3.direction_name.west", "West");
        translations.add("clear_f3.direction_name.east", "East");
        translations.add("clear_f3.direction_name.up", "Up");
        translations.add("clear_f3.direction_name.down", "Down");
        translations.add("clear_f3.group.rendering", "Rendering");
        translations.add("clear_f3.group.chunk_source", "Chunk Source");
        translations.add("clear_f3.group.spawn_counts", "Mob Spawning");
        translations.add("clear_f3.group.performance", "Performance Impactors");
        translations.add("clear_f3.group.server", "Server and Network");
        translations.add("clear_f3.group.sound", "Sound");
        translations.add("clear_f3.row.section_relative", "Section-relative");
        translations.add("clear_f3.row.speed", "Speed");
        translations.add("clear_f3.row.biome", "Biome");
        translations.add("clear_f3.row.server_biome", "Server Biome");
        translations.add("clear_f3.row.day", "Day");
        translations.add("clear_f3.row.local_difficulty", "Local Difficulty");
        translations.add("clear_f3.row.heap_initial", "Heap Initial");
        translations.add("clear_f3.row.heap_used_detail", "Heap Used (MXBean)");
        translations.add("clear_f3.row.heap_committed", "Heap Committed");
        translations.add("clear_f3.row.heap_maximum", "Heap Maximum");
        translations.add("clear_f3.row.non_heap_initial", "Non-heap Initial");
        translations.add("clear_f3.row.non_heap_used_detail", "Non-heap Used");
        translations.add("clear_f3.row.non_heap_committed", "Non-heap Committed");
        translations.add("clear_f3.row.non_heap_maximum", "Non-heap Maximum");
        translations.add("clear_f3.row.java", "Java");
        translations.add("clear_f3.row.cpu", "CPU");
        translations.add("clear_f3.row.display", "Display");
        translations.add("clear_f3.row.window", "Window");
        translations.add("clear_f3.row.pixel_density", "Pixel Density");
        translations.add("clear_f3.row.gpu_device", "GPU");
        translations.add("clear_f3.row.graphics_backend", "Graphics Backend");
        translations.add("clear_f3.row.graphics_driver", "Graphics Driver");
        translations.add("clear_f3.row.fps", "FPS");
        translations.add("clear_f3.row.frame_limit", "Frame Limit");
        translations.add("clear_f3.row.presentation", "Presentation");
        translations.add("clear_f3.row.refresh_rate", "Refresh Rate");
        translations.add("clear_f3.row.game_version", "Minecraft");
        translations.add("clear_f3.row.client_brand", "Client");
        translations.add("clear_f3.row.server", "Server");
        translations.add("clear_f3.row.tick_state", "Tick State");
        translations.add("clear_f3.row.tick_time", "Tick Time");
        translations.add("clear_f3.row.target_tick_time", "Target Tick Time");
        translations.add("clear_f3.row.packets_sent", "Sent Packets");
        translations.add("clear_f3.row.packets_received", "Received Packets");
        translations.add("clear_f3.row.gpu_utilization", "GPU Utilization");
        translations.add("clear_f3.row.transparency", "Improved Transparency");
        translations.add("clear_f3.row.clouds", "Clouds");
        translations.add("clear_f3.row.biome_blend", "Biome Blend Radius");
        translations.add("clear_f3.row.filtering", "Texture Filtering");
        translations.add("clear_f3.row.terrain_rendering", "Terrain Rendering");
        translations.add("clear_f3.row.client_light", "Client Light");
        translations.add("clear_f3.row.client_sky_light", "Client Sky Light");
        translations.add("clear_f3.row.client_block_light", "Client Block Light");
        translations.add("clear_f3.row.server_sky_light", "Server Sky Light");
        translations.add("clear_f3.row.server_block_light", "Server Block Light");
        translations.add("clear_f3.row.client_heightmap", "Client %s");
        translations.add("clear_f3.row.server_heightmap", "Server %s");
        translations.add("clear_f3.row.section_statistics", "Sections");
        translations.add("clear_f3.row.entity_statistics", "Entities");
        translations.add("clear_f3.row.particles", "Particles");
        translations.add("clear_f3.row.client_chunks", "Client Chunks");
        translations.add("clear_f3.row.server_chunks", "Server Chunks");
        translations.add("clear_f3.row.generator", "Generator");
        translations.add("clear_f3.row.biome_source", "Biome Source");
        translations.add("clear_f3.row.blending", "Blending");
        translations.add("clear_f3.row.spawnable_chunks", "Spawnable Chunks");
        translations.add("clear_f3.row.target_position", "Position");
        translations.add("clear_f3.row.registry_id", "ID");
        translations.add("clear_f3.row.tags", "Tags");
        translations.add("clear_f3.row.sound_channels", "Sound Channels");
        translations.add("clear_f3.row.sound_mood", "Ambient Mood");
        translations.add("clear_f3.row.sound_buffers", "Cached Buffers");
        translations.add("clear_f3.row.sound_cache_size", "Cache Size");
        translations.add("clear_f3.row.post_effect", "Post Effect");
        translations.add("clear_f3.value.unavailable", "Unavailable");
        translations.add("clear_f3.value.unlimited", "Unlimited");
        translations.add("clear_f3.value.integrated_server", "Integrated Server");
        translations.add("clear_f3.value.running", "Running");
        translations.add("clear_f3.value.frozen", "Frozen");
        translations.add("clear_f3.value.stepping", "Frozen - Stepping");
        translations.add("clear_f3.value.sprinting", "Sprinting");
        translations.add("clear_f3.value.enabled", "Enabled");
        translations.add("clear_f3.value.disabled", "Disabled");
        translations.add("clear_f3.value.old_noise", "Old Generation");
        translations.add("clear_f3.heightmap.world_surface_wg", "World Surface (Generation)");
        translations.add("clear_f3.heightmap.world_surface", "World Surface");
        translations.add("clear_f3.heightmap.ocean_floor_wg", "Ocean Floor (Generation)");
        translations.add("clear_f3.heightmap.ocean_floor", "Ocean Floor");
        translations.add("clear_f3.heightmap.motion_blocking", "Motion Blocking");
        translations.add("clear_f3.heightmap.motion_blocking_no_leaves", "Motion Blocking (No Leaves)");
        translations.add("clear_f3.mob_category.monster", "Monsters");
        translations.add("clear_f3.mob_category.creature", "Creatures");
        translations.add("clear_f3.mob_category.ambient", "Ambient");
        translations.add("clear_f3.mob_category.axolotls", "Axolotls");
        translations.add("clear_f3.mob_category.underground_water_creature", "Underground Water Creatures");
        translations.add("clear_f3.mob_category.water_creature", "Water Creatures");
        translations.add("clear_f3.mob_category.water_ambient", "Water Ambient");
        translations.add("clear_f3.mob_category.misc", "Miscellaneous");
        translations.add("clear_f3.row.server_light", "Server Light");
        translations.add("clear_f3.row.client_heightmap_values", "Client Heightmaps");
        translations.add("clear_f3.row.server_heightmap_values", "Server Heightmaps");
        translations.add("clear_f3.row.spawn_statistics", "Spawn Counts");
        translations.add("clear_f3.row.help", "Help");
        translations.add("clear_f3.row.edit_debug", "Edit Debug Entries");
        translations.add("clear_f3.value.fast", "Fast");
        translations.add("clear_f3.value.fancy", "Fancy");
        translations.add("clear_f3.value.waiting_for_chunk", "Waiting for chunk...");
        translations.add("clear_f3.value.facing_angles", "%s (%s) %s");
        translations.add("clear_f3.value.chart_shortcut", "%s %s");
        translations.add("clear_f3.value.visible", "Visible");
        translations.add("clear_f3.value.hidden", "Hidden");
        translations.add("clear_f3.chart.profiler", "Profiler");
        translations.add("clear_f3.chart.fps_tps", "FPS + TPS");
        translations.add("clear_f3.chart.fps", "FPS");
        translations.add("clear_f3.chart.network", "Bandwidth + Ping");
        translations.add("clear_f3.chart.ping", "Ping");
        translations.add("clear_f3.chart.lightmap", "Lightmap");
    }
}


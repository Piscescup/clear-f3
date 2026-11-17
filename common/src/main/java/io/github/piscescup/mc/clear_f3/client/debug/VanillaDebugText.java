package io.github.piscescup.mc.clear_f3.client.debug;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The translation of original vanilla text into the shared structured rows.
 * <p>
 * Complete known formats are matched before their values are separated. Values
 * supplied by Minecraft remain strings, preserving precision, units and unknown
 * markers. Unrecognized output is left to the ordinary text adapter.
 *
 * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
 * Name: <b>Yuantong Ren</b>
 * @version 1.0.0
 * @since 1.0.0
 */
final class VanillaDebugText {
    /**
     * The original labels and their shared row translation names.
     */
    private static final Map<String, String> LABELS = Map.ofEntries(
        Map.entry("XYZ", "xyz"), Map.entry("Block", "block"), Map.entry("Chunk", "chunk"),
        Map.entry("Chunk-relative", "section_relative"), Map.entry("Section-relative", "section_relative"),
        Map.entry("Facing", "facing"), Map.entry("Biome", "biome"), Map.entry("Server Biome", "server_biome"),
        Map.entry("Local Difficulty", "local_difficulty"), Map.entry("Mem", "heap_used"),
        Map.entry("Allocation rate", "allocation_rate"), Map.entry("Allocated", "heap_allocated"),
        Map.entry("Java", "java"), Map.entry("CPU", "cpu"), Map.entry("Display", "display"),
        Map.entry("GPU", "gpu_utilization"), Map.entry("Filtering", "filtering"),
        Map.entry("Client Light", "client_light"), Map.entry("Server Light", "server_light"),
        Map.entry("Client Chunk Cache", "client_chunks"), Map.entry("ServerChunkCache", "server_chunks"),
        Map.entry("Shader", "post_effect"), Map.entry("Post", "post_effect"),
        Map.entry("Sounds", "sound_channels"), Map.entry("Sound cache", "sound_cache_size"),
        Map.entry("Blending", "blending")
    );

    /**
     * Complete vanilla formats whose values supply more than one structured row.
     */
    private static final Pattern FACING = Pattern.compile("(north|south|west|east|up|down) \\(Towards (negative|positive) ([XYZ])\\) (\\([^)]*\\))");
    private static final Pattern CLIENT_LIGHT = Pattern.compile("(.+) \\((.+) sky, (.+) block\\)");
    private static final Pattern SERVER_LIGHT = Pattern.compile("\\((.+) sky, (.+) block\\)");
    private static final Pattern DIFFICULTY_DAY = Pattern.compile("(.+) \\(Day (\\d+)\\)");
    private static final Pattern SOUND_MOOD = Pattern.compile("(.+) \\(Mood (\\d+%)\\)");
    private static final Pattern SOUND_CACHE = Pattern.compile("(\\d+) buffers, (\\d+ MiB)");
    private static final Pattern DETAILED_MEMORY = Pattern.compile("Memory \\((heap|non-heap)\\): i=(\\d+MiB) u=(\\d+MiB) c=(\\d+MiB) m=(\\d+MiB)");
    private static final Pattern FPS = Pattern.compile("(\\d+) fps T: (\\S+)(.*)");
    private static final Pattern VERSION = Pattern.compile("Minecraft (.+) \\((.+)/(.+)\\)");
    private static final Pattern DIMENSION = Pattern.compile("([a-z0-9_.-]+:[a-z0-9_./-]+) FC: (\\d+)");
    private static final Pattern SERVER = Pattern.compile("Integrated server @ (.+) ms(?: ticks)?, ([0-9.]+) tx, ([0-9.]+) rx");
    private static final Pattern SERVER_STATE = Pattern.compile("Integrated server @ (.+) ms \\((frozen - stepping|frozen|sprinting)\\), ([0-9.]+) tx, ([0-9.]+) rx");
    private static final Pattern REMOTE_SERVER = Pattern.compile("\"(.+)\" server(?: \\((frozen - stepping|frozen|sprinting)\\))?, ([0-9.]+) tx, ([0-9.]+) rx");
    private static final Pattern PERFORMANCE = Pattern.compile("(improved-transparency)?\\s*(fast-clouds|fancy-clouds)?\\s*B: (\\d+)");
    private static final Pattern CHART = Pattern.compile("(\\[[^]]+]) (Profiler|FPS \\+ TPS|FPS|Bandwidth \\+ Ping|Ping|Lightmap) (visible|hidden);?");
    private static final Pattern OLD_CHARTS = Pattern.compile("Debug: Pie \\[shift]: (visible|hidden) (FPS(?: \\+ TPS)?) \\[alt]: (visible|hidden)");

    /**
     * Prevent the creation of this utility class.
     */
    private VanillaDebugText() {
    }

    /**
     * Convert one known vanilla line while keeping unknown formats untouched.
     *
     * @param destination the resolved vanilla group
     * @param line the original text, including formatting codes
     * @return {@code true} if the line was converted to structured rows
     */
    static boolean append(DebugGroupContents destination, String line) {
        DebugRow parsed = DebugTextParser.parse(line);
        String plain = DebugTextParser.formattedText(line).getString().strip();
        DebugGroup group = destination.group();
        if (group == DebugGroups.HELP) {
            return appendHelp(destination, plain);
        }
        if (DebugGroups.isTargetGroup(group)) {
            if (parsed.hasLabel() && (parsed.label().getString().equals("Targeted Block")
                || parsed.label().getString().equals("Targeted Fluid"))) {
                row(destination, "target_position", parsed.value());
                return true;
            }
            if (plain.equals("Targeted Entity")) {
                return true; // The translatable group title already identifies the target.
            }
            if (!plain.startsWith("#") && plain.contains(":") && Identifier.tryParse(plain) != null) {
                row(destination, "registry_id", DebugTextParser.formattedText(line));
                return true;
            }
            return false; // State property names and colors are supplied by the block or fluid.
        }
        Matcher match = DETAILED_MEMORY.matcher(plain);
        if (group == DebugGroups.MEMORY && match.matches()) {
            String prefix = match.group(1).equals("heap") ? "heap_" : "non_heap_";
            String[] names = {"initial", "used_detail", "committed", "maximum"};
            for (int index = 0; index < names.length; index++) row(destination, prefix + names[index], match.group(index + 2));
            return true;
        }
        match = FPS.matcher(plain);
        if ((group == DebugGroups.LEFT_PRIORITY || group == DebugGroups.RIGHT_PRIORITY) && match.matches()) {
            row(destination, "fps", match.group(1));
            row(destination, "frame_limit", match.group(2).equals("inf")
                ? Component.translatable("clear_f3.value.unlimited") : Component.literal(match.group(2)));
            if (!match.group(3).isBlank()) row(destination, "presentation", match.group(3).strip());
            return true;
        }
        match = VERSION.matcher(plain);
        if ((group == DebugGroups.LEFT_PRIORITY || group == DebugGroups.RIGHT_PRIORITY) && match.matches()) {
            row(destination, "game_version", match.group(1));
            row(destination, "client_brand", match.group(2) + "/" + match.group(3));
            return true;
        }
        match = DIMENSION.matcher(plain);
        if (group == DebugGroups.POSITION && match.matches()) {
            row(destination, "dimension", match.group(1));
            row(destination, "forced_chunks", match.group(2));
            return true;
        }
        if (group == DebugGroups.POSITION && plain.matches("Day #\\d+")) {
            row(destination, "day", plain.substring("Day #".length()));
            return true;
        }
        if (group == DebugGroups.SERVER && appendServer(destination, plain)) return true;
        match = PERFORMANCE.matcher(plain);
        if (group == DebugGroups.PERFORMANCE_IMPACTORS && match.matches()) {
            row(destination, "transparency", Component.translatable("clear_f3.value." + (match.group(1) == null ? "disabled" : "enabled")));
            row(destination, "clouds", Component.translatable("clear_f3.value." + (match.group(2) == null ? "disabled" : match.group(2).equals("fast-clouds") ? "fast" : "fancy")));
            row(destination, "biome_blend", match.group(3));
            return true;
        }
        if (group == DebugGroups.HEIGHTMAP && (plain.startsWith("CH ") || plain.startsWith("SH "))) {
            row(destination, plain.startsWith("CH ") ? "client_heightmap_values" : "server_heightmap_values", plain.substring(3));
            return true;
        }
        if (group == DebugGroups.CHUNK_GENERATION) {
            if (plain.startsWith("NoiseRouter ")) {
                row(destination, "generator", plain.substring("NoiseRouter ".length()));
                return true;
            }
            if (plain.startsWith("Biome builder ")) {
                row(destination, "biome_source", plain.substring("Biome builder ".length()));
                return true;
            }
        }
        if (plain.equals("Waiting for chunk...")) {
            destination.addRow(new DebugRow(Component.empty(), Component.translatable("clear_f3.value.waiting_for_chunk")));
            return true;
        }
        if (!parsed.hasLabel()) return false;
        String label = parsed.label().getString();
        String value = parsed.value().getString();
        if (group == DebugGroups.CHUNK_RENDERING) {
            String name = switch (label) {
                case "C" -> "section_statistics";
                case "E" -> "entity_statistics";
                case "P" -> "particles";
                default -> null;
            };
            if (name != null) {
                row(destination, name, parsed.value());
                return true;
            }
        }
        if (group == DebugGroups.SPAWN_COUNTS && label.equals("SC")) {
            // The remaining vanilla counters retain their original abbreviations and order.
            row(destination, "spawn_statistics", parsed.value());
            return true;
        }
        String name = LABELS.get(label);
        if (name == null) return false;
        match = FACING.matcher(value);
        if (label.equals("Facing") && match.matches()) {
            Component direction = Component.translatable("clear_f3.direction_name." + match.group(1));
            Component axis = Component.translatable("clear_f3.direction." + match.group(1));
            row(destination, name, Component.translatable("clear_f3.value.facing_angles", direction, axis, match.group(4)));
            return true;
        }
        match = CLIENT_LIGHT.matcher(value);
        if (label.equals("Client Light") && match.matches()) {
            row(destination, name, match.group(1));
            row(destination, "client_sky_light", match.group(2));
            row(destination, "client_block_light", match.group(3));
            return true;
        }
        match = SERVER_LIGHT.matcher(value);
        if (label.equals("Server Light") && match.matches()) {
            row(destination, "server_sky_light", match.group(1));
            row(destination, "server_block_light", match.group(2));
            return true;
        }
        match = DIFFICULTY_DAY.matcher(value);
        if (label.equals("Local Difficulty") && match.matches()) {
            row(destination, name, match.group(1));
            row(destination, "day", match.group(2));
            return true;
        }
        match = SOUND_MOOD.matcher(value);
        if (label.equals("Sounds") && match.matches()) {
            row(destination, name, match.group(1));
            row(destination, "sound_mood", match.group(2));
            return true;
        }
        match = SOUND_CACHE.matcher(value);
        if (label.equals("Sound cache") && match.matches()) {
            row(destination, "sound_buffers", match.group(1));
            row(destination, "sound_cache_size", match.group(2));
            return true;
        }
        row(destination, name, label.equals("Blending") && value.equals("Old")
            ? Component.translatable("clear_f3.value.old_noise") : parsed.value());
        return true;
    }

    /**
     * Separate vanilla server timings and packet counts using complete known formats.
     *
     * @param destination the server group
     * @param line the visible vanilla line
     * @return whether a complete server format was recognized
     */
    private static boolean appendServer(DebugGroupContents destination, String line) {
        Matcher match = REMOTE_SERVER.matcher(line);
        if (match.matches()) {
            row(destination, "server", match.group(1));
            if (match.group(2) != null) row(destination, "tick_state", state(match.group(2)));
            row(destination, "packets_sent", match.group(3));
            row(destination, "packets_received", match.group(4));
            return true;
        }
        match = SERVER_STATE.matcher(line);
        boolean hasState = match.matches();
        if (!hasState) {
            match = SERVER.matcher(line);
            if (!match.matches()) return false;
        }
        row(destination, "server", Component.translatable("clear_f3.value.integrated_server"));
        String[] times = match.group(1).split("/", -1);
        if (times.length == 2) {
            row(destination, "tick_time", times[0] + " ms");
            row(destination, "target_tick_time", times[1].equals("-") ? Component.translatable("clear_f3.value.unavailable") : Component.literal(times[1] + " ms"));
        } else row(destination, "tick_time", match.group(1) + " ms");
        row(destination, "tick_state", state(hasState ? match.group(2) : "running"));
        row(destination, "packets_sent", match.group(hasState ? 3 : 2));
        row(destination, "packets_received", match.group(hasState ? 4 : 3));
        return true;
    }

    /**
     * Translate the known state name of the vanilla tick manager.
     *
     * @param name the original state name
     * @return the translated state component
     */
    private static Component state(String name) {
        return Component.translatable("clear_f3.value." + (name.equals("frozen - stepping") ? "stepping" : name));
    }

    /**
     * Translate chart descriptions while retaining the actual shortcuts supplied by vanilla.
     *
     * @param destination the help group
     * @param line the visible shortcut description
     * @return whether the entire description was recognized
     */
    private static boolean appendHelp(DebugGroupContents destination, String line) {
        if (line.startsWith("For help: press ")) {
            row(destination, "help", line.substring("For help: press ".length()));
            return true;
        }
        if (line.startsWith("To edit: press ")) {
            row(destination, "edit_debug", line.substring("To edit: press ".length()));
            return true;
        }
        Matcher old = OLD_CHARTS.matcher(line);
        if (old.matches()) {
            chart(destination, "Shift+F3", "Profiler", old.group(1));
            chart(destination, "Alt+F3", old.group(2), old.group(3));
            return true;
        }
        String description = line.startsWith("Debug charts: ") ? line.substring("Debug charts: ".length()) : line;
        Matcher charts = CHART.matcher(description);
        int end = 0;
        while (charts.find()) {
            if (!description.substring(end, charts.start()).isBlank()) return false;
            end = charts.end();
        }
        if (end == 0 || !description.substring(end).isBlank()) return false;
        charts.reset();
        while (charts.find()) chart(destination, charts.group(1), charts.group(2), charts.group(3));
        return true;
    }

    /**
     * Add one chart's shortcut and current visibility to the help group.
     *
     * @param destination the help group
     * @param shortcut the original shortcut text
     * @param chart the original chart name
     * @param visibility the original visibility state
     */
    private static void chart(DebugGroupContents destination, String shortcut, String chart, String visibility) {
        String name = switch (chart) {
            case "Profiler" -> "profiler";
            case "FPS + TPS" -> "fps_tps";
            case "Bandwidth + Ping" -> "network";
            case "Ping" -> "ping";
            case "Lightmap" -> "lightmap";
            default -> "fps";
        };
        destination.addRow(new DebugRow(Component.translatable("clear_f3.chart." + name),
            Component.translatable("clear_f3.value.chart_shortcut", shortcut, Component.translatable("clear_f3.value." + visibility))));
    }

    /**
     * Add an unchanged vanilla value under its translated row name.
     *
     * @param destination the destination group
     * @param name the shared row translation name
     * @param value the unchanged visible value
     */
    private static void row(DebugGroupContents destination, String name, String value) {
        row(destination, name, Component.literal(value));
    }

    /**
     * Add a styled or translated value under its translated row name.
     *
     * @param destination the destination group
     * @param name the shared row translation name
     * @param value the component retaining styles and translation arguments
     */
    private static void row(DebugGroupContents destination, String name, Component value) {
        destination.addRow(new DebugRow(Component.translatable("clear_f3.row." + name), value));
    }
}

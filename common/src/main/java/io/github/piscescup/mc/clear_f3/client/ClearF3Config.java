package io.github.piscescup.mc.clear_f3.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import io.github.piscescup.mc.clear_f3.ClearF3References;
import io.github.piscescup.mc.clear_f3.client.debug.DebugColorRole;
import io.github.piscescup.mc.clear_f3.client.debug.DebugGroup;
import io.github.piscescup.mc.clear_f3.client.debug.DebugRowCatalog;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Persistent client settings, with separate key and value colors for each row.
 *
 * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
 * Name: <b>Yuantong Ren</b>
 * @version 1.0.0
 * @since 1.0.0
 */
public final class ClearF3Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static Path file;
    private static boolean enabled = true;
    private static final Map<String, Map<String, EnumMap<DebugColorRole, Integer>>> COLORS = new LinkedHashMap<>();

    private ClearF3Config() {
    }

    /**
     * Load client settings from the mod's subdirectory in the active instance's config directory.
     */
    public static void load(Path directory) {
        String filename = ClearF3References.MOD_ID + ".json";
        file = directory.resolve(ClearF3References.MOD_ID).resolve(filename);
        enabled = true;
        COLORS.clear();
        if (!Files.exists(file)) {
            save();
            return;
        }
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            JsonObject config = GSON.fromJson(reader, JsonObject.class);
            if (config == null) throw new JsonParseException("Expected a settings object");
            var value = config.get("enabled");
            if (value != null) {
                if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean()) enabled = value.getAsBoolean();
                else ClearF3References.LOGGER.warn("Invalid Clear F3 enabled setting: {}", value);
            }
            var groups = config.get("colors");
            if (groups == null) return;
            if (!groups.isJsonObject()) {
                ClearF3References.LOGGER.warn("Invalid Clear F3 colors setting: expected an object");
                return;
            }
            for (var group : groups.getAsJsonObject().entrySet()) {
                if (!group.getValue().isJsonObject()) {
                    ClearF3References.LOGGER.warn("Ignoring invalid Clear F3 color group: {}", group.getKey());
                    continue;
                }
                for (var row : group.getValue().getAsJsonObject().entrySet()) {
                    if (!row.getValue().isJsonObject()) {
                        ClearF3References.LOGGER.warn("Ignoring invalid Clear F3 color row: {}/{}", group.getKey(), row.getKey());
                        continue;
                    }
                    for (var setting : row.getValue().getAsJsonObject().entrySet()) {
                        var role = DebugColorRole.fromKey(setting.getKey());
                        var colorValue = setting.getValue();
                        if (role.isEmpty() || !colorValue.isJsonPrimitive() || !colorValue.getAsJsonPrimitive().isString()) {
                            ClearF3References.LOGGER.warn("Ignoring invalid Clear F3 row color: {}/{}/{}",
                                group.getKey(), row.getKey(), setting.getKey());
                            continue;
                        }
                        var color = DebugColorRole.parse(colorValue.getAsString());
                        if (color.isEmpty()) {
                            ClearF3References.LOGGER.warn("Ignoring invalid Clear F3 row color: {}/{}/{}",
                                group.getKey(), row.getKey(), setting.getKey());
                            continue;
                        }
                        COLORS.computeIfAbsent(group.getKey(), ignored -> new LinkedHashMap<>())
                            .computeIfAbsent(row.getKey(), ignored -> new EnumMap<>(DebugColorRole.class))
                            .put(role.get(), color.get());
                    }
                }
            }
        } catch (IOException | JsonParseException | IllegalStateException exception) {
            ClearF3References.LOGGER.warn("Could not read Clear F3 settings from {}; using defaults", file, exception);
        }
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static boolean toggle() {
        setEnabled(!enabled);
        return enabled;
    }

    public static void setEnabled(boolean value) {
        if (enabled != value) {
            enabled = value;
            save();
        }
    }

    private static EnumMap<DebugColorRole, Integer> overrides(DebugGroup group, DebugRowCatalog.Entry row) {
        var rows = COLORS.get(group.id().toString());
        return rows == null ? null : rows.get(row.id());
    }

    /**
     * Query one row only; another row in the same group is unaffected.
     */
    public static int color(DebugGroup group, DebugRowCatalog.Entry row, DebugColorRole role) {
        var colors = overrides(group, row);
        return colors == null ? role.defaultColor(row) : colors.getOrDefault(role, role.defaultColor(row));
    }

    public static boolean hasColor(DebugGroup group, DebugRowCatalog.Entry row, DebugColorRole role) {
        var colors = overrides(group, row);
        return colors != null && colors.containsKey(role);
    }

    public static void setColor(DebugGroup group, DebugRowCatalog.Entry row, DebugColorRole role, int color) {
        COLORS.computeIfAbsent(group.id().toString(), ignored -> new LinkedHashMap<>())
            .computeIfAbsent(row.id(), ignored -> new EnumMap<>(DebugColorRole.class)).put(role, color);
        save();
    }

    public static void resetColor(DebugGroup group, DebugRowCatalog.Entry row, DebugColorRole role) {
        var colors = overrides(group, row);
        if (colors != null && colors.remove(role) != null) {
            prune(group, row);
            save();
        }
    }

    private static void prune(DebugGroup group, DebugRowCatalog.Entry row) {
        var rows = COLORS.get(group.id().toString());
        var colors = rows.get(row.id());
        if (colors != null && colors.isEmpty()) rows.remove(row.id());
        if (rows.isEmpty()) COLORS.remove(group.id().toString());
    }

    /**
     * Restore both colors of one row in a single write.
     */
    public static void resetColors(DebugGroup group, DebugRowCatalog.Entry row) {
        var rows = COLORS.get(group.id().toString());
        if (rows != null && rows.remove(row.id()) != null) {
            if (rows.isEmpty()) COLORS.remove(group.id().toString());
            save();
        }
    }

    public static void resetColors(DebugGroup group) {
        if (COLORS.remove(group.id().toString()) != null) save();
    }

    public static void resetColors() {
        if (!COLORS.isEmpty()) {
            COLORS.clear();
            save();
        }
    }

    /** Save only overrides, retaining readable group and row identifiers as JSON member names. */
    private static void save() {
        if (file == null) return;
        JsonObject config = new JsonObject();
        config.addProperty("enabled", enabled);
        JsonObject groups = new JsonObject();
        COLORS.forEach((group, rows) -> {
            JsonObject entries = new JsonObject();
            rows.forEach((row, colors) -> {
                JsonObject values = new JsonObject();
                colors.forEach((role, color) -> values.addProperty(role.key(), DebugColorRole.format(color)));
                entries.add(row, values);
            });
            groups.add(group, entries);
        });
        config.add("colors", groups);
        try {
            Files.createDirectories(file.getParent());
            try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                GSON.toJson(config, writer);
                writer.write(System.lineSeparator());
            }
        } catch (IOException exception) {
            ClearF3References.LOGGER.warn("Could not save Clear F3 settings to {}", file, exception);
        }
    }
}

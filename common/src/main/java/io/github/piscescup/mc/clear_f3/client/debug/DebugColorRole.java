package io.github.piscescup.mc.clear_f3.client.debug;

import java.util.Locale;
import java.util.Optional;

/**
 * Independently configurable key and value colors of a single debug row.
 *
 * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
 * Name: <b>Yuantong Ren</b>
 * @version 1.0.0
 * @since 1.0.0
 */
public enum DebugColorRole {
    KEY("key"), VALUE("value");

    private final String key;
    DebugColorRole(String key) { this.key = key; }
    public String key() { return this.key; }
    public String translationKey() { return "clear_f3.color." + this.key; }

    /** Preserve the original colors of named values and unnamed full-width text. */
    public int defaultColor(DebugRowCatalog.Entry row) {
        return this == KEY || !row.valueAligned() ? 0xFFE0E0E0 : 0xFFD0D0D0;
    }

    public static Optional<DebugColorRole> fromKey(String key) {
        for (DebugColorRole role : values()) if (role.key.equals(key)) return Optional.of(role);
        return Optional.empty();
    }

    /** Parse RGB or ARGB input, retaining transparent and black values. */
    public static Optional<Integer> parse(String input) {
        String hex = input.trim();
        if (hex.startsWith("#")) hex = hex.substring(1);
        if (!hex.matches("[0-9a-fA-F]{6}([0-9a-fA-F]{2})?")) return Optional.empty();
        int value = (int) Long.parseLong(hex, 16);
        return Optional.of(hex.length() == 6 ? 0xFF000000 | value : value);
    }

    public static String format(int color) { return String.format(Locale.ROOT, "#%08X", color); }
}

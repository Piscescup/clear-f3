//#if MC>=260300
package io.github.piscescup.mc.clear_f3.client.debug.entry;

import io.github.piscescup.mc.clear_f3.client.debug.DebugRow;
import io.github.piscescup.mc.clear_f3.client.debug.DebugGroup;
import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

/**
 * The structured row destination shared by independent debug entry providers.
 *
 * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
 * Name: <b>Yuantong Ren</b>
 * @version 1.0.0
 * @since 1.0.0
 */
@FunctionalInterface
public interface DebugEntryOutput {
    /**
     * Append a row to the given display group.
     *
     * @param group the shared display definition
     * @param row the row to append
     */
    void add(DebugGroup group, DebugRow row);

    /**
     * Append a translated name and a root-locale formatted value.
     *
     * @param group the shared display definition
     * @param name the translation suffix of the row name
     * @param format the format string of the value
     * @param values the values to format
     */
    default void row(DebugGroup group, String name, String format, Object... values) {
        row(group, name, Component.literal(String.format(Locale.ROOT, format, values)).withStyle(ChatFormatting.WHITE));
    }

    /**
     * Append a translated name and a styled value without discarding its formatting.
     *
     * @param group the shared display definition
     * @param name the translation suffix of the row name
     * @param value the styled value to display
     */
    default void row(DebugGroup group, String name, Component value) {
        namedRow(group, Component.translatable("clear_f3.row." + name), value);
    }

    /**
     * Append a translated name and a value that may be shortened to fit its column.
     * <p>
     * The complete value remains in the row. Only its rendered lines may be
     * shortened, so a long tag list cannot hide the other rows in its group.
     *
     * @param group the shared display definition
     * @param name the translation suffix of the row name
     * @param value the complete styled value to display
     */
    default void collapsibleRow(DebugGroup group, String name, Component value) {
        add(group,
            new DebugRow(Component.translatable("clear_f3.row." + name), value, true));
    }

    /**
     * Append a row whose label may come from a property or a registry.
     *
     * @param group the shared display definition
     * @param name the name of the row
     * @param value the styled value to display
     */
    default void namedRow(DebugGroup group, Component name, Component value) {
        add(group, new DebugRow(name, value));
    }

}
//#endif

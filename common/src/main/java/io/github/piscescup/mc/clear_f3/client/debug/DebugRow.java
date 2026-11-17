package io.github.piscescup.mc.clear_f3.client.debug;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;

import java.util.Objects;

/**
 * A named value, a continuation or a full-width text line in a {@link DebugGroup}.
 *
 * @param label the name without a trailing colon, or an empty component for unnamed text
 * @param value the value with its text styles
 * @param collapsible whether a long value may end with an ellipsis when space is limited
 * @param continuation whether unnamed text aligns with the group's named values
 * @param colorKey a stable setting identifier independent of the displayed value
 * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
 * Name: <b>Yuantong Ren</b>
 * @version 1.0.0
 * @since 1.0.0
 */
public record DebugRow(Component label, Component value, boolean collapsible, boolean continuation, String colorKey) {
    /**
     * Create a new {@link DebugRow} whose value is always displayed in full.
     *
     * @param label the name of the value
     * @param value the value with its text styles
     */
    public DebugRow(Component label, Component value) {
        this(label, value, false);
    }

    /**
     * Create a named value or full-width row with optional value shortening.
     *
     * @param label the name of the value, or an empty component for full-width text
     * @param value the value with its text styles
     * @param collapsible whether a long value may be shortened to fit its column
     */
    public DebugRow(Component label, Component value, boolean collapsible) {
        this(label, value, collapsible, false);
    }

    /** Derive the setting identifier from the untranslated label, never its value. */
    public DebugRow(Component label, Component value, boolean collapsible, boolean continuation) {
        this(label, value, collapsible, continuation, labelKey(label));
    }

    /** Retain a parent's setting identifier for explicit continuation lines. */
    public DebugRow withColorKey(String key) {
        return new DebugRow(this.label, this.value, this.collapsible, this.continuation, key);
    }

    private static String labelKey(Component label) {
        if (label.getContents() instanceof TranslatableContents translated) {
            StringBuilder key = new StringBuilder(translated.getKey());
            for (Object argument : translated.getArgs()) {
                String part = argument instanceof Component component ? labelKey(component) : String.valueOf(argument);
                key.append('|').append(part.length()).append(':').append(part);
            }
            return key.toString();
        }
        return label.getString().isBlank() ? "" : "literal:" + label.getString();
    }

    /**
     * Create a new {@link DebugRow} with the given name and value.
     *
     * @param label the name of the value
     * @param value the value with its text styles
     * @param collapsible whether a long value may be shortened to fit its column
     * @param continuation whether unnamed text aligns with the group's named values
     * @param colorKey the stable identifier used by row color settings
     */
    public DebugRow {
        Objects.requireNonNull(label, "label");
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(colorKey, "colorKey");
    }

    /**
     * Check whether this row supplies a name for the group's shared name column.
     *
     * @return {@code true} if the row has a nonempty label
     */
    public boolean hasLabel() {
        return !label.getString().isEmpty();
    }

    /**
     * Check whether this row starts its text in the shared value column.
     *
     * @return {@code true} for named values and their unnamed continuations
     */
    public boolean alignsWithValues() {
        return hasLabel() || continuation;
    }
}

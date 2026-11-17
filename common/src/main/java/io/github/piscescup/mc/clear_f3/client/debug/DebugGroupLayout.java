package io.github.piscescup.mc.clear_f3.client.debug;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import java.util.ArrayList;
import java.util.List;

/**
 * Styled text measured for a single debug panel on every supported game version.
 * Names, continuations and explicit newlines share the same wrapping rules.
 *
 * @param group the panel definition
 * @param width the measured panel width
 * @param height the measured panel height
 * @param labelWidth the shared name-column width
 * @param title the wrapped title
 * @param rows the wrapped content
 * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
 * Name: <b>Yuantong Ren</b>
 * @since 1.0.0
 */
record DebugGroupLayout(DebugGroup group, int width, int height, int labelWidth,
                        List<FormattedCharSequence> title, List<MeasuredRow> rows) {
    /** The gap between names and values, in debug GUI units. */
    static final int VALUE_GAP = 5;

    /**
     * Shorten collapsible values while retaining every other row in the group.
     * <p>
     * Each shortened value retains its first line and a final ellipsis. The
     * collected rows are unchanged and are measured in full again next frame.
     *
     * @param group the measured group to fit
     * @param font the debug overlay font
     * @param limit the remaining content height in the column
     * @return the group with only the necessary collapsible lines removed
     */
    static DebugGroupLayout compact(DebugGroupLayout group, Font font, int limit) {
        if (group.height() <= limit) {
            return group;
        }
        int height = group.height();
        List<MeasuredRow> rows = new ArrayList<>(group.rows().size());
        for (MeasuredRow row : group.rows()) {
            if (row.collapsible() && height > limit) {
                int oldLines = Math.max(row.names().size(), row.values().size());
                int needed = (height - limit + font.lineHeight - 1) / font.lineHeight;
                int retained = Math.max(Math.max(row.names().size(), 2), oldLines - needed);
                if (retained < row.values().size()) {
                    List<FormattedCharSequence> values = new ArrayList<>(row.values().subList(0, retained - 1));
                    values.add(FormattedCharSequence.forward("...", Style.EMPTY));
                    row = new MeasuredRow(row.names(), values, true, row.valueAligned(), row.definition());
                    height -= (oldLines - retained) * font.lineHeight;
                }
            }
            rows.add(row);
        }
        int width = Math.min(group.width(), measureWidth(font, group.labelWidth(), group.title(), rows));
        return new DebugGroupLayout(group.group(), width, height, group.labelWidth(),
            group.title(), rows);
    }

    /**
     * Measure a group's text and prepare its wrapped rows.
     * <p>
     * Names retain their full width whenever the column can also fit the values.
     * Long values reserve up to half of the column before names need to wrap.
     * The final background width follows the wrapped text without wrapping it again.
     *
     * @param contents the content of the group
     * @param font the debug overlay font
     * @param limit the maximum content width of one column
     * @return the measured group with its prepared text
     */
    static DebugGroupLayout measure(DebugGroupContents contents, Font font, int limit) {
        List<DebugRow> rows = contents.orderedRows();
        int labelWidth = 0;
        int valueWidth = 0;
        for (DebugRow row : rows) {
            if (row.hasLabel()) {
                labelWidth = Math.max(labelWidth, font.width(row.label().copy().append(":")));
            }
            if (row.alignsWithValues()) {
                valueWidth = Math.max(valueWidth, font.width(row.value()));
            }
        }
        int reservedValueWidth = Math.max(0, Math.min(valueWidth, Math.max(1, (limit - VALUE_GAP) / 2)));
        if (labelWidth > 0) {
            labelWidth = Math.max(1, Math.min(limit - VALUE_GAP - reservedValueWidth, labelWidth));
        }
        int naturalWidth = font.width(contents.group().title()) + 3;
        if (labelWidth > 0) {
            naturalWidth = Math.max(naturalWidth, labelWidth + VALUE_GAP + valueWidth);
        }
        for (DebugRow row : rows) {
            if (!row.alignsWithValues() || labelWidth == 0) {
                naturalWidth = Math.max(naturalWidth, font.width(row.value()));
            }
        }
        int width = Math.max(1, Math.min(limit, naturalWidth));
        List<FormattedCharSequence> title = contents.group().title().getString().isEmpty()
            ? List.of() : font.split(contents.group().title(), Math.max(1, width - 3));
        List<MeasuredRow> measuredRows = new ArrayList<>();
        int height = title.size() * font.lineHeight;
        for (DebugRow row : rows) {
            List<FormattedCharSequence> names = row.hasLabel()
                ? split(font, row.label().copy().append(":"), labelWidth) : List.of();
            int availableWidth = row.alignsWithValues() && labelWidth > 0 ? width - labelWidth - VALUE_GAP : width;
            List<FormattedCharSequence> values = split(font, row.value(), Math.max(1, availableWidth));
            measuredRows.add(new MeasuredRow(names, values, row.collapsible(), row.alignsWithValues() && labelWidth > 0,
                DebugRowCatalog.remember(contents.group(), row)));
            height += Math.max(names.size(), values.size()) * font.lineHeight;
        }
        width = Math.min(width, measureWidth(font, labelWidth, title, measuredRows));
        return new DebugGroupLayout(contents.group(), width, height, labelWidth, title, measuredRows);
    }

    /**
     * Measure the occupied width of the prepared text at its drawing positions.
     * <p>
     * Named values and continuations retain the shared label offset. Full-width
     * lines and the indented title contribute only their actual visible widths.
     *
     * @param font the debug overlay font
     * @param labelWidth the shared width of the name column
     * @param title the wrapped title
     * @param rows the wrapped rows, including any shortened values
     * @return at least one pixel of content width
     */
    private static int measureWidth(
        Font font,
        int labelWidth,
        List<FormattedCharSequence> title,
        List<MeasuredRow> rows
    ) {
        int width = 1;
        for (FormattedCharSequence line : title) {
            width = Math.max(width, 3 + font.width(line));
        }
        for (MeasuredRow row : rows) {
            boolean named = !row.names().isEmpty();
            if (named) {
                width = Math.max(width, labelWidth);
            }
            int valueOffset = row.valueAligned() ? labelWidth + VALUE_GAP : 0;
            for (FormattedCharSequence line : row.values()) {
                width = Math.max(width, valueOffset + font.width(line));
            }
        }
        return width;
    }

    /**
     * Wrap text while retaining an empty line as vertical spacing.
     *
     * @param font the debug overlay font
     * @param text the component to wrap
     * @param width the available width
     * @return at least one line of styled text
     */
    private static List<FormattedCharSequence> split(Font font, Component text, int width) {
        List<FormattedCharSequence> lines = font.split(text, Math.max(1, width));
        return lines.isEmpty() ? List.of(FormattedCharSequence.EMPTY) : lines;
    }

    /**
     * A row whose name and value have been wrapped to their measured widths.
     *
     * @param names the wrapped name, including its colon
     * @param values the wrapped styled value
     * @param collapsible whether the value may be shortened to fit its column
     * @param valueAligned whether the value starts in the shared value column
     * @param definition the stable row identity used for key and value colors
     * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
     * Name: <b>Yuantong Ren</b>
     * @version 1.0.0
     * @since 1.0.0
     */
    record MeasuredRow(
        List<FormattedCharSequence> names,
        List<FormattedCharSequence> values,
        boolean collapsible,
        boolean valueAligned,
        DebugRowCatalog.Entry definition
    ) {
    }

}

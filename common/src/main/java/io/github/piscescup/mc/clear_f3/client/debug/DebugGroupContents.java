package io.github.piscescup.mc.clear_f3.client.debug;

import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * The rows and original text collected for a {@link DebugGroup} in one frame.
 *
 * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
 * Name: <b>Yuantong Ren</b>
 * @version 1.0.0
 * @since 1.0.0
 */
public final class DebugGroupContents {
    /**
     * The display definition of this group.
     */
    private final DebugGroup group;

    /**
     * The structured rows collected for this frame.
     */
    private final List<DebugRow> rows = new ArrayList<>();

    /**
     * The original text lines collected for this frame.
     */
    private final List<String> lines = new ArrayList<>();

    /**
     * The named rows, continuations and full-width text in their output order.
     */
    private final List<DebugRow> orderedRows = new ArrayList<>();
    private int unnamedRows;

    /**
     * Create a new content container for the given {@link DebugGroup}.
     *
     * @param group the display definition of the group
     */
    public DebugGroupContents(DebugGroup group) {
        this.group = Objects.requireNonNull(group, "group");
    }

    /**
     * Get the display definition of this group.
     *
     * @return the {@link DebugGroup} of this container
     */
    public DebugGroup group() {
        return group;
    }

    /**
     * Get a read-only view of the structured rows.
     *
     * @return the rows in their insertion order
     */
    public List<DebugRow> rows() {
        return Collections.unmodifiableList(rows);
    }

    /**
     * Get a read-only view of the original text lines.
     *
     * @return the lines in their insertion order
     */
    public List<String> lines() {
        return Collections.unmodifiableList(lines);
    }

    /**
     * Get all rows and original lines in their output order.
     * <p>
     * Original lines have an empty label. Lines following a named value align
     * with that value until an empty line ends the continuation.
     *
     * @return a read-only view preserving mixed named and unnamed output
     */
    public List<DebugRow> orderedRows() {
        return Collections.unmodifiableList(orderedRows);
    }

    /**
     * Append a structured row to this group.
     *
     * @param row the row to append
     */
    public void addRow(DebugRow row) {
        Objects.requireNonNull(row, "row");
        if (row.colorKey().isEmpty()) row = row.withColorKey("line:" + this.unnamedRows++);
        rows.add(Objects.requireNonNull(row, "row"));
        orderedRows.add(row);
        DebugRowCatalog.remember(this.group, row);
    }

    /**
     * Append a value line, combining adjacent collapsible rows with the same name.
     * <p>
     * Explicit line breaks and styles are retained. An intervening ordinary row
     * ends the sequence, even when later lines use the same name.
     *
     * @param label the shared name of the collapsible value
     * @param value the next styled line of the value
     */
    public void addCollapsibleLine(Component label, Component value) {
        if (!orderedRows.isEmpty()) {
            int last = orderedRows.size() - 1;
            DebugRow previous = orderedRows.get(last);
            if (previous.collapsible() && previous.label().equals(label)) {
                DebugRow combined = new DebugRow(label, previous.value().copy().append("\n").append(value), true)
                    .withColorKey(previous.colorKey());
                orderedRows.set(last, combined);
                rows.set(rows.size() - 1, combined);
                return;
            }
        }
        addRow(new DebugRow(label, value, true));
    }

    /**
     * Append an original text line to this group.
     *
     * @param line the line to append, including an empty line when spacing is needed
     */
    public void addLine(String line) {
        addLine(line, DebugTextParser.formattedText(line));
    }

    /**
     * Append an original line whose formatting has already been interpreted.
     * <p>
     * Nonblank text following a named value or another continuation starts in
     * the value column. Leading text and blank lines retain the full width.
     *
     * @param line the unchanged original text
     * @param formatted the styled visible text for the original line
     */
    void addLine(String line, Component formatted) {
        lines.add(Objects.requireNonNull(line, "line"));
        boolean continuation = !formatted.getString().isBlank() && !orderedRows.isEmpty()
            && orderedRows.get(orderedRows.size() - 1).alignsWithValues();
        String key = continuation ? orderedRows.get(orderedRows.size() - 1).colorKey()
            : formatted.getString().isBlank() ? "" : "line:" + this.unnamedRows++;
        DebugRow row = new DebugRow(Component.empty(), formatted, false, continuation, key);
        orderedRows.add(row);
        if (!formatted.getString().isBlank()) DebugRowCatalog.remember(this.group, row);
    }

    /**
     * Check whether this group has any content to display.
     *
     * @return {@code true} if this group contains neither rows nor nonblank lines
     */
    public boolean isEmpty() {
        return rows.isEmpty() && lines.stream().allMatch(String::isBlank);
    }
}

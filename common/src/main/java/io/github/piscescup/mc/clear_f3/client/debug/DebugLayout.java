package io.github.piscescup.mc.clear_f3.client.debug;

import net.minecraft.client.gui.Font;
import net.minecraft.resources.Identifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Common panel placement for legacy text and modern debug-entry output.
 * Placement completes before drawing; previous columns survive small data changes.
 *
 * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
 * Name: <b>Yuantong Ren</b>
 * @since 1.0.0
 */
final class DebugLayout {
    private static final int MARGIN = 3;
    private static final int COLUMN_GAP = 8;
    private final Map<Identifier, DebugPanelSize> sizes = new LinkedHashMap<>();
    private List<Identifier> previousLeft = List.of();
    private List<Identifier> previousRight = List.of();
    private List<Identifier> previousGroups = List.of();
    private int previousWidth;
    private int previousHeight;

    /** Discard size and column history when the viewport or world changes. */
    void clear() {
        sizes.clear();
        previousLeft = List.of();
        previousRight = List.of();
        previousGroups = List.of();
    }

    /**
     * Prepare visible panel positions without issuing any drawing commands.
     *
     * @param font the current font, measured afresh after resource or language changes
     * @param width the viewport width
     * @param height the viewport height
     * @param groups the groups in canonical display order
     * @return the visible panels and their positions
     */
    List<Placement> arrange(Font font, int width, int height, Collection<DebugGroupContents> groups) {
        long now = System.nanoTime();
        if (width <= 2 * MARGIN + COLUMN_GAP || height <= 4) {
            clear();
            return List.of();
        }
        if (width != previousWidth || height != previousHeight) {
            clear();
            previousWidth = width;
            previousHeight = height;
        }
        int contentLimit = Math.max(1, (width - 2 * MARGIN - COLUMN_GAP) / 2);
        Map<Identifier, DebugGroupLayout> pending = new LinkedHashMap<>();
        for (DebugGroupContents contents : groups) {
            if (!contents.isEmpty()) {
                pending.put(contents.group().id(), stabilize(DebugGroupLayout.measure(contents, font, contentLimit), now));
            }
        }
        ColumnState left = new ColumnState(DebugGroup.Column.LEFT);
        ColumnState right = new ColumnState(DebugGroup.Column.RIGHT);
        sizes.keySet().retainAll(pending.keySet());
        List<Identifier> currentGroups = List.copyOf(pending.keySet());
        if (!currentGroups.equals(previousGroups)) {
            // Newly enabled panels return to canonical order on both API paths.
            previousLeft = List.of();
            previousRight = List.of();
            previousGroups = currentGroups;
        }
        List<Placement> placements = new ArrayList<>();
        // Titleless priority groups remain above all ordinary groups.
        var iterator = pending.values().iterator();
        while (iterator.hasNext()) {
            DebugGroupLayout group = iterator.next();
            if (group.title().isEmpty() && group.group().preferredColumn() != DebugGroup.Column.AUTO) {
                ColumnState column = group.group().preferredColumn() == DebugGroup.Column.LEFT ? left : right;
                if (place(group, column, placements, font, width, height)) {
                    iterator.remove();
                }
            }
        }
        restore(previousLeft, pending, left, placements, font, width, height);
        restore(previousRight, pending, right, placements, font, width, height);
        iterator = pending.values().iterator();
        while (iterator.hasNext()) {
            DebugGroupLayout group = iterator.next();
            DebugGroup.Column preferred = group.group().preferredColumn();
            if (preferred != DebugGroup.Column.AUTO) {
                ColumnState column = preferred == DebugGroup.Column.LEFT ? left : right;
                if (place(group, column, placements, font, width, height)) {
                    iterator.remove();
                }
            }
        }
        for (DebugGroupLayout group : pending.values()) {
            ColumnState first = left.y < right.y ? left : right;
            ColumnState second = first == left ? right : left;
            if (!place(group, first, placements, font, width, height)) {
                place(group, second, placements, font, width, height);
            }
        }
        previousLeft = List.copyOf(left.groups);
        previousRight = List.copyOf(right.groups);
        return List.copyOf(placements);
    }

    /** Briefly hold shrinking widths while keeping height matched to current content. */
    private DebugGroupLayout stabilize(DebugGroupLayout group, long now) {
        DebugPanelSize size = sizes.computeIfAbsent(group.group().id(), ignored -> new DebugPanelSize());
        size.update(group.width(), group.height(), now);
        return new DebugGroupLayout(group.group(), size.width(), size.height(), group.labelWidth(),
            group.title(), group.rows());
    }

    /** A measured panel and its final drawing coordinates. */
    record Placement(DebugGroupLayout group, int x, int y, DebugGroup.Column side) {}

    /**
     * Restore the previous order of groups that still fit in their column.
     *
     * @param previous the identifiers displayed in the previous frame
     * @param pending the groups that have not been placed
     * @param column the column to restore
     * @param placements the resulting panel positions
     * @param font the debug overlay font
     * @param width the viewport width
     * @param height the viewport height
     */
    private void restore(
        List<Identifier> previous,
        Map<Identifier, DebugGroupLayout> pending,
        ColumnState column,
        List<Placement> placements,
        Font font,
        int width,
        int height
    ) {
        for (Identifier id : previous) {
            DebugGroupLayout group = pending.get(id);
            if (group != null && place(group, column, placements, font, width, height)) {
                pending.remove(id);
            }
        }
    }

    /**
     * Place a group when the column has enough space.
     * <p>
     * Long collapsible values are shortened before checking whether the group
     * fits. The other rows and at least one value line remain visible.
     * <p>
     * A group taller than the viewport may occupy an empty column and is clipped
     * to the viewport. Later groups never overlap a previously placed group.
     *
     * @param group the measured group to place
     * @param column the target column
     * @param placements the resulting panel positions
     * @param font the debug overlay font
     * @param width the viewport width
     * @param height the viewport height
     * @return {@code true} if the group was placed
     */
    private boolean place(
        DebugGroupLayout group,
        ColumnState column,
        List<Placement> placements,
        Font font,
        int width,
        int height
    ) {
        group = DebugGroupLayout.compact(group, font, height - column.y - 1);
        if (!column.groups.isEmpty() && column.y + group.height() + 1 > height) {
            return false;
        }
        int x = column.side == DebugGroup.Column.LEFT ? MARGIN : width - MARGIN - group.width();
        placements.add(new Placement(group, x, column.y, column.side));
        column.groups.add(group.group().id());
        column.y += group.height() + font.lineHeight;
        return true;
    }

    /**
     * The placement state of one column during a frame.
     *
     * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
     * Name: <b>Yuantong Ren</b>
     * @version 1.0.0
     * @since 1.0.0
     */
    private static final class ColumnState {
        /**
         * The side occupied by this column.
         */
        private final DebugGroup.Column side;

        /**
         * The identifiers of groups placed in this column.
         */
        private final List<Identifier> groups = new ArrayList<>();

        /**
         * The next available vertical coordinate.
         */
        private int y = 2;

        /**
         * Create the placement state of the given column.
         *
         * @param side the side occupied by the column
         */
        private ColumnState(DebugGroup.Column side) {
            this.side = side;
        }
    }

}

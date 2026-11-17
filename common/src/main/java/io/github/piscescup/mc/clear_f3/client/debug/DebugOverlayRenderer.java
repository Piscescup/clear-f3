package io.github.piscescup.mc.clear_f3.client.debug;

import com.mojang.blaze3d.platform.Window;
//#if MC>=260300
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
//#endif
//#if MC>=260300
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
//#endif
import io.github.piscescup.mc.clear_f3.ClearF3References;
import io.github.piscescup.mc.clear_f3.client.ClearF3Config;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
//#if MC>=260300
import net.minecraft.client.renderer.RenderPipelines;
//#endif
import net.minecraft.util.FormattedCharSequence;

import java.util.Collection;
import java.util.List;

/**
 * The GUI drawing backend for the {@code Clear F3} overlay.
 * <p>
 * {@link DebugGroupLayout} measures styled text and {@link DebugLayout} assigns
 * panel positions. This class applies the fixed GUI scale and draws the prepared
 * panels, keeping graphics API differences out of measurement and placement.
 *
 * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
 * Name: <b>Yuantong Ren</b>
 * @version 1.0.0
 * @since 1.0.0
 */
public final class DebugOverlayRenderer {
    /**
     * The number of framebuffer pixels per overlay unit, independent of GUI scale.
     */
    private static final int TEXT_SCALE = 2;

    /**
     * The distance between a row name and its value.
     */
    private static final int VALUE_GAP = DebugGroupLayout.VALUE_GAP;

    /**
     * The width of the solid accent lines in debug GUI coordinates.
     */
    private static final int ACCENT_WIDTH = 2;

    /**
     * The opaque GUI pipeline that replaces the destination color of accent lines.
     * <p>
     * The vanilla GUI shaders are retained, while color blending is disabled.
     */
    //#if MC>=260300
    private static final RenderPipeline ACCENT_PIPELINE = RenderPipelines.register(
        RenderPipeline.builder(RenderPipelines.GUI_SNIPPET)
            .withLocation(ClearF3References.id("pipeline/debug_accent"))
            .withColorTargetState(ColorTargetState.DEFAULT)
            .build()
    );
    //#endif

    /**
     * The background color of group titles in ARGB format.
     */
    private static final int TITLE_BACKGROUND = 0x90303030;

    /**
     * The background color of group content in ARGB format.
     */
    private static final int CONTENT_BACKGROUND = 0x90505050;

    /**
     * The shared panel placement and size history.
     */
    private final DebugLayout layout = new DebugLayout();

    /**
     * Create a renderer with an empty layout history.
     */
    public DebugOverlayRenderer() {
    }

    /**
     * Clear size and placement history after a world or entry-selection change.
     */
    public void clearLayoutCache() {
        layout.clear();
    }

    /**
     * Draw the overlay at a fixed pixel scale and restore the caller's transform.
     * <p>
     * Layout uses the same scale as text, backgrounds and clipping. Replacing the
     * local transform also avoids multiplying the separate vanilla debug scale
     * introduced in newer versions. The saved transform remains available to charts.
     *
     * @param graphics the extractor receiving the GUI render state
     * @param font     the debug overlay font
     * @param window   the current framebuffer dimensions and GUI scale
     * @param groups   the groups collected for the current frame
     */
    public void extractAtFixedScale(
        GuiGraphicsExtractor graphics, Font font, Window window, Collection<DebugGroupContents> groups
    ) {
        float scale = (float) (TEXT_SCALE / (double) window.getGuiScale());
        var pose = graphics.pose();
        //#if MC>=12106
        pose.pushMatrix();
        //#else
        //$$ pose.pushPose();
        //#endif
        try {
            //#if MC>=12106
            pose.identity();
            pose.scale(scale, scale);
            //#else
            //$$ pose.setIdentity();
            //$$ pose.scale(scale, scale, 1.0F);
            //#endif
            extractRenderState(graphics, font, window.getWidth() / TEXT_SCALE,
                window.getHeight() / TEXT_SCALE, groups);
        } finally {
            //#if MC>=12106
            pose.popMatrix();
            //#else
            //$$ pose.popPose();
            //#endif
        }
    }

    /**
     * Measure, place and draw panels using the same layout on all game versions.
     *
     * @param graphics the GUI output
     * @param font     the debug font
     * @param width    the available width
     * @param height   the available height
     * @param groups   the collected groups
     */
    public void extractRenderState(GuiGraphicsExtractor graphics, Font font, int width, int height,
                                   Collection<DebugGroupContents> groups) {
        List<DebugLayout.Placement> placements = layout.arrange(font, width, height, groups);
        if (placements.isEmpty()) return;
        //#if MC>=12106
        graphics.nextStratum();
        //#endif
        //#if MC>=12104
        graphics.enableScissor(0, 0, width, height);
        //#else
        //$$ graphics.enableScissor(0, 0, graphics.guiWidth(), graphics.guiHeight());
        //#endif
        try {
            for (DebugLayout.Placement placement : placements) {
                draw(placement.group(), graphics, font, placement.x(), placement.y(), width, placement.side());
            }
        } finally {
            graphics.disableScissor();
        }
    }

    /**
     * Draw the measured title, content and accent line of a group.
     *
     * @param group       the measured group
     * @param graphics    the GUI render-state extractor
     * @param font        the debug overlay font
     * @param x           the content's left coordinate
     * @param y           the title's top coordinate
     * @param screenWidth the viewport width
     * @param side        the column containing this group
     */
    private void draw(
        DebugGroupLayout group,
        GuiGraphicsExtractor graphics,
        Font font,
        int x,
        int y,
        int screenWidth,
        DebugGroup.Column side
    ) {
        int top = y;
        int titleHeight = group.title().size() * font.lineHeight;
        if (titleHeight > 0) {
            graphics.fill(
                x - 1, y - 1,
                x + group.width() + 1, y + titleHeight - 1,
                TITLE_BACKGROUND
            );
            for (FormattedCharSequence line : group.title()) {
                graphics.text(
                    font, line,
                    x + 3, y,
                    0xFFFFFFFF, false
                );
                y += font.lineHeight;
            }
        }
        graphics.fill(x - 1, y - 1, x + group.width() + 1, top + group.height() + 1, CONTENT_BACKGROUND);
        for (DebugGroupLayout.MeasuredRow row : group.rows()) {
            for (int index = 0; index < row.names().size(); index++) {
                FormattedCharSequence line = row.names().get(index);
                text(graphics, font, group.group(), row.definition(), DebugColorRole.KEY, line,
                    x + group.labelWidth() - font.width(line),
                    y + index * font.lineHeight
                );
            }
            for (int index = 0; index < row.values().size(); index++) {
                text(graphics, font, group.group(), row.definition(), DebugColorRole.VALUE, row.values().get(index),
                    row.valueAligned() ? x + group.labelWidth() + VALUE_GAP : x,
                    y + index * font.lineHeight
                );
            }
            y += Math.max(row.names().size(), row.values().size()) * font.lineHeight;
        }
        if (group.group().accentColor().isPresent()) {
            int accentX = side == DebugGroup.Column.LEFT ? 0 : screenWidth - ACCENT_WIDTH;
            //#if MC>=260300
            graphics.fill(ACCENT_PIPELINE, accentX, top - 1, accentX + ACCENT_WIDTH, top + group.height() + 1,
                0xFF000000 | group.group().accentColor().getAsInt());
            //#else
            //$$ graphics.fill(accentX, top - 1, accentX + ACCENT_WIDTH, top + group.height() + 1,
            //$$     0xFF000000 | group.group().accentColor().getAsInt());
            //#endif
        }
    }

    /** Apply only this row's selected color, preserving other character styles. */
    private void text(GuiGraphicsExtractor graphics, Font font, DebugGroup group, DebugRowCatalog.Entry row,
                      DebugColorRole role, FormattedCharSequence line, int x, int y) {
        int color = ClearF3Config.color(group, row, role);
        if ((color >>> 24) == 0) return;
        FormattedCharSequence styled = ClearF3Config.hasColor(group, row, role)
            ? sink -> line.accept((index, style, codePoint) -> sink.accept(index, style.withColor(color & 0xFFFFFF), codePoint))
            : line;
        graphics.text(font, styled, x, y, color, false);
    }

}

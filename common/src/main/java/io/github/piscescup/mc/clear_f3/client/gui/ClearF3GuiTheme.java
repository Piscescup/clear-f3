package io.github.piscescup.mc.clear_f3.client.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

/**
 * Transparent settings pages, light gray color dialogs and shared beveled controls.
 *
 * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
 * Name: <b>Yuantong Ren</b>
 * @version 1.0.0
 * @since 1.0.0
 */
final class ClearF3GuiTheme {
    static final int BACKGROUND = 0x70000000;
    static final int MENU_BACKGROUND = 0xFF181818;
    static final int PANEL = 0xFFF0F0F0;
    static final int PANEL_MUTED = 0xFF555555;
    static final int PANEL_NOTICE = 0xFFB52A25;
    static final int MODAL_OVERLAY = 0xAA000000;
    static final int HOVER = 0xFF303030;
    static final int BORDER = 0xFF9A9A9A;
    static final int TEXT = 0xFFE0E0E0;
    static final int MUTED = 0xFFA0A0A0;
    static final int ACCENT = 0xFF735218;
    static final int NOTICE = 0xFFFF6666;
    static final int CONTROL_TEXT = 0xFFE0E0E0;
    static final int CONTROL_MUTED = 0xFFA0A0A0;

    /**
     * Prevent construction of this drawing utility.
     */
    private ClearF3GuiTheme() {
    }

    /**
     * Draw a light gray panel with a single-pixel border.
     */
    static void frame(GuiGraphicsExtractor graphics, int left, int top, int right, int bottom) {
        graphics.fill(left, top, right, bottom, BORDER);
        graphics.fill(left + 1, top + 1, right - 1, bottom - 1, PANEL);
    }

    /**
     * Draw a beveled button while leaving its input and focus handling to vanilla.
     */
    static void button(GuiGraphicsExtractor graphics, int left, int top, int right, int bottom,
                       boolean active, boolean highlighted) {
        graphics.fill(left, top, right, bottom, 0xFF080808);
        graphics.fill(left + 1, top + 1, right - 1, bottom - 1,
            !active ? 0xFF303030 : highlighted ? 0xFF808080 : 0xFF686868);
        int light = !active ? 0xFF505050 : highlighted ? 0xFFFFFFFF : 0xFFC0C0C0;
        graphics.fill(left + 1, top + 1, right - 1, top + 2, light);
        graphics.fill(left + 1, top + 1, left + 2, bottom - 1, light);
        graphics.fill(left + 2, bottom - 2, right - 1, bottom - 1, 0xFF303030);
        graphics.fill(right - 2, top + 2, right - 1, bottom - 1, 0xFF303030);
    }

    /**
     * Show alpha over a checkerboard rather than flattening against the panel.
     */
    static void swatch(GuiGraphicsExtractor graphics, int x, int y, int size, int color) {
        graphics.fill(x - 1, y - 1, x + size + 1, y + size + 1, BORDER);
        int half = size / 2;
        graphics.fill(x, y, x + size, y + size, 0xFFCCCCCC);
        graphics.fill(x, y, x + half, y + half, 0xFF777777);
        graphics.fill(x + half, y + half, x + size, y + size, 0xFF777777);
        graphics.fill(x, y, x + size, y + size, color);
    }

    /**
     * Secondary group identifiers use the compact caption scale of the reference UI.
     */
    static void smallText(GuiGraphicsExtractor graphics, Font font, String text, int x, int y, int width) {
        float scale = 0.75F;
        var lines = font.split(Component.literal(text), Math.max(1, (int) (width / scale)));
        if (lines.isEmpty()) return;
        //#if MC>=12106
        graphics.pose().pushMatrix();
        graphics.pose().scale(scale, scale);
        //#else
        //$$ graphics.pose().pushPose();
        //$$ graphics.pose().scale(scale, scale, 1.0F);
        //#endif
        graphics.text(font, lines.get(0), (int) (x / scale), (int) (y / scale), MUTED, false);
        //#if MC>=12106
        graphics.pose().popMatrix();
        //#else
        //$$ graphics.pose().popPose();
        //#endif
    }
}

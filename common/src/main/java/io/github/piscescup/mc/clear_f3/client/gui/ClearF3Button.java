package io.github.piscescup.mc.clear_f3.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.function.IntSupplier;

/**
 * A gray settings button retaining vanilla activation, focus and narration.
 *
 * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
 * Name: <b>Yuantong Ren</b>
 * @version 1.0.0
 * @since 1.0.0
 */
final class ClearF3Button extends Button {
    private boolean plain;
    private String subtitle;
    private IntSupplier swatch;
    private boolean swatchOnly;

    /**
     * Create a themed button using the standard vanilla press callback.
     */
    ClearF3Button(int x, int y, int width, int height, Component message, OnPress onPress) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
    }

    /**
     * Use a flat section header while retaining native focus and narration.
     */
    ClearF3Button plain(String subtitle) {
        this.plain = true;
        this.subtitle = subtitle.isEmpty() ? null : subtitle;
        return this;
    }

    /**
     * Show the current color alongside the button label.
     */
    ClearF3Button swatch(IntSupplier color) {
        this.swatch = color;
        return this;
    }

    /**
     * Use a centered color chip while retaining the full hex label for narration.
     */
    ClearF3Button swatchOnly(IntSupplier color) {
        this.swatch = color;
        this.swatchOnly = true;
        return this;
    }

    /**
     * Draw the shared button surface and keep its label inside its bounds.
     */
    @Override
    //#if MC>=260100
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
    //#elseif MC>=12111
    //$$ protected void renderContents(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    //#else
    //$$ protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    //#endif
        if (this.plain) {
            if (isHoveredOrFocused())
                graphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), ClearF3GuiTheme.HOVER);
        } else {
            ClearF3GuiTheme.button(graphics, getX(), getY(), getX() + getWidth(), getY() + getHeight(),
                this.active, this.active && isHoveredOrFocused());
        }
        var font = Minecraft.getInstance().font;
        int inset = this.swatch == null ? 5 : 20;
        var lines = font.split(getMessage(), Math.max(1, getWidth() - inset - 5));
        if (!this.swatchOnly && !lines.isEmpty()) {
            var line = lines.get(0);
            int x = this.plain || this.swatch != null ? getX() + inset : getX() + (getWidth() - font.width(line)) / 2;
            int textColor = this.plain
                ? (this.active ? ClearF3GuiTheme.TEXT : ClearF3GuiTheme.MUTED)
                : (this.active ? ClearF3GuiTheme.CONTROL_TEXT : ClearF3GuiTheme.CONTROL_MUTED);
            graphics.text(font, line, x,
                this.subtitle == null ? getY() + (getHeight() - font.lineHeight) / 2 : getY() + 3,
                textColor, false);
        }
        if (this.subtitle != null) {
            ClearF3GuiTheme.smallText(graphics, font, this.subtitle, getX() + 5, getY() + 15, getWidth() - 10);
        }
        if (this.swatch != null)
            ClearF3GuiTheme.swatch(graphics, getX() + (this.swatchOnly ? (getWidth() - 10) / 2 : 5),
                getY() + (getHeight() - 10) / 2, 10, this.swatch.getAsInt());
    }
}

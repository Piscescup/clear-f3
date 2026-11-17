package io.github.piscescup.mc.clear_f3.client.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Scrollable, collapsible settings rows with clipped native controls.
 * Expansion follows stable component identifiers, including third-party groups.
 *
 * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
 * Name: <b>Yuantong Ren</b>
 * @version 1.0.0
 * @since 1.0.0
 */
final class ClearF3ContentList {
    private final Consumer<AbstractWidget> register;
    private final Runnable rebuild;
    private final List<PositionedWidget> widgets = new ArrayList<>();
    private final List<TextRow> rows = new ArrayList<>();
    private final List<Header> headers = new ArrayList<>();
    private final Map<String, Boolean> expanded = new HashMap<>();
    private Font font;
    private int left, top, width, height, cursor, depth;
    private double scroll;
    private boolean dragging;
    private double dragOffset;

    ClearF3ContentList(Consumer<AbstractWidget> register, Runnable rebuild) {
        this.register = register;
        this.rebuild = rebuild;
    }

    /**
     * Rebuild generated rows while preserving section and scroll state.
     */
    void begin(Font font, int left, int top, int width, int height) {
        this.font = font;
        this.left = left;
        this.top = top;
        this.width = Math.max(1, width);
        this.height = Math.max(1, height);
        this.cursor = 0;
        this.depth = 0;
        this.dragging = false;
        this.widgets.clear();
        this.rows.clear();
        this.headers.clear();
    }

    void finish() {
        scrollTo(this.scroll);
    }

    double scroll() {
        return this.scroll;
    }

    void scrollTo(double value) {
        this.scroll = Math.max(0, Math.min(maximum(), value));
        positionWidgets();
    }

    private int maximum() {
        return Math.max(0, this.cursor - this.height);
    }

    private int screenY(int y) {
        return this.top + y - (int) this.scroll;
    }

    private int innerWidth() {
        return Math.max(1, this.width - this.depth * 14 - 16);
    }

    /**
     * Build a nested section only while it is expanded.
     */
    void section(String key, Component label, String subtitle, boolean initial, Runnable contents) {
        boolean open = this.expanded.getOrDefault(key, initial);
        Component message = Component.literal(open ? "[-] " : "[+] ")
            .withStyle(style -> style.withColor(open ? ClearF3GuiTheme.MUTED : ClearF3GuiTheme.NOTICE))
            .append(label.copy().withStyle(style -> style.withColor(ClearF3GuiTheme.TEXT)));
        var button = new ClearF3Button(0, 0, innerWidth(), 28, message, ignored -> {
            this.expanded.put(key, !open);
            this.rebuild.run();
        }).plain(subtitle);
        widget(button, this.depth * 14, this.cursor, innerWidth(), true);
        this.cursor += 30;
        if (open) {
            this.depth++;
            contents.run();
            this.depth--;
        }
    }

    void expand(List<String> keys, boolean value) {
        for (String key : keys) this.expanded.put(key, value);
    }

    boolean allExpanded(List<String> keys) {
        return !keys.isEmpty() && keys.stream().allMatch(key -> this.expanded.getOrDefault(key, false));
    }

    void header(Component title) {
        this.headers.add(new Header(this.cursor, title));
        this.cursor += 24;
    }

    void text(Component text) {
        List<FormattedCharSequence> lines = this.font.split(text, Math.max(1, innerWidth() - 8));
        int rowHeight = Math.max(22, lines.size() * (this.font.lineHeight + 2) + 8);
        this.rows.add(new TextRow(this.cursor, rowHeight, this.depth * 14 + 4, lines));
        this.cursor += rowHeight + 2;
    }

    void full(AbstractWidget widget) {
        widget(widget, this.depth * 14 + 4, this.cursor + 3, Math.max(1, innerWidth() - 4), true);
        this.cursor += 26;
    }

    /** Keep a setting label and its control together in a compact row. */
    void option(Component label, AbstractWidget control) {
        int available = Math.max(1, innerWidth() - 4);
        int labelWidth = Math.max(1, Math.min(160, (available - 4) / 2));
        int controlWidth = Math.max(1, Math.min(160, available - labelWidth - 4));
        var lines = this.font.split(label, Math.max(1, labelWidth - 4));
        int rowHeight = Math.max(26, lines.size() * (this.font.lineHeight + 1) + 6);
        int x = this.depth * 14 + 4;
        this.rows.add(new TextRow(this.cursor, rowHeight, x, lines));
        widget(control, x + labelWidth + 4, this.cursor + (rowHeight - 20) / 2, controlWidth, true);
        this.cursor += rowHeight + 2;
    }

    /**
     * Keep one row's two color controls side by side under its Group heading.
     */
    void rowColors(Component label, AbstractWidget key, AbstractWidget value, AbstractWidget reset,
                   boolean hasKey, boolean canReset) {
        int available = Math.max(1, innerWidth() - 4);
        int labelWidth = Math.max(1, available * 30 / 100);
        int resetWidth = Math.min(48, Math.max(1, available / 6));
        int colorWidth = Math.max(1, (available - labelWidth - resetWidth - 12) / 2);
        var lines = this.font.split(label, Math.max(1, labelWidth - 4));
        int rowHeight = Math.max(26, lines.size() * (this.font.lineHeight + 1) + 6);
        int x = this.depth * 14 + 4;
        this.rows.add(new TextRow(this.cursor, rowHeight, x, lines));
        widget(key, x + labelWidth, this.cursor + (rowHeight - 20) / 2, colorWidth, hasKey);
        widget(value, x + labelWidth + colorWidth + 4, this.cursor + (rowHeight - 20) / 2, colorWidth, true);
        widget(reset, x + labelWidth + 2 * colorWidth + 8, this.cursor + (rowHeight - 20) / 2,
            resetWidth, canReset);
        this.cursor += rowHeight + 2;
    }

    private void widget(AbstractWidget widget, int x, int y, int width, boolean enabled) {
        widget.setWidth(width);
        this.register.accept(widget);
        this.widgets.add(new PositionedWidget(widget, x, y, enabled));
    }

    private void positionWidgets() {
        for (var positioned : this.widgets) {
            var widget = positioned.widget();
            widget.setX(this.left + positioned.x());
            widget.setY(screenY(positioned.y()));
            boolean visible = widget.getY() >= this.top && widget.getY() + widget.getHeight() <= this.top + this.height;
            widget.visible = visible;
            widget.active = visible && positioned.enabled();
        }
    }

    boolean mouseScrolled(double x, double y, double amount) {
        if (!contains(x, y) || maximum() == 0 || amount == 0) return false;
        scrollTo(this.scroll - amount * 28);
        return true;
    }

    private boolean contains(double x, double y) {
        return x >= this.left && x < this.left + this.width && y >= this.top && y < this.top + this.height;
    }

    private int thumbHeight() {
        return Math.min(this.height, Math.max(12, this.height * this.height / Math.max(1, this.cursor)));
    }

    private int thumbY() {
        return this.top + (int) ((this.height - thumbHeight()) * this.scroll / Math.max(1, maximum()));
    }

    boolean mouseClicked(double x, double y, int button) {
        if (button != 0 || maximum() == 0 || !contains(x, y) || x < this.left + this.width - 8) return false;
        this.dragging = true;
        this.dragOffset = y >= thumbY() && y < thumbY() + thumbHeight() ? y - thumbY() : thumbHeight() / 2.0;
        return mouseDragged(x, y, button);
    }

    boolean mouseDragged(double x, double y, int button) {
        if (!this.dragging || button != 0) return false;
        scrollTo((y - this.top - this.dragOffset) * maximum() / Math.max(1, this.height - thumbHeight()));
        return true;
    }

    boolean mouseReleased(int button) {
        boolean handled = this.dragging && button == 0;
        if (handled) this.dragging = false;
        return handled;
    }

    void draw(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        graphics.enableScissor(this.left, this.top, this.left + this.width, this.top + this.height);
        try {
            for (Header header : this.headers) {
                int y = screenY(header.y());
                if (y + 24 <= this.top || y >= this.top + this.height) continue;
                //#if MC>=260100
                graphics.text(this.font, header.title().copy().withStyle(ChatFormatting.BOLD), this.left + 4,
                    y + 5, ClearF3GuiTheme.TEXT, false);
                //#else
                //$$ graphics.drawString(this.font, header.title().copy().withStyle(ChatFormatting.BOLD), this.left + 4,
                //$$     y + 5, ClearF3GuiTheme.TEXT, false);
                //#endif
                graphics.fill(this.left + 3, y + 20, this.left + this.width - 10, y + 21, ClearF3GuiTheme.BORDER);
            }
            for (TextRow row : this.rows) {
                int y = screenY(row.y());
                if (y + row.height() <= this.top || y >= this.top + this.height) continue;
                int textY = y + Math.max(3, (row.height() - row.lines().size() * (this.font.lineHeight + 1)) / 2);
                for (var line : row.lines()) {
                    //#if MC>=260100
                    graphics.text(this.font, line, this.left + row.x(), textY, ClearF3GuiTheme.TEXT, false);
                    //#else
                    //$$ graphics.drawString(this.font, line, this.left + row.x(), textY, ClearF3GuiTheme.TEXT, false);
                    //#endif
                    textY += this.font.lineHeight + 1;
                }
            }
            for (var positioned : this.widgets) {
                if (!positioned.widget().visible) continue;
                //#if MC>=260100
                positioned.widget().extractRenderState(graphics, mouseX, mouseY, partialTick);
                //#else
                //$$ positioned.widget().render(graphics, mouseX, mouseY, partialTick);
                //#endif
            }
        } finally {
            graphics.disableScissor();
        }
        if (maximum() > 0) {
            graphics.fill(this.left + this.width - 7, this.top, this.left + this.width, this.top + this.height, 0xFF303030);
            graphics.fill(this.left + this.width - 6, thumbY(), this.left + this.width - 1,
                thumbY() + thumbHeight(), ClearF3GuiTheme.BORDER);
        }
    }

    private record PositionedWidget(AbstractWidget widget, int x, int y, boolean enabled) {
    }

    private record TextRow(int y, int height, int x, List<FormattedCharSequence> lines) {
    }

    private record Header(int y, Component title) {
    }
}

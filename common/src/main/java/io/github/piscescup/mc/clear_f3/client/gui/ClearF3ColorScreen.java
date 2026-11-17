package io.github.piscescup.mc.clear_f3.client.gui;

import io.github.piscescup.mc.clear_f3.client.ClearF3Config;
import io.github.piscescup.mc.clear_f3.client.debug.DebugColorRole;
import io.github.piscescup.mc.clear_f3.client.debug.DebugGroup;
import io.github.piscescup.mc.clear_f3.client.debug.DebugGroups;
import io.github.piscescup.mc.clear_f3.client.debug.DebugRowCatalog;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * A modal color editor with presets, validated hexadecimal input and a local preview.
 * Only Apply changes saved colors; Escape and Cancel discard the draft.
 *
 * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
 * Name: <b>Yuantong Ren</b>
 * @version 1.0.0
 * @since 1.0.0
 */
final class ClearF3ColorScreen extends ClearF3Screen {
    private static final int[] PALETTE = {
        0xFF000000, 0xFF0000AA, 0xFF00AA00, 0xFF00AAAA,
        0xFFAA0000, 0xFFAA00AA, 0xFFFFAA00, 0xFFAAAAAA,
        0xFF555555, 0xFF5555FF, 0xFF55FF55, 0xFF55FFFF,
        0xFFFF5555, 0xFFFF55FF, 0xFFFFFF55, 0xFFFFFFFF
    };
    private final DebugGroup group;
    private final DebugRowCatalog.Entry row;
    private final DebugColorRole role;
    private EditBox input;
    private ClearF3Button apply;
    private int selected;
    private boolean resetRequested;
    private int left, top, panelWidth, panelHeight;

    ClearF3ColorScreen(Screen parent, DebugGroup group, DebugRowCatalog.Entry row, DebugColorRole role) {
        super(parent, Component.translatable("clear_f3.gui.edit_color", Component.translatable(role.translationKey())));
        this.group = group;
        this.row = row;
        this.role = role;
        this.selected = ClearF3Config.color(group, row, role);
    }

    @Override
    protected void init() {
        if (this.parent.width != this.width || this.parent.height != this.height) {
            //#if MC>=12111
            this.parent.resize(this.width, this.height);
            //#else
            //$$ this.parent.resize(this.minecraft, this.width, this.height);
            //#endif
        }
        this.panelWidth = Math.max(1, Math.min(360, this.width - 24));
        this.panelHeight = Math.max(1, Math.min(224, this.height - 16));
        this.left = (this.width - this.panelWidth) / 2;
        this.top = (this.height - this.panelHeight) / 2;
        int innerWidth = Math.max(1, this.panelWidth - 32);
        this.input = addRenderableWidget(new EditBox(this.font, this.left + 16, this.top + 46,
            innerWidth, 20, Component.translatable("clear_f3.gui.hex_color")));
        this.input.setMaxLength(9);
        this.input.setTextColor(ClearF3GuiTheme.CONTROL_TEXT);
        this.input.setValue(DebugColorRole.format(this.selected));
        int actionWidth = Math.max(1, (innerWidth - 8) / 3);
        this.apply = addRenderableWidget(new ClearF3Button(this.left + 16, this.top + this.panelHeight - 28,
            actionWidth, 20, Component.translatable("clear_f3.gui.apply"), ignored -> {
            if (this.resetRequested) ClearF3Config.resetColor(this.group, this.row, this.role);
            else ClearF3Config.setColor(this.group, this.row, this.role, this.selected);
            onClose();
        }));
        addRenderableWidget(new ClearF3Button(this.left + 20 + actionWidth, this.top + this.panelHeight - 28,
            actionWidth, 20, Component.translatable("controls.reset"), ignored -> {
            this.input.setValue(DebugColorRole.format(this.role.defaultColor(this.row)));
            this.resetRequested = true;
        }));
        addRenderableWidget(new ClearF3Button(this.left + 24 + actionWidth * 2, this.top + this.panelHeight - 28,
            actionWidth, 20, Component.translatable("gui.cancel"), ignored -> onClose()));
        this.input.setResponder(value -> {
            var parsed = DebugColorRole.parse(value);
            this.apply.active = parsed.isPresent();
            this.input.setTextColor(parsed.isPresent() ? ClearF3GuiTheme.CONTROL_TEXT : 0xFFFF6666);
            parsed.ifPresent(color -> this.selected = color);
            this.resetRequested = false;
        });
        int cellWidth = Math.max(1, innerWidth / 8);
        for (int i = 0; i < PALETTE.length; i++) {
            int color = PALETTE[i];
            addRenderableWidget(new ClearF3Button(this.left + 16 + (i % 8) * cellWidth,
                this.top + 88 + (i / 8) * 24, Math.max(1, cellWidth - 3), 20,
                Component.literal(DebugColorRole.format(color)), ignored -> this.input.setValue(DebugColorRole.format(color)))
                .swatchOnly(() -> color));
        }
        setInitialFocus(this.input);
    }

    private int color(DebugColorRole role) {
        return role == this.role ? this.selected : ClearF3Config.color(this.group, this.row, role);
    }

    /**
     * Draw the draft without changing the running overlay until it is applied.
     */
    private void preview(GuiGraphicsExtractor graphics) {
        int x = this.left + 16;
        int y = this.top + 144;
        int right = this.left + this.panelWidth - 16;
        graphics.fill(x, y, right, y + 42, 0xFF383838);
        graphics.fill(x, y, right, y + 15, 0x90303030);
        graphics.fill(x, y + 15, right, y + 42, 0x90505050);
        if (this.group.accentColor().isPresent()) graphics.fill(x, y, x + 2, y + 42,
            0xFF000000 | this.group.accentColor().getAsInt());
        var title = this.font.split(DebugGroups.settingsTitle(this.group), Math.max(1, right - x - 12));
        if (!title.isEmpty()) graphics.text(this.font, title.get(0), x + 6, y + 3, 0xFFFFFFFF, false);
        Component label = this.row.hasKey() ? Component.literal(this.row.label().getString() + ":") : Component.empty();
        var labels = this.font.split(label, Math.max(1, (right - x) * 2 / 3));
        int keyWidth = 0;
        if (this.row.hasKey() && !labels.isEmpty()) {
            keyWidth = this.font.width(labels.get(0));
            if ((color(DebugColorRole.KEY) >>> 24) != 0) graphics.text(this.font, labels.get(0), x + 6, y + 23,
                color(DebugColorRole.KEY), false);
        }
        if ((color(DebugColorRole.VALUE) >>> 24) != 0) graphics.text(this.font, "123",
            x + 6 + (this.row.hasKey() ? keyWidth + 6 : 0), y + 23, color(DebugColorRole.VALUE), false);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        this.parent.extractRenderState(graphics, -1, -1, partialTick);
        //#if MC>=12106
        graphics.nextStratum();
        //#else
        //$$ graphics.pose().pushPose();
        //$$ graphics.pose().translate(0, 0, 300);
        //#endif
        try {
            graphics.fill(0, 0, this.width, this.height, ClearF3GuiTheme.MODAL_OVERLAY);
            ClearF3GuiTheme.frame(graphics, this.left, this.top,
                this.left + this.panelWidth, this.top + this.panelHeight);
            graphics.text(this.font, this.title, this.left + 16, this.top + 12, ClearF3GuiTheme.ACCENT, false);
            var lines = this.font.split(this.row.title(), Math.max(1, this.panelWidth - 32));
            if (!lines.isEmpty()) graphics.text(this.font, lines.get(0), this.left + 16, this.top + 28,
                ClearF3GuiTheme.PANEL_MUTED, false);
            graphics.text(this.font, Component.translatable(this.apply.active ? "clear_f3.gui.hex_hint" : "clear_f3.gui.hex_invalid"),
                this.left + 16, this.top + 72, this.apply.active ? ClearF3GuiTheme.PANEL_MUTED : ClearF3GuiTheme.PANEL_NOTICE, false);
            preview(graphics);
            super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        } finally {
            //#if MC<12106
            //$$ graphics.pose().popPose();
            //#endif
        }
    }
}

package io.github.piscescup.mc.clear_f3.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.piscescup.mc.clear_f3.client.ClearF3Config;
import io.github.piscescup.mc.clear_f3.client.ClearF3KeyMappings;
import io.github.piscescup.mc.clear_f3.client.debug.DebugColorRole;
import io.github.piscescup.mc.clear_f3.client.debug.DebugGroup;
import io.github.piscescup.mc.clear_f3.client.debug.DebugGroups;
import io.github.piscescup.mc.clear_f3.client.debug.DebugRowCatalog;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Tooltip;
//#if MC>=12109
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
//#endif
import net.minecraft.network.chat.Component;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Tabbed local settings with persistent section state and per-component colors.
 *
 * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
 * Name: <b>Yuantong Ren</b>
 * @version 1.0.0
 * @since 1.0.0
 */
public final class ClearF3ConfigScreen extends ClearF3Screen {
    private enum Page {GENERAL, COLORS}

    private Page page = Page.GENERAL;
    private final EnumMap<Page, Double> scrollHistory = new EnumMap<>(Page.class);
    private final ClearF3ContentList content = new ClearF3ContentList(this::addWidget, this::rebuildPage);
    private final Map<KeyMapping, ClearF3Button> keyButtons = new LinkedHashMap<>();
    private KeyMapping selectedKey;
    private int left, right, footer;

    /**
     * Create a settings page returning to its caller on Done or Escape.
     */
    public ClearF3ConfigScreen(Screen parent) {
        super(parent, Component.translatable("clear_f3.gui.title"));
    }

    @Override
    protected void init() {
        this.selectedKey = null;
        this.keyButtons.clear();
        int width = Math.max(1, this.width - 24);
        this.left = (this.width - width) / 2;
        this.right = this.left + width;
        this.footer = this.height - 32;
        Component generalLabel = Component.translatable("clear_f3.gui.tab.general");
        Component colorsLabel = Component.translatable("clear_f3.gui.tab.colors");
        int maxTabWidth = Math.max(1, (width - 4) / 2);
        int generalWidth = Math.max(1, Math.min(maxTabWidth, Math.max(54, this.font.width(generalLabel) + 16)));
        int colorsWidth = Math.max(1, Math.min(maxTabWidth, Math.max(54, this.font.width(colorsLabel) + 16)));
        var general = addRenderableWidget(new ClearF3Button(this.left, 30, generalWidth, 20,
            generalLabel, ignored -> select(Page.GENERAL)));
        var colors = addRenderableWidget(new ClearF3Button(this.left + generalWidth + 4, 30, colorsWidth, 20,
            colorsLabel, ignored -> select(Page.COLORS)));
        general.active = this.page != Page.GENERAL;
        colors.active = this.page != Page.COLORS;
        this.content.begin(this.font, this.left + 6, 58, Math.max(1, width - 12), Math.max(1, this.footer - 70));
        if (this.page == Page.GENERAL) buildGeneral();
        else buildColors();
        updateKeyButtons();
        this.content.finish();
        int actionWidth = Math.max(1, Math.min(120, (width - 8) / 3));
        if (this.page == Page.COLORS) {
            addRenderableWidget(new ClearF3Button(this.left, this.footer, actionWidth, 20,
                Component.translatable("clear_f3.gui.reset_colors"), ignored -> {
                ClearF3Config.resetColors();
                rebuildPage();
            }));
            boolean allExpanded = this.content.allExpanded(groupKeys());
            addRenderableWidget(new ClearF3Button(this.left + actionWidth + 4, this.footer, actionWidth, 20,
                Component.translatable(allExpanded ? "clear_f3.gui.collapse_all" : "clear_f3.gui.expand_all"), ignored -> {
                this.content.expand(groupKeys(), !allExpanded);
                rebuildPage();
            }));
        }
        int closeX = this.page == Page.COLORS ? this.left + 2 * (actionWidth + 4) : this.left;
        addRenderableWidget(new ClearF3Button(closeX, this.footer, actionWidth, 20,
            Component.translatable("gui.done"), ignored -> onClose()));
    }

    private void select(Page page) {
        this.scrollHistory.put(this.page, this.content.scroll());
        this.page = page;
        rebuildPage();
        this.content.scrollTo(this.scrollHistory.getOrDefault(page, 0.0));
    }

    private void rebuildPage() {
        clearWidgets();
        init();
    }

    private Component toggleLabel() {
        return Component.translatable(
            "clear_f3.gui.enabled",
            Component.translatable(ClearF3Config.isEnabled() ? "options.on" : "options.off"
                )
                .withStyle(ClearF3Config.isEnabled() ? ChatFormatting.GREEN : ChatFormatting.GRAY)
        );
    }

    private void buildGeneral() {
        this.content.header(Component.translatable("clear_f3.gui.tab.general"));
        this.content.section("general", Component.translatable("clear_f3.gui.tab.general"), "", true, () -> {
            this.content.full(new ClearF3Button(0, 0, 1, 20, toggleLabel(), button -> {
                ClearF3Config.toggle();
                button.setMessage(toggleLabel());
            }));
        });
        this.content.section("shortcuts", Component.translatable("clear_f3.gui.shortcuts"), "", true, () -> {
            keyBinding("clear_f3.gui.toggle_key", ClearF3KeyMappings.TOGGLE);
            keyBinding("clear_f3.gui.settings_key", ClearF3KeyMappings.OPEN_SETTINGS);
        });
    }

    /** Edit the existing vanilla mapping so the controls menu shares the same binding. */
    private void keyBinding(String label, KeyMapping mapping) {
        var button = new ClearF3Button(0, 0, 1, 20, mapping.getTranslatedKeyMessage(), ignored -> {
            this.selectedKey = mapping;
            updateKeyButtons();
        });
        button.setTooltip(Tooltip.create(Component.translatable(label).append(" ")
            .append(Component.translatable("clear_f3.gui.keybind_hint"))));
        this.keyButtons.put(mapping, button);
        this.content.option(Component.translatable(label), button);
    }

    private void updateKeyButtons() {
        this.keyButtons.forEach((mapping, button) -> {
            Component message = mapping.getTranslatedKeyMessage();
            if (mapping == this.selectedKey) {
                message = Component.translatable("clear_f3.gui.press_key").withStyle(ChatFormatting.YELLOW);
            } else if (!mapping.isUnbound()) {
                for (KeyMapping other : this.minecraft.options.keyMappings) {
                    if (other != mapping && mapping.same(other)) {
                        message = message.copy().withStyle(ChatFormatting.RED);
                        break;
                    }
                }
            }
            button.setMessage(message);
        });
    }

    /** Apply a captured input and persist it through Minecraft's normal options file. */
    private void bindSelectedKey(InputConstants.Key key) {
        this.selectedKey.setKey(key);
        this.selectedKey = null;
        KeyMapping.resetMapping();
        this.minecraft.options.save();
        updateKeyButtons();
    }

    private List<String> groupKeys() {
        return DebugGroups.configurableGroups().stream().map(group -> group.id().toString()).toList();
    }

    private void buildColors() {
        this.content.header(Component.translatable("clear_f3.gui.tab.colors"));
        this.content.text(Component.translatable("clear_f3.gui.colors_hint"));
        for (DebugGroup group : DebugGroups.configurableGroups()) {
            this.content.section(group.id().toString(), DebugGroups.settingsTitle(group), group.id().toString(), false, () -> {
                var rows = DebugRowCatalog.rows(group);
                if (rows.isEmpty()) this.content.text(Component.translatable("clear_f3.gui.rows_hint"));
                for (var row : rows) {
                    var key = colorButton(group, row, DebugColorRole.KEY);
                    var value = colorButton(group, row, DebugColorRole.VALUE);
                    var reset = new ClearF3Button(0, 0, 1, 20,
                        Component.translatable("controls.reset"), ignored -> {
                        ClearF3Config.resetColors(group, row);
                        rebuildPage();
                    });
                    this.content.rowColors(row.title(), key, value, reset, row.hasKey(),
                        ClearF3Config.hasColor(group, row, DebugColorRole.KEY) || ClearF3Config.hasColor(group, row, DebugColorRole.VALUE));
                }
                this.content.full(new ClearF3Button(0, 0, 1, 20,
                    Component.translatable("clear_f3.gui.reset_component"), ignored -> {
                    ClearF3Config.resetColors(group);
                    rebuildPage();
                }));
            });
        }
    }

    private ClearF3Button colorButton(DebugGroup group, DebugRowCatalog.Entry row, DebugColorRole role) {
        int color = ClearF3Config.color(group, row, role);
        Component label = Component.translatable("clear_f3.gui.color_button", Component.translatable(role.translationKey()),
            DebugColorRole.format(color));
        var button = new ClearF3Button(0, 0, 1, 20, label,
            ignored -> display(new ClearF3ColorScreen(this, group, row, role)))
            .swatch(() -> ClearF3Config.color(group, row, role));
        button.setTooltip(Tooltip.create(label));
        return button;
    }

    /**
     * Minecraft method renames are supplied by the adjacent-version mapping files.
     */
    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, this.width, this.height,
            this.minecraft.level == null ? ClearF3GuiTheme.MENU_BACKGROUND : ClearF3GuiTheme.BACKGROUND);
        graphics.text(this.font, this.title, this.left + 8, 6, ClearF3GuiTheme.TEXT, false);
        int hintX = this.left + 14 + this.font.width(this.title);
        var hints = this.font.split(Component.translatable("clear_f3.gui.expand_hint"), Math.max(1, this.right - hintX));
        if (!hints.isEmpty()) graphics.text(this.font, hints.get(0), hintX, 6, ClearF3GuiTheme.NOTICE, false);
        graphics.fill(this.left, this.footer - 8, this.right, this.footer - 7, ClearF3GuiTheme.BORDER);
        this.content.draw(graphics, mouseX, mouseY, partialTick);
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
    }

    //#if MC>=12002
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        return this.content.mouseScrolled(mouseX, mouseY, vertical) || super.mouseScrolled(mouseX, mouseY, horizontal, vertical);
    }
    //#else
    //$$ @Override
    //$$ public boolean mouseScrolled(double mouseX, double mouseY, double vertical) {
    //$$     return this.content.mouseScrolled(mouseX, mouseY, vertical) || super.mouseScrolled(mouseX, mouseY, vertical);
    //$$ }
    //#endif

    //#if MC>=12109
    @Override
    public boolean keyPressed(KeyEvent event) {
        if (this.selectedKey != null) {
            bindSelectedKey(event.key() == InputConstants.KEY_ESCAPE ? InputConstants.UNKNOWN : InputConstants.getKey(event));
            return true;
        }
        return super.keyPressed(event);
    }
    //#else
    //$$ @Override
    //$$ public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    //$$     if (this.selectedKey != null) {
    //$$         bindSelectedKey(keyCode == InputConstants.KEY_ESCAPE ? InputConstants.UNKNOWN : InputConstants.getKey(keyCode, scanCode));
    //$$         return true;
    //$$     }
    //$$     return super.keyPressed(keyCode, scanCode, modifiers);
    //$$ }
    //#endif

    //#if MC>=12109
    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (this.selectedKey != null) {
            bindSelectedKey(InputConstants.Type.MOUSE.getOrCreate(event.button()));
            return true;
        }
        return this.content.mouseClicked(event.x(), event.y(), event.button()) || super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        return this.content.mouseDragged(event.x(), event.y(), event.button()) || super.mouseDragged(event, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        return this.content.mouseReleased(event.button()) || super.mouseReleased(event);
    }
    //#else
    //$$ @Override
    //$$ public boolean mouseClicked(double x, double y, int button) {
    //$$     if (this.selectedKey != null) {
    //$$         bindSelectedKey(InputConstants.Type.MOUSE.getOrCreate(button));
    //$$         return true;
    //$$     }
    //$$     return this.content.mouseClicked(x, y, button) || super.mouseClicked(x, y, button);
    //$$ }
    //$$ @Override
    //$$ public boolean mouseDragged(double x, double y, int button, double dragX, double dragY) {
    //$$     return this.content.mouseDragged(x, y, button) || super.mouseDragged(x, y, button, dragX, dragY);
    //$$ }
    //$$ @Override
    //$$ public boolean mouseReleased(double x, double y, int button) {
    //$$     return this.content.mouseReleased(button) || super.mouseReleased(x, y, button);
    //$$ }
    //#endif
}

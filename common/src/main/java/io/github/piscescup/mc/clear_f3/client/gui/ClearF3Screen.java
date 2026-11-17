package io.github.piscescup.mc.clear_f3.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Shared navigation and background policy of local settings pages.
 *
 * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
 * Name: <b>Yuantong Ren</b>
 * @version 1.0.0
 * @since 1.0.0
 */
abstract class ClearF3Screen extends Screen {
    protected final Screen parent;

    protected ClearF3Screen(Screen parent, Component title) {
        super(title);
        this.parent = parent;
    }

    /**
     * Open a page through the GUI owner used by the current game version.
     */
    static void display(Screen screen) {
        Minecraft client = Minecraft.getInstance();
        //#if MC>=260200
        client.gui.setScreen(screen);
        //#else
        //$$ client.setScreen(screen);
        //#endif
    }

    /**
     * Settings pages supply their own dark background and panel.
     */
    @Override
    //#if MC>=260100
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
    //#elseif MC>=12002
    //$$ public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    //#else
    //$$ public void renderBackground(GuiGraphics graphics) {
    //#endif
    }

    @Override
    public void onClose() {
        display(this.parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    //#if MC>=12110
    @Override
    public boolean isInGameUi() {
        return this.minecraft.level != null;
    }
    //#endif
}

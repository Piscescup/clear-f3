package io.github.piscescup.mc.clear_f3.client;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.piscescup.mc.clear_f3.client.gui.ClearF3ConfigScreen;
//#if MC>=12109
import io.github.piscescup.mc.clear_f3.ClearF3References;
//#endif
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

/**
 * The configurable toggle key and its shared client input handler.
 *
 * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
 * Name: <b>Yuantong Ren</b>
 * @version 1.0.0
 * @since 1.0.0
 */
public final class ClearF3KeyMappings {
    /**
     * The category registered by the active loader in the controls menu.
     */
    //#if MC>=12109
    public static final KeyMapping.Category CATEGORY = new KeyMapping.Category(ClearF3References.id("main"));
    //#else
    //$$ public static final String CATEGORY = "key.categories.clear_f3";
    //#endif

    /**
     * The toggle key, which defaults to F8 and can be rebound in controls.
     */
    public static final KeyMapping TOGGLE = new KeyMapping("key.clear_f3.toggle", InputConstants.KEY_F8, CATEGORY);

    /**
     * The settings shortcut, independent of whether the overlay is enabled.
     */
    public static final KeyMapping OPEN_SETTINGS = new KeyMapping("key.clear_f3.open_settings", InputConstants.KEY_F9, CATEGORY);

    /**
     * Prevent construction of this input utility.
     */
    private ClearF3KeyMappings() {
    }

    /**
     * Consume clicks in every screen and apply toggles only during gameplay.
     *
     * @param minecraft the active client supplied by the loader's tick event
     */
    public static void handleInput(Minecraft minecraft) {
        while (TOGGLE.consumeClick()) {
            //#if MC>=260200
            if (minecraft.gui.screen() == null && minecraft.player != null) {
                //#else
                //$$ if (minecraft.screen == null && minecraft.player != null) {
                //#endif
                boolean enabled = ClearF3Config.toggle();
                Component message = Component.translatable(
                    enabled ? "clear_f3.message.enabled" : "clear_f3.message.disabled"
                );
                //#if MC>=260100
                minecraft.player.sendOverlayMessage(message);
                //#else
                //$$ minecraft.player.displayClientMessage(message, true);
                //#endif
            }
        }
        while (OPEN_SETTINGS.consumeClick()) {
            //#if MC>=260200
            if (minecraft.gui.screen() == null && minecraft.player != null) {
                minecraft.gui.setScreen(new ClearF3ConfigScreen(null));
            }
            //#else
            //$$ if (minecraft.screen == null && minecraft.player != null) {
            //$$     minecraft.setScreen(new ClearF3ConfigScreen(null));
            //$$ }
            //#endif
        }
    }
}

package io.github.piscescup.fabricmc.clear_f3.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.github.piscescup.mc.clear_f3.client.gui.ClearF3ConfigScreen;

/**
 * The optional Mod Menu settings entry for every supported Fabric version.
 *
 * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
 * Name: <b>Yuantong Ren</b>
 * @version 1.0.0
 * @since 1.0.0
 */
public final class ClearF3ModMenu implements ModMenuApi {
    /** @return a settings page that returns to the calling Mod Menu screen */
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ClearF3ConfigScreen::new;
    }
}

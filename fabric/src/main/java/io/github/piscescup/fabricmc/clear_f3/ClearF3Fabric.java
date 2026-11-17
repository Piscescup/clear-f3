package io.github.piscescup.fabricmc.clear_f3;

import io.github.piscescup.mc.clear_f3.ClearF3References;
import io.github.piscescup.mc.clear_f3.client.ClearF3Config;
import io.github.piscescup.mc.clear_f3.client.ClearF3KeyMappings;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
//#if MC>=260100
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
//#else
//$$ import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
//#endif
//#if MC>=12109
import net.minecraft.client.KeyMapping;
//#endif

/**
 * The Fabric client entrypoint for {@code Clear F3} mod.
 *
 * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
 * Name: <b>Yuantong Ren</b>
 * @version 1.0.0
 * @since 1.0.0
 */
public final class ClearF3Fabric implements ClientModInitializer {
    /**
     * Create the Fabric client entrypoint.
     */
    public ClearF3Fabric() {
    }

    /**
     * Initialize the shared client code after Fabric loads the mod.
     */
    @Override
    public void onInitializeClient() {
        ClearF3References.init();
        ClearF3Config.load(ClearF3FabricReferences.FABRIC_LOADER.getConfigDir());
        //#if MC>=12109
        KeyMapping.Category.register(ClearF3KeyMappings.CATEGORY.id());
        //#endif
        //#if MC>=260100
        KeyMappingHelper.registerKeyMapping(ClearF3KeyMappings.TOGGLE);
        KeyMappingHelper.registerKeyMapping(ClearF3KeyMappings.OPEN_SETTINGS);
        //#else
        //$$ KeyBindingHelper.registerKeyBinding(ClearF3KeyMappings.TOGGLE);
        //$$ KeyBindingHelper.registerKeyBinding(ClearF3KeyMappings.OPEN_SETTINGS);
        //#endif
        ClientTickEvents.END_CLIENT_TICK.register(ClearF3KeyMappings::handleInput);
    }
}

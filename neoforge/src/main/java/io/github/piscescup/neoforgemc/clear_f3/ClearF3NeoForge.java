package io.github.piscescup.neoforgemc.clear_f3;

import io.github.piscescup.mc.clear_f3.ClearF3References;
import io.github.piscescup.mc.clear_f3.client.ClearF3Config;
import io.github.piscescup.mc.clear_f3.client.ClearF3KeyMappings;
import io.github.piscescup.mc.clear_f3.client.gui.ClearF3ConfigScreen;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

/**
 * The NeoForge client entrypoint for {@code Clear F3} mod.
 *
 * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
 * Name: <b>Yuantong Ren</b>
 * @version 1.0.0
 * @since 1.0.0
 */
@Mod(value = ClearF3References.MOD_ID, dist = Dist.CLIENT)
public final class ClearF3NeoForge {
    /**
     * Initialize the shared client code when NeoForge constructs the mod.
     *
     * @param modBus the mod event bus supplied by NeoForge
     * @param container the mod container used to register the settings screen
     */
    public ClearF3NeoForge(IEventBus modBus, ModContainer container) {
        ClearF3References.init();
        ClearF3Config.load(FMLPaths.CONFIGDIR.get());
        container.registerExtensionPoint(
            IConfigScreenFactory.class,
            (mod, parent) -> new ClearF3ConfigScreen(parent)
        );
        modBus.addListener(this::registerKeyMappings);
        NeoForge.EVENT_BUS.addListener(this::onClientTick);
    }

    /**
     * Register the shared controls category and toggle key.
     *
     * @param event the client key-mapping registration event
     */
    private void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.registerCategory(ClearF3KeyMappings.CATEGORY);
        event.register(ClearF3KeyMappings.TOGGLE);
        event.register(ClearF3KeyMappings.OPEN_SETTINGS);
    }

    /**
     * Handle queued toggle clicks after the client updates.
     *
     * @param event the client tick completion event
     */
    private void onClientTick(ClientTickEvent.Post event) {
        ClearF3KeyMappings.handleInput(Minecraft.getInstance());
    }
}

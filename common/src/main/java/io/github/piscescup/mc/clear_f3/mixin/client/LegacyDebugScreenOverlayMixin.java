//#if MC<12109
//$$ package io.github.piscescup.mc.clear_f3.mixin.client;
//$$
//$$ import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
//$$ import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
//$$ import io.github.piscescup.mc.clear_f3.client.ClearF3Config;
//$$ import io.github.piscescup.mc.clear_f3.client.debug.DebugOverlay;
//$$ import net.minecraft.client.gui.GuiGraphics;
//$$ import net.minecraft.client.gui.components.DebugScreenOverlay;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.Unique;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$
//$$ import java.util.List;
//$$
//$$ /**
//$$  * The client integration for versions preceding the vanilla debug-entry API.
//$$  * <p>
//$$  * Only text passes are replaced. Vanilla retains data collection, reduced-debug
//$$  * restrictions, shortcut descriptions, chunk caches and performance charts.
//$$  *
//$$  * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
//$$  * Name: <b>Yuantong Ren</b>
//$$  * @version 1.0.0
//$$  * @since 1.0.0
//$$  */
//$$ @Mixin(DebugScreenOverlay.class)
//$$ public abstract class LegacyDebugScreenOverlayMixin {
//$$     /** The common collection, layout and rendering lifecycle. */
//$$     @Unique
//$$     private final DebugOverlay clearF3$overlay = new DebugOverlay();
//$$
//$$     /**
//$$      * Create the legacy integration state.
//$$      */
//$$     protected LegacyDebugScreenOverlayMixin() {
//$$     }
//$$
//$$     /**
//$$      * Collect each original column and draw the groups after both are available.
//$$      *
//$$      * @param overlay the vanilla debug overlay
//$$      * @param graphics the graphics used by the current text pass
//$$      * @param lines the original column, including additions made by other mods
//$$      * @param leftSide whether the first column is being collected
//$$      * @param original the replaced vanilla text operation
//$$      */
//$$     @WrapOperation(
//$$         method = {"drawGameInformation", "drawSystemInformation"},
//$$         at = @At(
//$$             value = "INVOKE",
//$$             target = "Lnet/minecraft/client/gui/components/DebugScreenOverlay;renderLines(Lnet/minecraft/client/gui/GuiGraphics;Ljava/util/List;Z)V"
//$$         )
//$$     )
//$$     private void clearF3$renderGroups(
//$$         DebugScreenOverlay overlay,
//$$         GuiGraphics graphics,
//$$         List<String> lines,
//$$         boolean leftSide,
//$$         Operation<Void> original
//$$     ) {
//$$         if (!ClearF3Config.isEnabled()) {
//$$             clearF3$overlay.invalidateLayout();
//$$             original.call(overlay, graphics, lines, leftSide);
//$$             return;
//$$         }
//$$         if (leftSide) {
//$$             clearF3$overlay.beginFrame();
//$$         }
//$$         clearF3$overlay.adapter().addLegacyLines(lines, leftSide);
//$$         if (!leftSide) {
//$$             clearF3$overlay.extractRenderState(graphics);
//$$         }
//$$     }
//$$ }
//#endif

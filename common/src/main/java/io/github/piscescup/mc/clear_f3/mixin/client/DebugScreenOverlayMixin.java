//#if MC>=12109
package io.github.piscescup.mc.clear_f3.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.piscescup.mc.clear_f3.client.ClearF3Config;
import io.github.piscescup.mc.clear_f3.client.debug.DebugOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * The client integration of the {@code Clear F3} overlay with vanilla debug rendering.
 * <p>
 * Vanilla retains entry selection, reduced-information restrictions, chunk access
 * and charts. Text uses the shared renderer's fixed scale.
 *
 * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
 * Name: <b>Yuantong Ren</b>
 * @version 1.0.0
 * @since 1.0.0
 */
@Mixin(DebugScreenOverlay.class)
public abstract class DebugScreenOverlayMixin {
    /** The common collection, layout and rendering lifecycle. */
    @Unique
    private final DebugOverlay clearF3$overlay = new DebugOverlay();

    /**
     * The vanilla entry-list version used by the previous frame.
     */
    @Unique
    private long clearF3$entriesVersion = Long.MIN_VALUE;

    /**
     * Create the integration state for the vanilla debug overlay.
     */
    protected DebugScreenOverlayMixin() {
    }

    /**
     * Begin a new collection and invalidate layouts after entry settings change.
     *
     * @param graphics the vanilla GUI render-state extractor
     * @param info the injection callback
     */
    //#if MC>=260100
    @Inject(method = "extractRenderState", at = @At("HEAD"))
    //#else
    //$$ @Inject(method = "render", at = @At("HEAD"))
    //#endif
    private void clearF3$beginFrame(GuiGraphicsExtractor graphics, CallbackInfo info) {
        if (!ClearF3Config.isEnabled()) {
            clearF3$overlay.invalidateLayout();
            return;
        }
        clearF3$overlay.beginFrame();
        long version = Minecraft.getInstance().debugEntries.getCurrentlyEnabledVersion();
        if (version != clearF3$entriesVersion) {
            clearF3$overlay.invalidateLayout();
            clearF3$entriesVersion = version;
        }
    }

    /**
     * Collect an enabled entry using structured rows or its original implementation.
     *
     * @param entry the entry selected by vanilla
     * @param displayer the original vanilla output collector
     * @param level the level selected by vanilla
     * @param clientChunk the cached client chunk, or {@code null} when unavailable
     * @param serverChunk the cached server chunk, or {@code null} when unavailable
     * @param original the next operation in the entry display chain
     * @param id the identifier of the currently enabled entry
     */
    @WrapOperation(
        //#if MC>=260100
        method = "extractRenderState",
        //#else
        //$$ method = "render",
        //#endif
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/debug/DebugScreenEntry;display(Lnet/minecraft/client/gui/components/debug/DebugScreenDisplayer;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/chunk/LevelChunk;Lnet/minecraft/world/level/chunk/LevelChunk;)V"
        )
    )
    private void clearF3$collectEntry(
        DebugScreenEntry entry,
        DebugScreenDisplayer displayer,
        Level level,
        LevelChunk clientChunk,
        LevelChunk serverChunk,
        Operation<Void> original,
        @Local Identifier id
    ) {
        if (!ClearF3Config.isEnabled()) {
            original.call(entry, displayer, level, clientChunk, serverChunk);
            return;
        }
        if (!clearF3$overlay.adapter().displayStructured(id, entry, level, clientChunk, serverChunk)) {
            original.call(entry, clearF3$overlay.adapter(), level, clientChunk, serverChunk);
        }
    }

    /**
     * Replace the two vanilla text passes with the independently scaled overlay.
     *
     * @param overlay the vanilla debug overlay receiving the text pass
     * @param graphics the vanilla GUI render-state extractor
     * @param lines the remaining vanilla shortcut descriptions
     * @param leftSide whether this is the first, left-column text pass
     * @param width the viewport width in debug GUI coordinates
     * @param original the original text-rendering operation
     */
    @WrapOperation(
        //#if MC>=260100
        method = "extractRenderState",
        //#else
        //$$ method = "render",
        //#endif
        at = @At(
            value = "INVOKE",
            //#if MC>=260300
            target = "Lnet/minecraft/client/gui/components/DebugScreenOverlay;extractLines(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Ljava/util/List;ZI)V"
            //#elseif MC>=260100
            //$$ target = "Lnet/minecraft/client/gui/components/DebugScreenOverlay;extractLines(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Ljava/util/List;Z)V"
            //#else
            //$$ target = "Lnet/minecraft/client/gui/components/DebugScreenOverlay;renderLines(Lnet/minecraft/client/gui/GuiGraphics;Ljava/util/List;Z)V"
            //#endif
        )
    )
    private void clearF3$renderGroups(
        DebugScreenOverlay overlay,
        GuiGraphicsExtractor graphics,
        List<String> lines,
        boolean leftSide,
        //#if MC>=260300
        int width,
        //#endif
        Operation<Void> original
    ) {
        if (!ClearF3Config.isEnabled()) {
            //#if MC>=260300
            original.call(overlay, graphics, lines, leftSide, width);
            //#else
            //$$ original.call(overlay, graphics, lines, leftSide);
            //#endif
            return;
        }
        if (leftSide) {
            clearF3$overlay.adapter().addHelp(lines);
            clearF3$overlay.extractRenderState(graphics);
        }
    }

    /**
     * Clear remembered group positions when vanilla resets the debug overlay.
     *
     * @param info the injection callback
     */
    @Inject(method = "reset", at = @At("HEAD"))
    private void clearF3$reset(CallbackInfo info) {
        clearF3$overlay.invalidateLayout();
    }
}
//#endif

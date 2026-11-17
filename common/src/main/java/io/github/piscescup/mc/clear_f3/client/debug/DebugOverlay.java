package io.github.piscescup.mc.clear_f3.client.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

/**
 * Shared collection and rendering lifecycle for legacy and modern F3 hooks.
 * The version-specific mixins only supply data through their available APIs.
 *
 * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
 * Name: <b>Yuantong Ren</b>
 * @since 1.0.0
 */
public final class DebugOverlay {
    private final DebugAdapter adapter = new DebugAdapter();
    private final DebugOverlayRenderer renderer = new DebugOverlayRenderer();
    private Object level;

    /** @return the collector used by both vanilla integration paths */
    public DebugAdapter adapter() { return adapter; }

    /** Begin collection, discarding previous placement when the world changes. */
    public void beginFrame() {
        Object currentLevel = Minecraft.getInstance().level;
        if (level != currentLevel) {
            invalidateLayout();
            level = currentLevel;
        }
        adapter.beginFrame();
    }

    /** Discard remembered dimensions and positions after entry settings change. */
    public void invalidateLayout() { renderer.clearLayoutCache(); }

    /**
     * Render the collected data at the common fixed scale.
     *
     * @param graphics the current GUI output
     */
    public void extractRenderState(GuiGraphicsExtractor graphics) {
        Minecraft minecraft = Minecraft.getInstance();
        renderer.extractAtFixedScale(graphics, minecraft.font, minecraft.getWindow(), adapter.groups());
    }
}

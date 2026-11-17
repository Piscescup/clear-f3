//#if MC>=260300
package io.github.piscescup.mc.clear_f3.client.debug.entry;

import io.github.piscescup.mc.clear_f3.client.debug.DebugGroups;
import net.minecraft.client.Minecraft;
import net.minecraft.client.TextureFilteringMethod;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.Objects;

/**
 * The structured data provider for {@code DebugEntrySimplePerformanceImpactors}.
 *
 * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
 * Name: <b>Yuantong Ren</b>
 * @version 1.0.0
 * @since 1.0.0
 */
public final class PerformanceOptionsDebugEntry implements DebugEntryProvider {
    /**
     * The destination of this entry's structured rows.
     */
    private final DebugEntryOutput output;

    /**
     * Create the data provider with the given row destination.
     *
     * @param output the destination of the collected rows
     */
    public PerformanceOptionsDebugEntry(DebugEntryOutput output) {
        this.output = Objects.requireNonNull(output, "output");
    }

    /**
     * Collect this entry's rows using the data already made available by vanilla.
     *
     * @param entry       the original vanilla entry
     * @param level       the server or client level, or {@code null} outside a world
     * @param clientChunk the cached client chunk, or {@code null} when unavailable
     * @param serverChunk the cached server chunk, or {@code null} when unavailable
     */
    @Override
    public void display(DebugScreenEntry entry, Level level, LevelChunk clientChunk, LevelChunk serverChunk) {
        displayPerformanceOptions();
    }

    /**
     * Display the active graphics options that affect rendering performance.
     */
    private void displayPerformanceOptions() {
        Minecraft minecraft = Minecraft.getInstance();
        var options = minecraft.options;
        TextureFilteringMethod filtering = options.textureFiltering().get();
        output.row(DebugGroups.PERFORMANCE_IMPACTORS, "transparency", Component.translatable(options.improvedTransparency().get()
            ? "clear_f3.value.enabled" : "clear_f3.value.disabled"));
        output.row(DebugGroups.PERFORMANCE_IMPACTORS, "clouds", options.cloudStatus().get().caption());
        output.row(DebugGroups.PERFORMANCE_IMPACTORS, "biome_blend", "%d", options.biomeBlendRadius().get());
        Component filteringValue = filtering.caption().copy();
        if (filtering == TextureFilteringMethod.ANISOTROPIC) {
            filteringValue = filteringValue.copy().append(" " + options.maxAnisotropyValue() + "x");
        }
        output.row(DebugGroups.PERFORMANCE_IMPACTORS, "filtering", filteringValue);
        output.row(DebugGroups.PERFORMANCE_IMPACTORS, "terrain_rendering", "%s", minecraft.levelRenderer.isChunkRenderingUsingMultiDrawIndirect()
            ? "multidrawindirect" : "naive");
    }
}
//#endif

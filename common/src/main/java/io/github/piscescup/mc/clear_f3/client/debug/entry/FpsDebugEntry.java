//#if MC>=260300
package io.github.piscescup.mc.clear_f3.client.debug.entry;

import io.github.piscescup.mc.clear_f3.client.debug.DebugGroups;
import com.mojang.blaze3d.platform.VideoMode;
import com.mojang.renderpearl.api.device.GpuSurface;

import java.util.Locale;
import java.util.Objects;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugEntryFps;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

/**
 * The structured data provider for {@code DebugEntryFps}.
 *
 * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
 * Name: <b>Yuantong Ren</b>
 * @version 1.0.0
 * @since 1.0.0
 */
public final class FpsDebugEntry implements DebugEntryProvider {
    /**
     * The destination of this entry's structured rows.
     */
    private final DebugEntryOutput output;

    /**
     * Create the data provider with the given row destination.
     *
     * @param output the destination of the collected rows
     */
    public FpsDebugEntry(DebugEntryOutput output) {
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
        displayFps();
    }

    /**
     * Display frame rate, frame limit, presentation mode and refresh rate.
     */
    private void displayFps() {
        Minecraft minecraft = Minecraft.getInstance();
        int limit = minecraft.getFramerateLimitTracker().getFramerateLimit();
        GpuSurface.PresentMode mode = minecraft.windowSurface().currentConfiguration()
            .map(GpuSurface.Configuration::presentMode).orElse(null);
        VideoMode videoMode = minecraft.getWindow().getActiveVideoMode();
        output.row(DebugGroups.LEFT_PRIORITY, "fps", "%d", minecraft.getFps());
        output.row(DebugGroups.LEFT_PRIORITY, "frame_limit", limit == 260 ? Component.translatable("clear_f3.value.unlimited")
            : Component.literal(Integer.toString(limit)).withStyle(ChatFormatting.WHITE));
        output.row(DebugGroups.LEFT_PRIORITY, "presentation", mode == null ? Component.translatable("clear_f3.value.unavailable")
            : Component.literal(mode.name().toLowerCase(Locale.ROOT).replace('_', ' ')).withStyle(ChatFormatting.WHITE));
        output.row(DebugGroups.LEFT_PRIORITY, "refresh_rate", "%s Hz", videoMode == null ? "0" : videoMode.refreshRateLabel());
    }
}
//#endif

//#if MC>=260300
package io.github.piscescup.mc.clear_f3.client.debug.entry;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.device.DeviceInfo;
import io.github.piscescup.mc.clear_f3.client.debug.DebugGroups;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugEntrySystemSpecs;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.Objects;

/**
 * The structured data provider for {@code DebugEntrySystemSpecs}.
 *
 * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
 * Name: <b>Yuantong Ren</b>
 * @version 1.0.0
 * @since 1.0.0
 */
public final class SystemSpecsDebugEntry implements DebugEntryProvider {
    /**
     * The destination of this entry's structured rows.
     */
    private final DebugEntryOutput output;

    /**
     * Create the data provider with the given row destination.
     *
     * @param output the destination of the collected rows
     */
    public SystemSpecsDebugEntry(DebugEntryOutput output) {
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
        displaySystem();
    }

    /**
     * Display device information using the vanilla cached CPU description.
     */
    private void displaySystem() {
        DeviceInfo device = RenderSystem.getDevice().getDeviceInfo();
        Window window = Minecraft.getInstance().getWindow();
        String deviceType = switch (device.type()) {
            case OTHER -> "";
            case INTEGRATED -> " (iGPU)";
            case DISCRETE -> " (dGPU)";
            case VIRTUAL -> " (vGPU)";
            case CPU -> " (software)";
        };
        output.row(DebugGroups.SYSTEM_SPECS, "java", "%s", System.getProperty("java.version"));
        output.row(DebugGroups.SYSTEM_SPECS, "cpu", "%s", DebugEntrySystemSpecs.getCpuInfo());
        output.row(DebugGroups.SYSTEM_SPECS, "display", "%d x %d (%s)", window.getWidth(), window.getHeight(), device.vendorName());
        output.row(DebugGroups.SYSTEM_SPECS, "window", "%d x %d", window.getScreenWidth(), window.getScreenHeight());
        output.row(DebugGroups.SYSTEM_SPECS, "pixel_density", "%.2fx", window.getPixelDensity());
        output.row(DebugGroups.SYSTEM_SPECS, "gpu_device", "%s%s", device.name(), deviceType);
        output.row(DebugGroups.SYSTEM_SPECS, "graphics_backend", "%s", device.backendName());
        output.row(DebugGroups.SYSTEM_SPECS, "graphics_driver", "%s", device.driverInfo().lines().findFirst().orElse(device.driverInfo()));
    }
}
//#endif

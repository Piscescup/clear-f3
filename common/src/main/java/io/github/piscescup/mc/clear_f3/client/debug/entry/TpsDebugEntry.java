//#if MC>=260300
package io.github.piscescup.mc.clear_f3.client.debug.entry;

import io.github.piscescup.mc.clear_f3.client.debug.DebugGroups;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.Locale;
import java.util.Objects;

/**
 * The structured data provider for {@code DebugEntryTps}.
 *
 * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
 * Name: <b>Yuantong Ren</b>
 * @version 1.0.0
 * @since 1.0.0
 */
public final class TpsDebugEntry implements DebugEntryProvider {
    /**
     * The destination of this entry's structured rows.
     */
    private final DebugEntryOutput output;

    /**
     * Create the data provider with the given row destination.
     *
     * @param output the destination of the collected rows
     */
    public TpsDebugEntry(DebugEntryOutput output) {
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
        displayServer(level);
    }

    /**
     * Display synchronized server status and local tick time when available.
     * <p>
     * Remote servers do not expose measured tick time through this entry.
     *
     * @param level the level supplied by vanilla
     */
    private void displayServer(Level level) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientPacketListener listener = minecraft.getConnection();
        if (listener == null || level == null) {
            return;
        }
        IntegratedServer server = minecraft.getSingleplayerServer();
        Connection connection = listener.getConnection();
        TickRateManager manager = level.tickRateManager();
        String state = manager.isSteppingForward() ? "stepping" : manager.isFrozen() ? "frozen" : "running";
        boolean sprinting = server != null && server.tickRateManager().isSprinting();
        if (sprinting) {
            state = "sprinting";
        }
        output.row(DebugGroups.SERVER, "server", server != null ? Component.translatable("clear_f3.value.integrated_server")
            : Component.literal(Objects.toString(listener.serverBrand(), "unknown")).withStyle(ChatFormatting.WHITE));
        output.row(DebugGroups.SERVER, "tick_state", Component.translatable("clear_f3.value." + state));
        if (server != null) {
            output.row(DebugGroups.SERVER, "tick_time", "%.1f ms", server.getCurrentSmoothedTickTime());
            output.row(DebugGroups.SERVER, "target_tick_time", sprinting ? Component.translatable("clear_f3.value.unlimited")
                : Component.literal(String.format(Locale.ROOT, "%.1f ms", manager.millisecondsPerTick())).withStyle(ChatFormatting.WHITE));
        }
        output.row(DebugGroups.SERVER, "packets_sent", "%.0f", connection.getAverageSentPackets());
        output.row(DebugGroups.SERVER, "packets_received", "%.0f", connection.getAverageReceivedPackets());
    }
}
//#endif

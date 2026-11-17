//#if MC>=260300
package io.github.piscescup.mc.clear_f3.client.debug;

import io.github.piscescup.mc.clear_f3.client.debug.entry.BiomeDebugEntry;
import io.github.piscescup.mc.clear_f3.client.debug.entry.ChunkGenerationDebugEntry;
import io.github.piscescup.mc.clear_f3.client.debug.entry.ChunkRenderStatsDebugEntry;
import io.github.piscescup.mc.clear_f3.client.debug.entry.ChunkSourceStatsDebugEntry;
import io.github.piscescup.mc.clear_f3.client.debug.entry.DayCountDebugEntry;
import io.github.piscescup.mc.clear_f3.client.debug.entry.DebugEntryOutput;
import io.github.piscescup.mc.clear_f3.client.debug.entry.DebugEntryProvider;
import io.github.piscescup.mc.clear_f3.client.debug.entry.DetailedMemoryDebugEntry;
import io.github.piscescup.mc.clear_f3.client.debug.entry.EntityRenderStatsDebugEntry;
import io.github.piscescup.mc.clear_f3.client.debug.entry.FpsDebugEntry;
import io.github.piscescup.mc.clear_f3.client.debug.entry.GpuUtilizationDebugEntry;
import io.github.piscescup.mc.clear_f3.client.debug.entry.HeightmapDebugEntry;
import io.github.piscescup.mc.clear_f3.client.debug.entry.LightDebugEntry;
import io.github.piscescup.mc.clear_f3.client.debug.entry.LocalDifficultyDebugEntry;
import io.github.piscescup.mc.clear_f3.client.debug.entry.LookingAtDebugEntry;
import io.github.piscescup.mc.clear_f3.client.debug.entry.LookingAtEntityDebugEntry;
import io.github.piscescup.mc.clear_f3.client.debug.entry.MemoryDebugEntry;
import io.github.piscescup.mc.clear_f3.client.debug.entry.ParticleRenderStatsDebugEntry;
import io.github.piscescup.mc.clear_f3.client.debug.entry.PerformanceOptionsDebugEntry;
import io.github.piscescup.mc.clear_f3.client.debug.entry.PlayerSpeedDebugEntry;
import io.github.piscescup.mc.clear_f3.client.debug.entry.PositionDebugEntry;
import io.github.piscescup.mc.clear_f3.client.debug.entry.PostEffectsDebugEntry;
import io.github.piscescup.mc.clear_f3.client.debug.entry.SectionPositionDebugEntry;
import io.github.piscescup.mc.clear_f3.client.debug.entry.SoundCacheDebugEntry;
import io.github.piscescup.mc.clear_f3.client.debug.entry.SoundMoodDebugEntry;
import io.github.piscescup.mc.clear_f3.client.debug.entry.SpawnCountsDebugEntry;
import io.github.piscescup.mc.clear_f3.client.debug.entry.SystemSpecsDebugEntry;
import io.github.piscescup.mc.clear_f3.client.debug.entry.TpsDebugEntry;
import io.github.piscescup.mc.clear_f3.client.debug.entry.VersionDebugEntry;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import net.minecraft.client.gui.components.debug.DebugEntryBiome;
import net.minecraft.client.gui.components.debug.DebugEntryChunkGeneration;
import net.minecraft.client.gui.components.debug.DebugEntryChunkRenderStats;
import net.minecraft.client.gui.components.debug.DebugEntryChunkSourceStats;
import net.minecraft.client.gui.components.debug.DebugEntryDayCount;
import net.minecraft.client.gui.components.debug.DebugEntryDetailedMemory;
import net.minecraft.client.gui.components.debug.DebugEntryEntityRenderStats;
import net.minecraft.client.gui.components.debug.DebugEntryFps;
import net.minecraft.client.gui.components.debug.DebugEntryGpuUtilization;
import net.minecraft.client.gui.components.debug.DebugEntryHeightmap;
import net.minecraft.client.gui.components.debug.DebugEntryLight;
import net.minecraft.client.gui.components.debug.DebugEntryLocalDifficulty;
import net.minecraft.client.gui.components.debug.DebugEntryLookingAt;
import net.minecraft.client.gui.components.debug.DebugEntryLookingAtEntity;
import net.minecraft.client.gui.components.debug.DebugEntryLookingAtEntityTags;
import net.minecraft.client.gui.components.debug.DebugEntryMemory;
import net.minecraft.client.gui.components.debug.DebugEntryNoop;
import net.minecraft.client.gui.components.debug.DebugEntryParticleRenderStats;
import net.minecraft.client.gui.components.debug.DebugEntryPlayerSpeed;
import net.minecraft.client.gui.components.debug.DebugEntryPosition;
import net.minecraft.client.gui.components.debug.DebugEntryPostEffects;
import net.minecraft.client.gui.components.debug.DebugEntrySectionPosition;
import net.minecraft.client.gui.components.debug.DebugEntrySimplePerformanceImpactors;
import net.minecraft.client.gui.components.debug.DebugEntrySoundCache;
import net.minecraft.client.gui.components.debug.DebugEntrySoundMood;
import net.minecraft.client.gui.components.debug.DebugEntrySpawnCounts;
import net.minecraft.client.gui.components.debug.DebugEntrySystemSpecs;
import net.minecraft.client.gui.components.debug.DebugEntryTps;
import net.minecraft.client.gui.components.debug.DebugEntryVersion;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

/**
 * The exact-class dispatcher for vanilla {@code Minecraft 26.3} debug entries.
 * <p>
 * Each provider owns its data collection and caches. Display groups do not
 * determine which providers run; vanilla still selects each enabled entry.
 *
 * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
 * Name: <b>Yuantong Ren</b>
 * @version 1.0.0
 * @since 1.0.0
 */
final class VanillaDebugEntries {
    /**
     * The providers indexed by their exact vanilla entry class.
     */
    private final Map<Class<? extends DebugScreenEntry>, DebugEntryProvider> providers = new LinkedHashMap<>();

    /**
     * Create independent providers that share the given row destination.
     *
     * @param destination the destination of each group definition and structured row
     */
    VanillaDebugEntries(BiConsumer<DebugGroup, DebugRow> destination) {
        Objects.requireNonNull(destination, "destination");
        DebugEntryOutput output = destination::accept;
        providers.put(DebugEntryPosition.class, new PositionDebugEntry(output));
        providers.put(DebugEntrySectionPosition.class, new SectionPositionDebugEntry(output));
        providers.put(DebugEntryPlayerSpeed.class, new PlayerSpeedDebugEntry(output));
        providers.put(DebugEntryBiome.class, new BiomeDebugEntry(output));
        providers.put(DebugEntryDayCount.class, new DayCountDebugEntry(output));
        providers.put(DebugEntryLocalDifficulty.class, new LocalDifficultyDebugEntry(output));
        providers.put(DebugEntryMemory.class, new MemoryDebugEntry(output));
        providers.put(DebugEntryDetailedMemory.class, new DetailedMemoryDebugEntry(output));
        providers.put(DebugEntrySystemSpecs.class, new SystemSpecsDebugEntry(output));
        providers.put(DebugEntryFps.class, new FpsDebugEntry(output));
        providers.put(DebugEntryVersion.class, new VersionDebugEntry(output));
        providers.put(DebugEntryTps.class, new TpsDebugEntry(output));
        providers.put(DebugEntryGpuUtilization.class, new GpuUtilizationDebugEntry(output));
        providers.put(DebugEntrySimplePerformanceImpactors.class, new PerformanceOptionsDebugEntry(output));
        providers.put(DebugEntryLight.class, new LightDebugEntry(output));
        providers.put(DebugEntryHeightmap.class, new HeightmapDebugEntry(output));
        providers.put(DebugEntryChunkRenderStats.class, new ChunkRenderStatsDebugEntry(output));
        providers.put(DebugEntryEntityRenderStats.class, new EntityRenderStatsDebugEntry(output));
        providers.put(DebugEntryParticleRenderStats.class, new ParticleRenderStatsDebugEntry(output));
        providers.put(DebugEntryChunkSourceStats.class, new ChunkSourceStatsDebugEntry(output));
        providers.put(DebugEntryChunkGeneration.class, new ChunkGenerationDebugEntry(output));
        providers.put(DebugEntrySpawnCounts.class, new SpawnCountsDebugEntry(output));
        providers.put(DebugEntrySoundMood.class, new SoundMoodDebugEntry(output));
        providers.put(DebugEntrySoundCache.class, new SoundCacheDebugEntry(output));
        providers.put(DebugEntryPostEffects.class, new PostEffectsDebugEntry(output));
        providers.put(DebugEntryLookingAt.BlockStateInfo.class, new LookingAtDebugEntry(output, false, false));
        providers.put(DebugEntryLookingAt.BlockTagInfo.class, new LookingAtDebugEntry(output, false, true));
        providers.put(DebugEntryLookingAt.FluidStateInfo.class, new LookingAtDebugEntry(output, true, false));
        providers.put(DebugEntryLookingAt.FluidTagInfo.class, new LookingAtDebugEntry(output, true, true));
        providers.put(DebugEntryLookingAtEntity.class, new LookingAtEntityDebugEntry(output, false));
        providers.put(DebugEntryLookingAtEntityTags.class, new LookingAtEntityDebugEntry(output, true));
        providers.put(DebugEntryNoop.class, (entry, level, clientChunk, serverChunk) -> { });
    }

    /**
     * Display an exact vanilla entry using its independent data provider.
     *
     * @param entry the entry selected by vanilla
     * @param level the server or client level, or {@code null} outside a world
     * @param clientChunk the cached client chunk, or {@code null} when unavailable
     * @param serverChunk the cached server chunk, or {@code null} when unavailable
     * @return {@code true} if an exact provider handled the entry
     */
    boolean display(DebugScreenEntry entry, Level level, LevelChunk clientChunk, LevelChunk serverChunk) {
        DebugEntryProvider provider = providers.get(entry.getClass());
        if (provider == null) {
            return false;
        }
        provider.display(entry, level, clientChunk, serverChunk);
        return true;
    }
}
//#endif

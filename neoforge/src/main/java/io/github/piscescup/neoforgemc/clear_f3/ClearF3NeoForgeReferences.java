package io.github.piscescup.neoforgemc.clear_f3;

import io.github.piscescup.mc.clear_f3.ClearF3References;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.IModInfo;

/**
 * The NeoForge mod list, container and metadata references for {@code Clear F3} mod.
 *
 * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
 * Name: <b>Yuantong Ren</b>
 * @version 1.0.0
 * @since 1.0.0
 */
public final class ClearF3NeoForgeReferences {
    /**
     * Prevent construction of this utility class.
     */
    private ClearF3NeoForgeReferences() {}

    /**
     * The loaded mod list supplied by NeoForge.
     */
    public static final ModList MOD_LIST_NEOFORGE = ModList.get();

    /**
     * The NeoForge mod container registered for {@code Clear F3} mod.
     */
    public static final ModContainer MOD_CONTAINER_NEOFORGE = MOD_LIST_NEOFORGE
        .getModContainerById(ClearF3References.MOD_ID)
        .orElseThrow();

    /**
     * The metadata supplied by the NeoForge mod container.
     */
    public static final IModInfo MOD_METADATA_NEOFORGE = MOD_CONTAINER_NEOFORGE
        .getModInfo();


    /**
     * The mod version formatted for display by NeoForge.
     */
    public static final String MOD_VERSION_NEOFORGE = MOD_METADATA_NEOFORGE
        .getVersion()
        .toString();
}

package io.github.piscescup.fabricmc.clear_f3;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;

/**
 * The Fabric loader, container and metadata references for {@code Clear F3} mod.
 *
 * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
 * Name: <b>Yuantong Ren</b>
 * @version 1.0.0
 * @since 1.0.0
 */
public final class ClearF3FabricReferences {
    /**
     * Prevent construction of this utility class.
     */
    private ClearF3FabricReferences() {}

    /**
     * The active Fabric loader instance.
     */
    public static final FabricLoader FABRIC_LOADER = FabricLoader.getInstance();

    /**
     * The Fabric mod container registered for {@code Clear F3} mod.
     */
    public static final ModContainer MOD_CONTAINER_FABRIC = FABRIC_LOADER
        .getModContainer("clear_f3")
        .orElseThrow();

    /**
     * The metadata supplied by the Fabric mod container.
     */
    public static final ModMetadata MOD_METADATA_FABRIC = MOD_CONTAINER_FABRIC
        .getMetadata();

    /**
     * The mod version formatted for display by Fabric.
     */
    public static final String MOD_VERSION_FABRIC = MOD_METADATA_FABRIC
        .getVersion()
        .getFriendlyString();
}

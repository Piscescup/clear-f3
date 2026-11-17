package io.github.piscescup.mc.clear_f3;

import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * The shared initialization and identifiers for {@code Clear F3} mod.
 *
 * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
 * Name: <b>Yuantong Ren</b>
 * @version 1.0.0
 * @since 1.0.0
 */
public final class ClearF3References {

	/**
	 * The unique namespace for {@code Clear F3} mod.
	 */
	public static final String MOD_ID = "clear_f3";

	/**
	 * The display name for {@code Clear F3} mod.
	 */
	public static final String MOD_NAME = "Clear F3";

	/**
	 * The version for {@code Clear F3} mod.
	 */
	public static final String MOD_VERSION = "1.0.0";

	/**
	 * The logger for {@code Clear F3} mod.
	 */
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

	/**
	 * The marker for mod initialization messages.
	 */
	private static final Marker INIT_MARKER = MarkerFactory.getMarker("Initiation");

	/**
	 * Prevent construction of this shared utility class.
	 */
	private ClearF3References() {
	}

	/**
	 * Initialize {@code Clear F3} mod from the active loader's client entrypoint.
	 */
	public static void init() {
		LOGGER.info(INIT_MARKER, "Thank you for using {}, version: {}", MOD_NAME, MOD_VERSION);
		LOGGER.info(INIT_MARKER,
		"""
		
		Contact:
		     - GitHub: https://github.com/Piscescup
		     - Email: piscescup@outlook.com
		""");
	}

	/**
	 * Create a new {@link Identifier} with the namespace of {@code Clear F3} mod.
	 *
	 * @param path the path of the identifier
	 * @return an {@link Identifier} with the given {@code path} under {@link #MOD_ID}
	 */
	public static Identifier id(String path) {
		//#if MC>=12101
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
		//#else
		//$$ return new ResourceLocation(MOD_ID, path);
		//#endif
	}

    /**
     * Create an identifier in the vanilla Minecraft namespace.
     *
     * @param path the path in the vanilla namespace
     * @return the vanilla identifier for the given path
     */
    public static Identifier minecraftId(String path) {
        //#if MC>=12101
        return Identifier.withDefaultNamespace(path);
        //#else
        //$$ return new ResourceLocation("minecraft", path);
        //#endif
    }
}

package io.github.piscescup.fabricmc.clear_f3;

import io.github.piscescup.fabricmc.clear_f3.datagen.lang.ENUSLanguageProvider;
import io.github.piscescup.fabricmc.clear_f3.datagen.lang.ZHCNLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

/**
 * The data generator entrypoint for {@code Clear F3} mod.
 *
 * @author Github: <a href="https://github.com/Piscescup">Pisces Cup</a><br>
 * Name: <b>Yuantong Ren</b>
 * @version 1.0.0
 * @since 1.0.0
 */
public final class ClearF3DataGenerator implements DataGeneratorEntrypoint {
	/**
	 * Create the data generator entrypoint for {@code Clear F3} mod.
	 */
	public ClearF3DataGenerator() {
	}

	/**
	 * Register the data providers for {@code Clear F3} mod.
	 *
	 * @param fabricDataGenerator the generator supplied by Fabric
	 */
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(ENUSLanguageProvider::new);
		pack.addProvider(ZHCNLanguageProvider::new);
	}
}

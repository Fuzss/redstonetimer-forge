package fuzs.redstonetimer;

import fuzs.redstonetimer.data.ModLanguageProvider;
import fuzs.redstonetimer.data.ModLootTableProvider;
import fuzs.redstonetimer.data.ModRecipeProvider;
import fuzs.redstonetimer.registry.ModRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(RedstoneTimer.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class RedstoneTimer {
	public static final String MOD_ID = "redstonetimer";
	public static final String MOD_NAME = "Redstone Timer";
	public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

	@SubscribeEvent
	public static void onConstructMod(final FMLConstructModEvent evt) {
		ModRegistry.touch();
	}

	@SubscribeEvent
	public static void onGatherData(final GatherDataEvent evt) {
		DataGenerator generator = evt.getGenerator();
		final ExistingFileHelper existingFileHelper = evt.getExistingFileHelper();
		generator.addProvider(new ModLootTableProvider(generator, MOD_ID));
		generator.addProvider(new ModRecipeProvider(generator));
		generator.addProvider(new ModLanguageProvider(generator, MOD_ID));
	}
}

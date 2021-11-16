package fuzs.redstonetimer;

import fuzs.puzzleslib.network.NetworkHandler;
import fuzs.redstonetimer.registry.ModRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(RedstoneTimer.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class RedstoneTimer {
	public static final String MOD_ID = "redstonetimer";
	public static final String MOD_NAME = "Redstone Timer";
	public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

	public static final NetworkHandler NETWORK = NetworkHandler.of(MOD_ID);

	@SubscribeEvent
	public static void onConstructMod(final FMLConstructModEvent evt) {
		ModRegistry.touch();
	}
}

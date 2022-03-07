package fuzs.redstonetimer.client;

import fuzs.redstonetimer.RedstoneTimer;
import fuzs.redstonetimer.client.registry.ModClientRegistry;
import fuzs.redstonetimer.client.render.block.entity.TimerRenderer;
import fuzs.redstonetimer.client.screen.TimerScreen;
import fuzs.redstonetimer.registry.ModRegistry;
import fuzs.redstonetimer.world.inventory.TimerMenu;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = RedstoneTimer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class RedstoneTimerClient {
    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent evt) {
        MenuScreens.register(ModRegistry.TIMER_MENU_TYPE.get(), (TimerMenu menu, Inventory inventory, Component title) -> new TimerScreen(menu));
        BlockEntityRenderers.register(ModRegistry.TIMER_BLOCK_ENTITY_TYPE.get(), TimerRenderer::new);
        ItemBlockRenderTypes.setRenderLayer(ModRegistry.TIMER_BLOCK.get(), RenderType.cutout());
    }

    @SubscribeEvent
    public static void onRegisterLayerDefinitions(final EntityRenderersEvent.RegisterLayerDefinitions evt) {
        evt.registerLayerDefinition(ModClientRegistry.TIMER, TimerRenderer::getTexturedModelData);
    }
}

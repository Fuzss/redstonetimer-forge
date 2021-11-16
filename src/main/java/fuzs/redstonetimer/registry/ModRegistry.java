package fuzs.redstonetimer.registry;

import fuzs.puzzleslib.registry.RegistryManager;
import fuzs.redstonetimer.RedstoneTimer;
import fuzs.redstonetimer.block.TimerBlock;
import fuzs.redstonetimer.block.entity.TimerBlockEntity;
import fuzs.redstonetimer.world.inventory.TimerMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.fmllegacy.RegistryObject;

public class ModRegistry {
    private static final RegistryManager REGISTRY = RegistryManager.of(RedstoneTimer.MOD_ID);
    public static final RegistryObject<Block> TIMER_BLOCK = REGISTRY.registerBlock("timer", () -> new TimerBlock(BlockBehaviour.Properties.of(Material.DECORATION).instabreak().sound(SoundType.WOOD)));
    public static final RegistryObject<Item> TIMER_ITEM = REGISTRY.registerBlockItem("timer", CreativeModeTab.TAB_REDSTONE);
    public static final RegistryObject<BlockEntityType<TimerBlockEntity>> TIMER_BLOCK_ENTITY_TYPE = REGISTRY.registerRawBlockEntityType("timer", () -> BlockEntityType.Builder.of(TimerBlockEntity::new, TIMER_BLOCK.get()));
    public static final RegistryObject<MenuType<TimerMenu>> TIMER_MENU_TYPE = REGISTRY.registerRawMenuType("timer", () -> (int containerId, Inventory inventory) -> new TimerMenu(containerId));

    public static void touch() {

    }
}

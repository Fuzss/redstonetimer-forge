package fuzs.redstonetimer.data;

import fuzs.redstonetimer.registry.ModRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(DataGenerator p_125973_) {
        super(p_125973_);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> p_176532_) {
        ShapedRecipeBuilder.shaped(ModRegistry.TIMER_BLOCK.get())
                .define('T', Blocks.REDSTONE_TORCH)
                .define('Q', Items.QUARTZ)
                .define('S', Blocks.STONE)
                .pattern(" T ")
                .pattern("QTQ")
                .pattern("SSS")
                .unlockedBy("has_quartz", has(Items.QUARTZ))
                .save(p_176532_);
    }
}

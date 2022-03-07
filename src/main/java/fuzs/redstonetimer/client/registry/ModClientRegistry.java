package fuzs.redstonetimer.client.registry;

import fuzs.puzzleslib.client.model.geom.ModelLayerRegistry;
import fuzs.redstonetimer.RedstoneTimer;
import net.minecraft.client.model.geom.ModelLayerLocation;

public class ModClientRegistry {
    private static final ModelLayerRegistry LAYER_REGISTRY = ModelLayerRegistry.of(RedstoneTimer.MOD_ID);
    public static final ModelLayerLocation TIMER = LAYER_REGISTRY.register("timer");
}

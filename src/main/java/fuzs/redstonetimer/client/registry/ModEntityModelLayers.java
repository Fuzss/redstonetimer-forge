package fuzs.redstonetimer.client.registry;

import fuzs.redstonetimer.RedstoneTimer;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class ModEntityModelLayers {
    public static final ModelLayerLocation TIMER = register("timer");

    private static ModelLayerLocation register(String id) {
        return register(id, "main");
    }

    private static ModelLayerLocation register(String id, String layer) {
        return new ModelLayerLocation(new ResourceLocation(RedstoneTimer.MOD_ID, id), layer);
    }
}

package fuzs.redstonetimer.client.render.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.redstonetimer.client.registry.ModClientRegistry;
import fuzs.redstonetimer.world.level.block.TimerBlock;
import fuzs.redstonetimer.world.level.block.entity.TimerBlockEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

public class TimerRenderer implements BlockEntityRenderer<TimerBlockEntity> {
    public static final Material TIMER_POINTER_TEXTURE = new Material(InventoryMenu.BLOCK_ATLAS, new ResourceLocation("block/stone"));
    private static final String TIMER_POINTER = "timer_pointer";
    private final ModelPart timerPointer;

    public TimerRenderer(BlockEntityRendererProvider.Context ctx) {
        ModelPart modelPart = ctx.bakeLayer(ModClientRegistry.TIMER);
        this.timerPointer = modelPart.getChild(TIMER_POINTER);
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartDataRoot = modelData.getRoot();
        PartDefinition modelPartDataMiddle = modelPartDataRoot.addOrReplaceChild(TIMER_POINTER, CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, 7.0F, -2.0F, 4.0F, 2.0F, 6.0F), PartPose.offset(8.0F, 0.0F, 8.0F));
        // 8^(1/2)
        final float width = 2.8284271F;
        modelPartDataMiddle.addOrReplaceChild("timer_pointer_front", CubeListBuilder.create().texOffs(0, 0).addBox(-width / 2.0F, 7.0F, -width / 2.0F, width, 2.0F, width, new CubeDeformation(-0.001F)), PartPose.offsetAndRotation(0.0F, 0.0F, 4.0F, 0.0F, (float) (Math.PI / 4), 0.0F));
        modelPartDataMiddle.addOrReplaceChild("timer_pointer_back", CubeListBuilder.create().texOffs(0, 0).addBox(-width / 2.0F, 7.0F, -width / 2.0F, width, 2.0F, width, new CubeDeformation(-0.001F)), PartPose.offsetAndRotation(0.0F, 0.0F, -2.0F, 0.0F,(float) (Math.PI / 4), 0.0F));
        return LayerDefinition.create(modelData, 16, 16);
    }

    @Override
    public void render(TimerBlockEntity timerBlockEntity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        Direction direction = timerBlockEntity.getBlockState().getValue(TimerBlock.FACING);
        float rotationAngle = timerBlockEntity.getRotationAngle(tickDelta) - direction.toYRot();
        rotationAngle *= 0.017453292F;
        this.timerPointer.yRot = rotationAngle;
        VertexConsumer vertexConsumer = TIMER_POINTER_TEXTURE.buffer(vertexConsumers, RenderType::entitySolid);
        this.timerPointer.render(matrices, vertexConsumer, light, overlay);
    }
}

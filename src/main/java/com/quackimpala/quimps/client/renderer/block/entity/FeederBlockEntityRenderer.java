package com.quackimpala.quimps.client.renderer.block.entity;

import com.quackimpala.quimps.block.FeederBlock;
import com.quackimpala.quimps.block.entity.FeederBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class FeederBlockEntityRenderer implements BlockEntityRenderer<FeederBlockEntity> {
    protected static final double TO_PX = 0.0625;
    private static final Vec3d NORTH_OFFSET = new Vec3d(TO_PX * 8.0, TO_PX * 3.5, TO_PX * 2.5);
    private static final Vec3d SOUTH_OFFSET = new Vec3d(TO_PX * 8.0, TO_PX * 3.5, TO_PX * 13.5);
    private static final Vec3d WEST_OFFSET = new Vec3d(TO_PX * 2.5, TO_PX * 3.5, TO_PX * 8.0);
    private static final Vec3d EAST_OFFSET = new Vec3d(TO_PX * 13.5, TO_PX * 3.5, TO_PX * 8.0);

    private final ItemRenderer itemRenderer;

    public FeederBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        itemRenderer = ctx.getItemRenderer();
    }

    @Override
    public void render(FeederBlockEntity feeder, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        final ItemStack stack = feeder.getStack();
        if (stack.isEmpty())
            return;

        matrices.push();
        final Direction facing = feeder.getCachedState().get(FeederBlock.FACING);
        final Vec3d offset = getPosOffset(facing);
        matrices.translate(offset.getX(), offset.getY(), offset.getZ());
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f - facing.asRotation()));
        matrices.scale(0.3f, 0.3f, 0.3f);

        itemRenderer.renderItem(stack, ModelTransformationMode.FIXED, light, overlay, matrices, vertexConsumers, feeder.getWorld(), 0);
        matrices.pop();
    }

    protected Vec3d getPosOffset(Direction facing) {
        return switch (facing) {
            default -> NORTH_OFFSET;
            case SOUTH -> SOUTH_OFFSET;
            case WEST -> WEST_OFFSET;
            case EAST -> EAST_OFFSET;
        };
    }
}

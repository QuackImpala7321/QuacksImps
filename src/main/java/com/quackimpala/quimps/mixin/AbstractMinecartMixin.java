package com.quackimpala.quimps.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.quackimpala.quimps.acc.PoweredRailMixinAccessor;
import com.quackimpala.quimps.acc.PoweredRailMixinAccessor.PowerDirection;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(AbstractMinecartEntity.class)
public abstract class AbstractMinecartMixin {
    @Shadow
    protected abstract boolean willHitBlockAt(BlockPos pos);

    @Unique
    private BlockState stateBelow;
    @Unique
    private double addedX;
    @Unique
    private double addedZ;

    @Inject(
            method = "moveOnRail",
            at = @At("HEAD")
    )
    private void moveOnRailMixin(BlockPos pos, BlockState state, CallbackInfo ci) {
        stateBelow = state;
        addedX = 0.0d;
        addedZ = 0.0d;
    }

    @Inject(
            method = "moveOnRail",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;setVelocity(DDD)V",
                    ordinal = 1,
                    shift = At.Shift.BEFORE
            )
    )
    private void standStillOnPoweredRailMixin(BlockPos pos, BlockState state, CallbackInfo ci, @Local RailShape railShape) {
        final PowerDirection direction = state.get(PoweredRailMixinAccessor.MOMENTUM);
        if (railShape == RailShape.EAST_WEST) {
            if (willHitBlockAt(pos.west()) || willHitBlockAt(pos.east()))
                return;
            addedX = 0.02d * direction.toInt();
        }
        else if (railShape == RailShape.NORTH_SOUTH) {
            if (willHitBlockAt(pos.north()) || willHitBlockAt(pos.south()))
                return;
            addedZ = 0.02d * direction.toInt();
        }
    }

    @ModifyArgs(
            method = "moveOnRail",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;setVelocity(DDD)V",
                    ordinal = 1
            )
    )
    private void setVelocityMixin(Args args) {
        args.set(0, (double) args.get(0) + addedX);
        args.set(2, (double) args.get(2) + addedZ);
    }

    @ModifyArg(
            method = "moveOnRail",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V",
                    ordinal = 9
            )
    )
    private Vec3d moveOnPoweredRailMixin(Vec3d velocity) {
        if (stateBelow == null || !stateBelow.contains(PoweredRailMixinAccessor.MOMENTUM))
            return velocity;

        final PowerDirection direction = stateBelow.get(PoweredRailMixinAccessor.MOMENTUM);
        final int i = direction.toInt();
        if (i == 0)
            return velocity;

        final Vec3d abs = absVec3d(velocity);
        return abs.multiply(i);
    }

    @Unique
    private static Vec3d absVec3d(Vec3d vec) {
        return new Vec3d(
                Math.abs(vec.getX()),
                Math.abs(vec.getY()),
                Math.abs(vec.getZ())
        );
    }
}

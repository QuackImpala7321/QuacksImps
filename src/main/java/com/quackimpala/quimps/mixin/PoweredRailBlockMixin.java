package com.quackimpala.quimps.mixin;

import com.quackimpala.quimps.acc.PoweredRailMixinAccessor;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PoweredRailBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PoweredRailBlock.class)
public abstract class PoweredRailBlockMixin extends AbstractRailBlock implements PoweredRailMixinAccessor {
    protected PoweredRailBlockMixin(boolean forbidCurves, Settings settings) {
        super(forbidCurves, settings);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        final boolean sneaking = player.isSneaking();
        if (!sneaking)
            return ActionResult.PASS;

        world.setBlockState(pos, state.cycle(MOMENTUM));
        return ActionResult.success(world.isClient());
    }

    @Inject(
            method = "appendProperties",
            at = @At("TAIL")
    )
    private void appendPropertiesMixin(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(MOMENTUM);
    }

    @ModifyArg(
            method = "<init>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/PoweredRailBlock;setDefaultState(Lnet/minecraft/block/BlockState;)V"),
            index = 0
    )
    private BlockState defaultStateMixin(BlockState state) {
        return state.with(MOMENTUM, PowerDirection.NEUTRAL);
    }
}

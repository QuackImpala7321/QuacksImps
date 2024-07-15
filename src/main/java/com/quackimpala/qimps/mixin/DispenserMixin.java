package com.quackimpala.qimps.mixin;

import com.quackimpala.qimps.DispenserMixinAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.state.StateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DispenserBlock.class)
public abstract class DispenserMixin implements DispenserMixinAccessor {
    @Inject(
            method = "appendProperties",
            at = @At("TAIL")
    )
    private void appendPropertiesMixin(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(WAXED);
    }

    @ModifyArg(
            method = "<init>(Lnet/minecraft/block/AbstractBlock$Settings;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/DispenserBlock;setDefaultState(Lnet/minecraft/block/BlockState;)V"),
            index = 0
    )
    private BlockState defaultStateMixin(BlockState state) {
        return state.with(WAXED, false);
    }

    @Override
    public boolean isWaxed(BlockState state) {
        return state.get(WAXED);
    }
}

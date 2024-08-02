package com.quackimpala.quimps.mixin;

import com.quackimpala.quimps.DispenserMixinAccessor;
import com.quackimpala.quimps.QuacksImps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.item.ItemConvertible;
import net.minecraft.state.StateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DispenserBlock.class)
public abstract class DispenserBlockMixin implements DispenserMixinAccessor {
    @Inject(
            method = "appendProperties",
            at = @At("TAIL")
    )
    private void appendPropertiesMixin(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(WAXED);
    }

    @ModifyArg(
            method = "<init>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/DispenserBlock;setDefaultState(Lnet/minecraft/block/BlockState;)V"),
            index = 0
    )
    private BlockState defaultStateMixin(BlockState state) {
        return state.with(WAXED, false);
    }
}

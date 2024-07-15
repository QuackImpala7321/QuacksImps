package com.quackimpala.qimps.mixin;

import com.quackimpala.qimps.block.dispenser.BucketItemDispenserBehavior;
import net.minecraft.block.dispenser.DispenserBehavior;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(DispenserBehavior.class)
public interface DispenserBehaviorMixin {
    @ModifyArg(
            method = "registerDefaults",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/block/DispenserBlock;registerBehavior(Lnet/minecraft/item/ItemConvertible;Lnet/minecraft/block/dispenser/DispenserBehavior;)V",
                    ordinal = 51),
            index = 1)
    private static DispenserBehavior bucketBehaviorMixin(DispenserBehavior behavior) {
        return new BucketItemDispenserBehavior();
    }
}

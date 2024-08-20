package com.quackimpala.quimps.mixin;

import com.quackimpala.quimps.acc.DispenserMixinAccessor;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DispenserBlockEntity.class)
public abstract class DispenserBlockEntityMixin {
    @Inject(
            method = "chooseNonEmptySlot",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/entity/DispenserBlockEntity;generateLoot(Lnet/minecraft/entity/player/PlayerEntity;)V",
                    shift = At.Shift.AFTER
            ),
            cancellable = true)
    private void chooseSlotMixin(Random random, CallbackInfoReturnable<Integer> cir) {
        final DispenserBlockEntity blockEntity = DispenserBlockEntity.class.cast(this);
        if (!blockEntity.getCachedState().get(DispenserMixinAccessor.WAXED))
            return;

        final DefaultedList<ItemStack> inventory = blockEntity.getHeldStacks();
        for (int i = 0; i < inventory.size(); i++) {
            if (!inventory.get(i).isEmpty()) {
                cir.setReturnValue(i);
                return;
            }
        }

        cir.setReturnValue(-1);
    }
}

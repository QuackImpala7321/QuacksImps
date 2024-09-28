package com.quackimpala.quimps.mixin;

import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Leashable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BeehiveBlockEntity.class)
public class BeehiveBlockEntityMixin {
    @Inject(
            method = "tryEnterHive",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/BeehiveBlockEntity;addBee(Lnet/minecraft/block/entity/BeehiveBlockEntity$BeeData;)V", shift = At.Shift.BEFORE)
    )
    private void tryEnterHiveMixin(Entity entity, CallbackInfo ci) {
        if (!(entity instanceof Leashable leashable))
            return;

        leashable.detachLeash();
    }
}

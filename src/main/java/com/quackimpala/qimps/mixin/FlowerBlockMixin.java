package com.quackimpala.qimps.mixin;

import com.quackimpala.qimps.block.dispenser.ModDispenserBehaviors;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.FlowerBlock;
import net.minecraft.component.type.SuspiciousStewEffectsComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FlowerBlock.class)
public abstract class FlowerBlockMixin {
    @Inject(
            method = "<init>(Lnet/minecraft/component/type/SuspiciousStewEffectsComponent;Lnet/minecraft/block/AbstractBlock$Settings;)V",
            at = @At("TAIL")
    )
    private void initMixin(SuspiciousStewEffectsComponent stewEffects, AbstractBlock.Settings settings, CallbackInfo ci) {
        ModDispenserBehaviors.queueFlowerBehavior(FlowerBlock.class.cast(this));
    }
}

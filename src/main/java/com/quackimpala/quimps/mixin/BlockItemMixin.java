package com.quackimpala.quimps.mixin;

import com.quackimpala.quimps.registry.QIDispenserBehaviors;
import net.minecraft.block.Block;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.FlowerBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {
    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    private void initMixin(Block block, Item.Settings settings, CallbackInfo ci) {
        if (!(block instanceof FlowerBlock flower))
            return;

        DispenserBlock.registerBehavior(flower, QIDispenserBehaviors.FLOWER_BEHAVIOR);
    }
}

package com.quackimpala.quimps.mixin.dispenser_behavior;

import com.quackimpala.quimps.util.CauldronUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.block.dispenser.DispenserBehavior$6")
public abstract class GlassBottleItemDispenserBehavior extends FallibleItemDispenserBehavior {
    @Shadow protected abstract ItemStack replace(BlockPointer pointer, ItemStack oldStack, ItemStack newStack);

    @Inject(
            method = "dispenseSilently",
            at = @At("HEAD"),
            cancellable = true
    )
    private void dispenseMixin(BlockPointer pointer, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        final ServerWorld world = pointer.world();
        final BlockPos targetPos = pointer.pos().offset(pointer.state().get(DispenserBlock.FACING));
        final BlockState targetState = world.getBlockState(targetPos);

        setSuccess(CauldronUtil.tryInteractCauldron(world, targetPos, targetState, stack, pointer.blockEntity()));
        if (!isSuccess())
            return;

        setSuccess(true);
        cir.setReturnValue(
                replace(pointer, stack, PotionContentsComponent.createStack(Items.POTION, Potions.WATER)));
    }
}

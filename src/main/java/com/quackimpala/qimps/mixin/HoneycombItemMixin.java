package com.quackimpala.qimps.mixin;

import com.quackimpala.qimps.DispenserMixinAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.item.HoneycombItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HoneycombItem.class)
public abstract class HoneycombItemMixin {
    @Inject(
            method = "useOnBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/HoneycombItem;getWaxedState(Lnet/minecraft/block/BlockState;)Ljava/util/Optional;",
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    private void useOnBlockMixin(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        final World world = context.getWorld();
        final BlockState state = world.getBlockState(context.getBlockPos());
        if (!(state.getBlock() instanceof DispenserMixinAccessor acc))
            return;

        if (acc.isWaxed(state)) {
            cir.setReturnValue(ActionResult.PASS);
            return;
        }

        acc.wax(context.getWorld(), context.getBlockPos(), state, context.getPlayer(), context.getStack());
        cir.setReturnValue(
                ActionResult.success(world.isClient));
    }
}

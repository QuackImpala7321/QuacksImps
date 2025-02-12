package com.quackimpala.quimps.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.item.DecorationItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DecorationItem.class)
public abstract class DecorationItemMixin {
    @Inject(
            method = "useOnBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;getOrDefault(Lnet/minecraft/component/ComponentType;Ljava/lang/Object;)Ljava/lang/Object;",
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    private void useOnBlockMixin(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir, @Local ItemStack stack, @Local World world, @Local AbstractDecorationEntity decor) {
        if (context.getPlayer() != null) return;

        final NbtComponent nbt = stack.getOrDefault(DataComponentTypes.ENTITY_DATA, NbtComponent.DEFAULT);
        if (!nbt.isEmpty()) {
            EntityType.loadFromEntityNbt(world, null, decor, nbt);
        }

        if (decor.canStayAttached()) {
            if (!world.isClient) {
                decor.onPlace();
                world.emitGameEvent(null, GameEvent.ENTITY_PLACE, decor.getPos());
                world.spawnEntity(decor);
            }

            stack.decrement(1);
            cir.setReturnValue(ActionResult.success(world.isClient));
        } else {
            cir.setReturnValue(ActionResult.CONSUME);
        }
    }
}

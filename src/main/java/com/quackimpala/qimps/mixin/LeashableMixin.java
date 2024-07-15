package com.quackimpala.qimps.mixin;

import com.quackimpala.qimps.LastLeashDataAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.Leashable;
import net.minecraft.entity.Leashable.LeashData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Leashable.class)
public interface LeashableMixin extends LastLeashDataAccessor {
    @Redirect(
            method = "detachLeash(Lnet/minecraft/entity/Entity;ZZ)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;dropItem(Lnet/minecraft/item/ItemConvertible;)Lnet/minecraft/entity/ItemEntity;")
    )
    private static ItemEntity dropLeashMixin(Entity droppingEntity, ItemConvertible item) {
        if (!(droppingEntity instanceof LastLeashDataAccessor acc))
            return droppingEntity.dropItem(item);

        final LeashData lastData = acc.getLastLeashData();
        if (lastData == null)
            return droppingEntity.dropItem(item);

        final Entity holder = acc.getLastLeashData().leashHolder;
        if (holder instanceof PlayerEntity player) {
            player.giveItemStack(new ItemStack(item));
            return null;
        }
        return droppingEntity.dropItem(item);
    }

    @Inject(
            method = "detachLeash(Lnet/minecraft/entity/Entity;ZZ)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Leashable;setLeashData(Lnet/minecraft/entity/Leashable$LeashData;)V", shift = At.Shift.BEFORE)
    )
    private static <E extends Entity & Leashable> void beforeDetachMixin(E entity, boolean sendPacket, boolean dropItem, CallbackInfo ci) {
        if (!(entity instanceof LastLeashDataAccessor acc))
            return;

        acc.setLastLeashData(entity.getLeashData());
    }
}

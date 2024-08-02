package com.quackimpala.quimps.mixin;

import com.quackimpala.quimps.LastLeashDataAccessor;
import com.quackimpala.quimps.registry.ModSoundEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.Leashable;
import net.minecraft.entity.Leashable.LeashData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
        if (lastData == null || lastData.leashHolder == null) {
            acc.setLastLeashData(null);
            return droppingEntity.dropItem(item);
        }

        final Entity holder = lastData.leashHolder;
        final World world = holder.getWorld();
        if (holder instanceof PlayerEntity player) {
            final ItemEntity itemEntity = new ItemEntity(world,
                    player.getX(), player.getY(), player.getZ(),
                    new ItemStack(item));

            itemEntity.resetPickupDelay();
            world.spawnEntity(itemEntity);
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

    @Inject(
            method = "tickLeash",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Leashable;detachLeash()V", shift = At.Shift.AFTER)
    )
    private static <E extends Entity & Leashable> void tickLeashMixin(E entity, CallbackInfo ci) {
        entity.playSound(ModSoundEvents.LEASH_BREAK, 1.0f, 1.0f);
    }
}

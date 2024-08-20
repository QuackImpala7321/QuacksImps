package com.quackimpala.quimps.mixin;

import com.quackimpala.quimps.acc.LastLeashDataAccessor;
import net.minecraft.entity.Leashable.LeashData;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin implements LastLeashDataAccessor {
    @Unique
    @Nullable
    private LeashData lastLeashData;

    @Inject(
            method = "writeCustomDataToNbt",
            at = @At("TAIL")
    )
    private void writeNbtMixin(NbtCompound nbt, CallbackInfo ci) {
        writeLastLeashData(nbt, getLastLeashData());
    }

    @Inject(
            method = "readCustomDataFromNbt",
            at = @At("TAIL")
    )
    private void readNbtMixin(NbtCompound nbt, CallbackInfo ci) {
        setLastLeashData(readLastLeashData(nbt));
    }

    @Override
    @Nullable
    public LeashData getLastLeashData() {
        return lastLeashData;
    }

    @Override
    public void setLastLeashData(@Nullable LeashData lastLeashData) {
        this.lastLeashData = lastLeashData;
    }
}

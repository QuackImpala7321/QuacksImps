package com.quackimpala.quimps.mixin;

import com.quackimpala.quimps.AnimalEntityAccessor;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnimalEntity.class)
public abstract class AnimalEntityMixin extends PassiveEntity implements AnimalEntityAccessor {
    @Unique
    private int eatingCooldown;

    protected AnimalEntityMixin(EntityType<? extends PassiveEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(
            method = "mobTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/passive/PassiveEntity;mobTick()V",
                    shift = At.Shift.BEFORE
            )
    )
    private void mobTickMixin(CallbackInfo ci) {
        if (getEatingCooldown() > 0)
            setEatingCooldown(getEatingCooldown() - 1);
    }

    @Inject(
            method = "writeCustomDataToNbt",
            at = @At("TAIL")
    )
    private void writeEatingCooldown(NbtCompound nbt, CallbackInfo ci) {
        nbt.putInt(EATING_COOLDOWN, getEatingCooldown());
    }

    @Inject(
            method = "readCustomDataFromNbt",
            at = @At("TAIL")
    )
    private void readEatingCooldown(NbtCompound nbt, CallbackInfo ci) {
        setEatingCooldown(nbt.getInt(EATING_COOLDOWN));
    }

    @Override
    public void setEatingCooldown(int ticks) {
        eatingCooldown = ticks;
    }

    @Override
    public int getEatingCooldown() {
        return eatingCooldown;
    }
}

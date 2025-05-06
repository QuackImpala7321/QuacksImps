package com.quackimpala.quimps.mixin;

import com.quackimpala.quimps.acc.AnimalEntityAccessor;
import com.quackimpala.quimps.entity.ai.brain.QIMemoryModules;
import com.quackimpala.quimps.util.FeederPathData;
import com.quackimpala.quimps.util.SharedFeederLogic;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.sensor.TemptationsSensor;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.GlobalPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(TemptationsSensor.class)
public abstract class TemptSensorMixin {
    @Shadow protected abstract boolean test(ItemStack stack);

    @Inject(
            method = "sense(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/mob/PathAwareEntity;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/ai/brain/Brain;remember(Lnet/minecraft/entity/ai/brain/MemoryModuleType;Ljava/lang/Object;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void senseIfMixin(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, CallbackInfo ci) {
        final Brain<?> brain = pathAwareEntity.getBrain();
        brain.forget(QIMemoryModules.TEMPTING_FEEDER);
    }

    @Inject(
            method = "sense(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/mob/PathAwareEntity;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/ai/brain/Brain;forget(Lnet/minecraft/entity/ai/brain/MemoryModuleType;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void senseElseMixin(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, CallbackInfo ci) {
        if (pathAwareEntity instanceof AnimalEntity entity) {
            if (entity.isBaby() && !((AnimalEntityAccessor) entity).readyToEat()) return;
            if (entity.getBreedingAge() > 0 || !entity.canEat()) return;
        }

        final Optional<FeederPathData> op = SharedFeederLogic.sampleClosest(pathAwareEntity, this::test);
        final Brain<?> brain = pathAwareEntity.getBrain();
        if (op.isPresent()) {
            brain.remember(QIMemoryModules.TEMPTING_FEEDER, new GlobalPos(serverWorld.getRegistryKey(), op.get().feederPos()));
        } else {
            brain.forget(QIMemoryModules.TEMPTING_FEEDER);
        }

    }
}

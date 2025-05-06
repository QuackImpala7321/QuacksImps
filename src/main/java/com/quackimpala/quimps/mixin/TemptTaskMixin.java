package com.quackimpala.quimps.mixin;

import com.google.common.collect.ImmutableMap;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.quackimpala.quimps.block.entity.FeederBlockEntity;
import com.quackimpala.quimps.entity.ai.brain.QIMemoryModules;
import com.quackimpala.quimps.registry.QIBlockEntities;
import com.quackimpala.quimps.util.SharedFeederLogic;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.*;
import net.minecraft.entity.ai.brain.task.TemptTask;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Mixin(TemptTask.class)
public abstract class TemptTaskMixin {
    @Shadow @Final public Function<LivingEntity, Double> stopDistanceGetter;

    @Shadow protected abstract float getSpeed(PathAwareEntity entity);

    @Shadow protected abstract Optional<PlayerEntity> getTemptingPlayer(PathAwareEntity entity);

    @ModifyArg(
            method = "<init>(Ljava/util/function/Function;Ljava/util/function/Function;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/ai/brain/task/MultiTickTask;<init>(Ljava/util/Map;)V"
            ),
            index = 0
    )
    private static Map<MemoryModuleType<?>, MemoryModuleState> initMixin(Map<MemoryModuleType<?>, MemoryModuleState> requiredMemoryState) {
        final Map<MemoryModuleType<?>, MemoryModuleState> requiredStates = new HashMap<>(requiredMemoryState);
        requiredStates.replace(MemoryModuleType.TEMPTING_PLAYER, MemoryModuleState.REGISTERED);
        requiredStates.put(QIMemoryModules.TEMPTING_FEEDER, MemoryModuleState.REGISTERED);

        return ImmutableMap.copyOf(requiredStates);
    }

    @Unique
    private Optional<FeederBlockEntity> getTemptingFeeder(PathAwareEntity entity) {
        final Optional<GlobalPos> op = entity.getBrain().getOptionalRegisteredMemory(QIMemoryModules.TEMPTING_FEEDER);
        if (op.isEmpty() || entity.getServer() == null) return Optional.empty();

        final GlobalPos pos = op.get();
        final World world = entity.getServer().getWorld(pos.dimension());
        if (world == null || world != entity.getWorld()) return Optional.empty();

        final Optional<FeederBlockEntity> result = world.getBlockEntity(pos.pos(), QIBlockEntities.FEEDER);
        if (result.isPresent() && result.get().isEnabled()) {
            return result;
        }
        return Optional.empty();
    }

    @ModifyReturnValue(
            method = "shouldKeepRunning(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/mob/PathAwareEntity;J)Z",
            at = @At("RETURN")
    )
    private boolean shouldKeepRunningMixin(boolean original, ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
        return (getTemptingPlayer(pathAwareEntity).isPresent() || feederKeepRunning(pathAwareEntity)) && !pathAwareEntity.getBrain().hasMemoryModule(MemoryModuleType.BREED_TARGET) && !pathAwareEntity.getBrain().hasMemoryModule(MemoryModuleType.IS_PANICKING);
    }

    @Unique
    private boolean feederKeepRunning(PathAwareEntity pathAwareEntity) {
        final Optional<FeederBlockEntity> op = getTemptingFeeder(pathAwareEntity);
        return op.isPresent() && pathAwareEntity.squaredDistanceTo(op.get().getPos().toCenterPos()) > MathHelper.square(stopDistanceGetter.apply(pathAwareEntity));
    }

    @Inject(
            method = "keepRunning(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/mob/PathAwareEntity;J)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void keepRunningMixin(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l, CallbackInfo ci) {
        final Brain<?> brain = pathAwareEntity.getBrain();
        final Optional<FeederBlockEntity> op = getTemptingFeeder(pathAwareEntity);
        if (op.isEmpty()) return;

        final FeederBlockEntity feederBlockEntity = op.get();
        brain.remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(feederBlockEntity.getPos()));
        final double stopDistance = stopDistanceGetter.apply(pathAwareEntity);

        if (pathAwareEntity.squaredDistanceTo(feederBlockEntity.getPos().toCenterPos()) < MathHelper.square(stopDistance)) {
            brain.forget(MemoryModuleType.WALK_TARGET);
        } else {
            brain.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosLookTarget(feederBlockEntity.getPos()), getSpeed(pathAwareEntity), 2));
        }
        ci.cancel();
    }

    @Inject(
            method = "finishRunning(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/mob/PathAwareEntity;J)V",
            at = @At("TAIL")
    )
    private void finishRunningMixin(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l, CallbackInfo ci, @Local Brain<?> brain) {
        if (!brain.hasMemoryModule(QIMemoryModules.TEMPTING_FEEDER)) return;

        final Optional<FeederBlockEntity> op = getTemptingFeeder(pathAwareEntity);
        brain.forget(QIMemoryModules.TEMPTING_FEEDER);
        if (op.isEmpty()) return;
        final FeederBlockEntity feederBlockEntity = op.get();

        if (pathAwareEntity instanceof AnimalEntity entity
                && entity.isBreedingItem(feederBlockEntity.getStack())
                && SharedFeederLogic.tryEat(entity)
        ) {
            feederBlockEntity.serverEat(entity);
        }
    }
}

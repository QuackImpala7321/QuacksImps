package com.quackimpala.quimps.mixin;

import com.quackimpala.quimps.QuacksImps;
import com.quackimpala.quimps.acc.AnimalEntityAccessor;
import com.quackimpala.quimps.block.FeederBlock;
import com.quackimpala.quimps.block.entity.FeederBlockEntity;
import com.quackimpala.quimps.entity.ai.brain.QIMemoryModules;
import com.quackimpala.quimps.registry.QIBlockEntities;
import com.quackimpala.quimps.util.FeederPathData;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.sensor.TemptationsSensor;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.GlobalPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
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

        final Optional<FeederPathData> op = sampleClosest(pathAwareEntity);
        final Brain<?> brain = pathAwareEntity.getBrain();
        if (op.isPresent()) {
            brain.remember(QIMemoryModules.TEMPTING_FEEDER, new GlobalPos(serverWorld.getRegistryKey(), op.get().feederPos()));
        } else {
            brain.forget(QIMemoryModules.TEMPTING_FEEDER);
        }

    }

    @Unique
    private Optional<Path> tryGetPathForFeeder(PathAwareEntity entity, FeederBlockEntity feeder) {
        return test(feeder.getStack()) ?
                Optional.ofNullable(pathTo(entity, feeder))
                : Optional.empty();
    }

    @Unique
    private Path pathTo(PathAwareEntity entity, FeederBlockEntity feeder) {
        return entity.getNavigation().findPathTo(
                feeder.getPos().offset(feeder.getCachedState().get(FeederBlock.FACING)), 0);
    }

    @Unique
    private Optional<FeederPathData> sampleClosest(PathAwareEntity entity) {
        final Box box = new Box(entity.getBlockPos()).expand(10, 2, 10);
        BlockPos closestPos = null;
        Path closestPath = null;
        for (int x = (int) box.minX; x <= box.maxX; x++)
            for (int y = (int) box.minY; y <= box.maxY; y++)
                for (int z = (int) box.minZ; z <= box.maxZ; z++) {
                    final BlockPos pos = new BlockPos(x, y, z);
                    final Optional<FeederBlockEntity> optionalF = entity.getWorld().getBlockEntity(pos, QIBlockEntities.FEEDER);
                    if (optionalF.isEmpty()) continue;
                    final FeederBlockEntity feeder = optionalF.get();

                    final Optional<Path> optionalP = tryGetPathForFeeder(entity, feeder);
                    if (optionalP.isEmpty()) continue;
                    final Path path = optionalP.get();
                    if (closestPath == null || path.getLength() < closestPath.getLength()) {
                        closestPos = pos;
                        closestPath = path;
                    }
                }
        if (closestPath == null)
            return Optional.empty();
        return Optional.of(new FeederPathData(closestPos, closestPath));
    }
}

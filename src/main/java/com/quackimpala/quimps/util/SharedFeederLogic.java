package com.quackimpala.quimps.util;

import com.quackimpala.quimps.acc.AnimalEntityAccessor;
import com.quackimpala.quimps.block.FeederBlock;
import com.quackimpala.quimps.block.entity.FeederBlockEntity;
import com.quackimpala.quimps.registry.QIBlockEntities;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.Optional;
import java.util.function.Predicate;

public class SharedFeederLogic {
    public static Optional<FeederPathData> sampleClosest(PathAwareEntity entity, Predicate<ItemStack> predicate) {
        if (entity instanceof AnimalEntity animalEntity && !animalEntity.canEat()) return Optional.empty();

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

                    final Optional<Path> optionalP = tryGetPathForFeeder(entity, feeder, predicate);
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

    public static Optional<Path> tryGetPathForFeeder(PathAwareEntity entity, FeederBlockEntity feeder, Predicate<ItemStack> predicate) {
        return isTemptedBy(feeder, predicate) ?
                Optional.ofNullable(pathTo(entity, feeder))
                : Optional.empty();
    }

    public static boolean tryEat(AnimalEntity entity) {
        final AnimalEntityAccessor acc = (AnimalEntityAccessor) entity;
        int breedingAge = entity.getBreedingAge();
        if (entity.isBaby() && acc.readyToEat()) {
            entity.growUp(PassiveEntity.toGrowUpAge(-breedingAge), true);
            acc.setEatingCooldown(600);
            return true;
        } else if (breedingAge == 0 && entity.canEat()) {
            entity.lovePlayer(null);
            return true;
        }
        return false;
    }

    private static boolean isTemptedBy(FeederBlockEntity feeder, Predicate<ItemStack> predicate) {
        return feeder.isEnabled() && predicate.test(feeder.getStack());
    }

    private static Path pathTo(PathAwareEntity entity, FeederBlockEntity feeder) {
        return entity.getNavigation().findPathTo(
                feeder.getPos().offset(feeder.getCachedState().get(FeederBlock.FACING)), 0);
    }
}

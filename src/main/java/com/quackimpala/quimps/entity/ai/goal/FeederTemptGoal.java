package com.quackimpala.quimps.entity.ai.goal;

import com.quackimpala.quimps.acc.AnimalEntityAccessor;
import com.quackimpala.quimps.block.FeederBlock;
import com.quackimpala.quimps.block.entity.FeederBlockEntity;
import com.quackimpala.quimps.registry.QIBlockEntities;
import com.quackimpala.quimps.util.FeederPathData;
import net.minecraft.entity.ai.brain.sensor.TemptationsSensor;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.*;
import java.util.function.Predicate;

public class FeederTemptGoal extends Goal {
    private static final double REACH = 1.5d;

    protected final AnimalEntity entity;
    private final double speed;
    private final Predicate<ItemStack> predicate;
    private FeederPathData pathData;

    private boolean success;

    public FeederTemptGoal(AnimalEntity entity, double speed, Predicate<ItemStack> predicate) {
        this.entity = entity;
        this.speed = speed;
        this.predicate = predicate;

        setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    private void setSuccess(boolean success) {
        this.success = success;
    }

    private boolean isSuccess() {
        return success;
    }

    private boolean isTemptedBy(FeederBlockEntity feeder) {
        return predicate.test(feeder.getStack()) && entity.canEat();
    }

    private Optional<Path> tryGetPathForFeeder(FeederBlockEntity feeder) {
        return isTemptedBy(feeder) ?
            Optional.ofNullable(pathTo(feeder))
        : Optional.empty();
    }

    private boolean tryEat() {
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

    private Optional<FeederPathData> sampleClosest() {
        final Box box = new Box(entity.getBlockPos()).expand(TemptationsSensor.MAX_DISTANCE, TemptationsSensor.MAX_DISTANCE, TemptationsSensor.MAX_DISTANCE);
        BlockPos closestPos = null;
        Path closestPath = null;
        for (int x = (int) box.minX; x <= box.maxX; x++)
            for (int y = (int) box.minY; y <= box.maxY; y++)
                for (int z = (int) box.minZ; z <= box.maxZ; z++) {
                    final BlockPos pos = new BlockPos(x, y, z);
                    final Optional<FeederBlockEntity> optionalF = entity.getWorld().getBlockEntity(pos, QIBlockEntities.FEEDER);
                    if (optionalF.isEmpty()) continue;
                    final FeederBlockEntity feeder = optionalF.get();

                    final Optional<Path> optionalP = tryGetPathForFeeder(feeder);
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

    private Path pathTo(FeederBlockEntity feeder) {
        return entity.getNavigation().findPathTo(
                feeder.getPos().offset(feeder.getCachedState().get(FeederBlock.FACING)), 0);
    }

    @Override
    public boolean canStart() {
        if (entity.isBaby() && !((AnimalEntityAccessor) entity).readyToEat()) return false;
        if (entity.getBreedingAge() > 0 || !entity.canEat()) return false;

        final Optional<FeederPathData> optional = sampleClosest();
        if (optional.isEmpty()) return false;
        pathData = optional.get();
        entity.getNavigation().startMovingAlong(pathData.path(), speed);
        return true;
    }

    @Override
    public boolean shouldContinue() {
        setSuccess(false);
        final Optional<FeederBlockEntity> optionalF = entity.getWorld().getBlockEntity(pathData.feederPos(), QIBlockEntities.FEEDER);
        if (optionalF.isEmpty() || !isTemptedBy(optionalF.get()))
            return false;
        final Optional<Path> optionalP = tryGetPathForFeeder(optionalF.get());
        if (optionalP.isEmpty())
            return false;
        pathData.reroute(optionalP.get());
        entity.getNavigation().startMovingAlong(pathData.path(), speed);
        if (!entity.isNavigating())
            return false;
        final PathNode end = entity.getNavigation().getCurrentPath().getEnd();
        if (end == null)
            return false;
        setSuccess(end.getPos().distanceTo(entity.getPos()) <= REACH);
        return !isSuccess();
    }

    @Override
    public void stop() {
        entity.getNavigation().stop();
        if (!isSuccess()) return;
        final Optional<FeederBlockEntity> optional = entity.getWorld().getBlockEntity(pathData.feederPos(), QIBlockEntities.FEEDER);
        if (optional.isEmpty()) return;
        final FeederBlockEntity feeder = optional.get();
        if (!entity.isBreedingItem(feeder.getStack()) || !tryEat()) return;

        entity.getWorld().addSyncedBlockEvent(feeder.getPos(), feeder.getCachedState().getBlock(),
                FeederBlockEntity.EAT_EVENT, 0);
    }
}

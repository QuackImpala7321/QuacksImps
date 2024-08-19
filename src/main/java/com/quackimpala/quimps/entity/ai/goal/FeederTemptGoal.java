package com.quackimpala.quimps.entity.ai.goal;

import com.quackimpala.quimps.AnimalEntityAccessor;
import com.quackimpala.quimps.QuacksImps;
import com.quackimpala.quimps.block.entity.FeederBlockEntity;
import com.quackimpala.quimps.registry.ModBlockEntities;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.*;
import java.util.function.Predicate;

public class FeederTemptGoal extends Goal {
    private static final double MIN_DISTANCE = 3.0d;

    protected final AnimalEntity entity;
    private final double speed;
    private final Predicate<ItemStack> predicate;
    private Vec3d targetPos;

    private boolean active;

    public FeederTemptGoal(AnimalEntity entity, double speed, Predicate<ItemStack> predicate) {
        this.entity = entity;
        this.speed = speed;
        this.predicate = predicate;

        setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    protected void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    private boolean isTemptedBy(FeederBlockEntity feeder) {
        return predicate.test(feeder.getStack());
    }

    @Override
    public void tick() {
        if (targetPos.distanceTo(entity.getPos()) < MIN_DISTANCE)
            entity.getNavigation().stop();
        else
            entity.getNavigation().startMovingTo(targetPos.getX(), targetPos.getY(), targetPos.getZ(), speed);
    }

    private Optional<FeederBlockEntity> getFeederAt(Vec3d pos) {
        return getFeederAt(BlockPos.ofFloored(pos));
    }

    private Optional<FeederBlockEntity> getFeederAt(BlockPos pos) {
        return entity.getWorld().getBlockEntity(pos, ModBlockEntities.FEEDER);
    }

    private Optional<FeederBlockEntity> getClosestFeeder() {
        final Box box = new Box(entity.getBlockPos()).expand(8.0, 2.0, 8.0);
        Optional<FeederBlockEntity> closest = Optional.empty();
        double closestDistance = Double.POSITIVE_INFINITY;
        for (int x = (int) box.minX; x <= box.maxX; x++)
            for (int y = (int) box.minY; y <= box.maxY; y++)
                for (int z = (int) box.minZ; z <= box.maxZ; z++) {
                    final Optional<FeederBlockEntity> optional = getFeederAt(new BlockPos(x, y, z));
                    if (optional.isEmpty() || !isTemptedBy(optional.get()))
                        continue;

                    final double distance = Vec3d.of(optional.get().getPos()).distanceTo(entity.getPos());
                    if (distance >= closestDistance)
                        continue;

                    closest = optional;
                    closestDistance = distance;
                }

        return closest;
    }

    @Override
    public boolean canStart() {
        if (entity.getBreedingAge() > 0 || !entity.canEat())
            return false;

        final Optional<FeederBlockEntity> closest = getClosestFeeder();
        if (closest.isEmpty() || !isTemptedBy(closest.get()))
            return false;

        targetPos = closest.get().getPos().toCenterPos();
        return true;
    }

    @Override
    public boolean shouldContinue() {
        final Optional<FeederBlockEntity> optional = getFeederAt(targetPos);
        setActive(optional.isPresent() && isTemptedBy(optional.get()));

        return isActive() && targetPos.distanceTo(entity.getPos()) >= MIN_DISTANCE;
    }

    @Override
    public void start() {
        setActive(true);
    }

    private boolean tryEat() {
        final AnimalEntityAccessor acc = (AnimalEntityAccessor) entity;
        int breedingAge = entity.getBreedingAge();
        if (breedingAge == 0 && entity.canEat()) {
            QuacksImps.LOGGER.info("freaky");
            entity.lovePlayer(null);
            return true;
        } else if (entity.isBaby() && acc.readyToEat()) {
            QuacksImps.LOGGER.info("grow up kid");
            entity.growUp(PassiveEntity.toGrowUpAge(-breedingAge), true);
            acc.setEatingCooldown(600);
            return true;
        }
        return false;
    }

    @Override
    public void stop() {
        final Optional<FeederBlockEntity> optional = getFeederAt(targetPos);
        if (optional.isEmpty())
            return;

        final FeederBlockEntity feeder = optional.get();
        if (!isActive() || !entity.isBreedingItem(feeder.getStack()) || !tryEat())
            return;

        entity.getWorld().addSyncedBlockEvent(feeder.getPos(), feeder.getCachedState().getBlock(),
                FeederBlockEntity.EAT_EVENT, 0);
    }
}

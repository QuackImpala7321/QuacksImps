package com.quackimpala.quimps.entity.ai.goal;

import com.quackimpala.quimps.acc.AnimalEntityAccessor;
import com.quackimpala.quimps.block.entity.FeederBlockEntity;
import com.quackimpala.quimps.registry.QIBlockEntities;
import com.quackimpala.quimps.util.FeederPathData;
import com.quackimpala.quimps.util.SharedFeederLogic;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.ItemStack;

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

    @Override
    public boolean canStart() {
        if (entity.isBaby() && !((AnimalEntityAccessor) entity).readyToEat()) return false;
        if (entity.getBreedingAge() > 0 || !entity.canEat()) return false;

        final Optional<FeederPathData> optional = SharedFeederLogic.sampleClosest(entity, predicate);
        if (optional.isEmpty()) return false;
        pathData = optional.get();
        entity.getNavigation().startMovingAlong(pathData.path(), speed);
        return true;
    }

    @Override
    public boolean shouldContinue() {
        setSuccess(false);
        final Optional<FeederBlockEntity> optionalF = entity.getWorld().getBlockEntity(pathData.feederPos(), QIBlockEntities.FEEDER);
        if (optionalF.isEmpty())
            return false;
        final Optional<Path> optionalP = SharedFeederLogic.tryGetPathForFeeder(entity, optionalF.get(), predicate);
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
        if (entity.isBreedingItem(feeder.getStack()) && SharedFeederLogic.tryEat(entity)) {
            feeder.serverEat(entity);
        }
    }
}

package com.quackimpala.quimps.mixin;

import com.quackimpala.quimps.entity.ai.goal.FeederTemptGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.passive.AnimalEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GoalSelector.class)
public abstract class GoalSelectorMixin {
    @Shadow public abstract void add(int priority, Goal goal);

    @Inject(
            method = "add",
            at = @At("TAIL")
    )
    private void addMixin(int priority, Goal goal, CallbackInfo ci) {
        if (goal instanceof TemptGoal temptGoal && temptGoal.mob instanceof AnimalEntity animalEntity)
            add(priority, new FeederTemptGoal(animalEntity, temptGoal.speed, temptGoal.foodPredicate));
    }
}

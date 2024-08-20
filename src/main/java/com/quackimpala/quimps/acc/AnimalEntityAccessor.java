package com.quackimpala.quimps.acc;

public interface AnimalEntityAccessor {
    String EATING_COOLDOWN = "EatingCooldown";

    int getEatingCooldown();
    void setEatingCooldown(int ticks);

    default boolean readyToEat() {
        return getEatingCooldown() == 0;
    }
}

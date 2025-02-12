package com.quackimpala.quimps.mixin;

import com.google.common.collect.ImmutableList;
import com.quackimpala.quimps.entity.ai.brain.QIMemoryModules;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Collection;

@Mixin(Brain.class)
public abstract class BrainMixin {
    @ModifyArg(
            method = "createProfile",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/brain/Brain$Profile;<init>(Ljava/util/Collection;Ljava/util/Collection;)V"),
            index = 0
    )
    private static Collection<? extends MemoryModuleType<?>> profileMixin(Collection<? extends MemoryModuleType<?>> memModules) {
        if (!memModules.contains(MemoryModuleType.TEMPTING_PLAYER)) return memModules;

        final ImmutableList.Builder<MemoryModuleType<?>> builder = ImmutableList.builder();
        builder.addAll(memModules);
        builder.add(QIMemoryModules.TEMPTING_FEEDER);
        return builder.build();
    }
}

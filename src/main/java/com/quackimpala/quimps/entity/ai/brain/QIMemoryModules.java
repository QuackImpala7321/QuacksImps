package com.quackimpala.quimps.entity.ai.brain;

import com.mojang.serialization.Codec;
import com.quackimpala.quimps.QuacksImps;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.GlobalPos;

import java.util.Optional;

public interface QIMemoryModules {
    MemoryModuleType<GlobalPos> TEMPTING_FEEDER = register("tempting_feeder", GlobalPos.CODEC);

    static void registerMemoryModules() {}

    private static <T> MemoryModuleType<T> register(String id) {
        return Registry.register(Registries.MEMORY_MODULE_TYPE,
                Identifier.of(QuacksImps.MOD_ID, id),
                new MemoryModuleType<>(Optional.empty()));
    }

    private static <T> MemoryModuleType<T> register(String id, Codec<T> codec) {
        return Registry.register(Registries.MEMORY_MODULE_TYPE,
                Identifier.of(QuacksImps.MOD_ID, id),
                new MemoryModuleType<>(Optional.of(codec)));
    }
}

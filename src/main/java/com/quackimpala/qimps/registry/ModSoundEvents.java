package com.quackimpala.qimps.registry;

import com.quackimpala.qimps.QuacksImps;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSoundEvents {
    public static SoundEvent LEASH_BREAK = register("entity.leash.break");

    private static SoundEvent register(String path) {
        final Identifier id = Identifier.of(QuacksImps.MOD_ID, path);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void registerSoundEvents() { }
}

package com.quackimpala.quimps.registry;

import com.quackimpala.quimps.QuacksImps;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;

public class QIStats {
    public static Identifier INSPECT_PLACER = register("inspect_placer");

    private static Identifier register(String id) {
        return register(id, StatFormatter.DEFAULT);
    }

    private static Identifier register(String id, StatFormatter formatter) {
        Identifier identifier = Identifier.of(QuacksImps.MOD_ID, id);
        Registry.register(Registries.CUSTOM_STAT, identifier, identifier);
        Stats.CUSTOM.getOrCreateStat(identifier, formatter);
        return identifier;
    }

    public static void registerStats() { }
}

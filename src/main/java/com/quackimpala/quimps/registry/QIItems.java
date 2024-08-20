package com.quackimpala.quimps.registry;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.RegistryKey;

import java.util.Arrays;

public class QIItems {
    public static void populateItemGroups() {
        addToCreative(ItemGroups.REDSTONE, Spot.AFTER, QIBlocks.PLACER, Blocks.STICKY_PISTON);
        addToCreative(ItemGroups.FUNCTIONAL, Spot.BEFORE, Blocks.COMPOSTER, QIBlocks.FEEDER);
    }

    private static void addToCreative(RegistryKey<ItemGroup> group, Spot spot, ItemConvertible target, ItemConvertible... items) {
        addToCreative(group, spot, items[0], target);
        if (items.length > 1)
            addToCreative(group, Spot.AFTER, items[0], Arrays.copyOfRange(items, 1, items.length));
    }

    private static void addToCreative(RegistryKey<ItemGroup> group, Spot spot, ItemConvertible target, ItemConvertible item) {
        ItemGroupEvents.modifyEntriesEvent(group).register(content -> {
            switch (spot) {
                case BEFORE -> content.addBefore(target, item);
                case AFTER -> content.addAfter(target, item);
            }
        });
    }

    private enum Spot {
        BEFORE,
        AFTER
    }
}

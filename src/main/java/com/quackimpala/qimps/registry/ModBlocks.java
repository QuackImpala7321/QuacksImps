package com.quackimpala.qimps.registry;

import com.quackimpala.qimps.QuacksImps;
import com.quackimpala.qimps.block.PlacerBlock;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final Block PLACER = registerBlock("placer", new PlacerBlock(
            AbstractBlock.Settings.copy(Blocks.DISPENSER)
    ));

    public static void registerBlocks() {
        addToCreative(ItemGroups.REDSTONE, ModBlocks.PLACER, Spot.AFTER, Blocks.STICKY_PISTON);
    }

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return registerBlockWithoutItem(name, block);
    }

    private static Block registerBlockWithoutItem(String name, Block block) {
        return Registry.register(Registries.BLOCK, Identifier.of(QuacksImps.MOD_ID, name), block);
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM,
                Identifier.of(QuacksImps.MOD_ID, name),
                new BlockItem(block, new Item.Settings()));
    }

    private static void addToCreative(RegistryKey<ItemGroup> group, ItemConvertible item, Spot spot, ItemConvertible target) {
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

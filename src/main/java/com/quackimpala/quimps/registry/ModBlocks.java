package com.quackimpala.quimps.registry;

import com.quackimpala.quimps.QuacksImps;
import com.quackimpala.quimps.block.FeederBlock;
import com.quackimpala.quimps.block.PlacerBlock;
import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final PlacerBlock PLACER = registerBlock("placer", new PlacerBlock(Settings.copy(Blocks.DISPENSER)));
    public static final FeederBlock OAK_FEEDER = registerBlock("oak_feeder", new FeederBlock(Settings.copy(Blocks.OAK_PLANKS)));
    public static final FeederBlock SPRUCE_FEEDER = registerBlock("spruce_feeder", new FeederBlock(Settings.copy(Blocks.SPRUCE_PLANKS)));
    public static final FeederBlock BIRCH_FEEDER = registerBlock("birch_feeder", new FeederBlock(Settings.copy(Blocks.BIRCH_PLANKS)));

    public static void registerBlocks() {}

    private static <T extends Block> T registerBlock(String name, T block) {
        registerBlockItem(name, block);
        return registerBlockWithoutItem(name, block);
    }

    private static <T extends Block> T registerBlockWithoutItem(String name, T block) {
        return Registry.register(Registries.BLOCK, Identifier.of(QuacksImps.MOD_ID, name), block);
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM,
                Identifier.of(QuacksImps.MOD_ID, name),
                new BlockItem(block, new Item.Settings()));
    }
}

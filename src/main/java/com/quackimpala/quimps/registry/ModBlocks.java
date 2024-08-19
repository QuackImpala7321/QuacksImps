package com.quackimpala.quimps.registry;

import com.quackimpala.quimps.QuacksImps;
import com.quackimpala.quimps.block.FeederBlock;
import com.quackimpala.quimps.block.PlacerBlock;
import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final PlacerBlock PLACER = registerBlock("placer", new PlacerBlock(Settings.copy(Blocks.DISPENSER)));
    public static final FeederBlock FEEDER = registerBlock("feeder", new FeederBlock(Settings.create()
            .mapColor(MapColor.OAK_TAN).instrument(NoteBlockInstrument.BASEDRUM)
            .strength(2.0f, 6.0f)));

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

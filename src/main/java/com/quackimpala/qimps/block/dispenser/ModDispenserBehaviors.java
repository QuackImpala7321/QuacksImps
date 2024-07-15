package com.quackimpala.qimps.block.dispenser;

import net.minecraft.block.DispenserBlock;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;

import java.util.ArrayList;
import java.util.List;

public class ModDispenserBehaviors {
    private static final FlowerItemDispenserBehavior FLOWER_BEHAVIOR = new FlowerItemDispenserBehavior();
    private static final List<ItemConvertible> FLOWERS = new ArrayList<>();

    public static void registerBehaviors() {
        DispenserBlock.registerBehavior(Items.BOWL, new BowlItemDispenserBehavior());
        registerFlowerBehaviors();
    }

    public static void queueFlowerBehavior(ItemConvertible item) {
        FLOWERS.add(item);
    }

    private static void registerFlowerBehaviors() {
        for (final ItemConvertible item : FLOWERS)
            DispenserBlock.registerBehavior(item, FLOWER_BEHAVIOR);
        FLOWERS.clear();
    }
}

package com.quackimpala.qimps.registry;

import com.quackimpala.qimps.block.dispenser.BowlItemDispenserBehavior;
import com.quackimpala.qimps.block.dispenser.FlowerItemDispenserBehavior;
import net.minecraft.block.DispenserBlock;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;

import java.util.ArrayList;
import java.util.List;

public class ModDispenserBehaviors {
    private static boolean loadedFlowers = false;
    private static final FlowerItemDispenserBehavior FLOWER_BEHAVIOR = new FlowerItemDispenserBehavior();
    private static final List<ItemConvertible> FLOWERS = new ArrayList<>();

    public static void registerBehaviors() {
        DispenserBlock.registerBehavior(Items.BOWL, new BowlItemDispenserBehavior());
        registerFlowerBehaviors();
    }

    public static void queueFlowerBehavior(ItemConvertible item) {
        if (loadedFlowers)
            DispenserBlock.registerBehavior(item, FLOWER_BEHAVIOR);
        else
            FLOWERS.add(item);
    }

    private static void registerFlowerBehaviors() {
        loadedFlowers = true;

        for (final ItemConvertible item : FLOWERS)
            DispenserBlock.registerBehavior(item, FLOWER_BEHAVIOR);
        FLOWERS.clear();
    }
}

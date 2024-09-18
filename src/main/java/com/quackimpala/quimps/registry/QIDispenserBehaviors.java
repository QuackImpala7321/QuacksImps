package com.quackimpala.quimps.registry;

import com.quackimpala.quimps.block.dispenser.BowlItemDispenserBehavior;
import com.quackimpala.quimps.block.dispenser.FlowerItemDispenserBehavior;
import com.quackimpala.quimps.util.QueueableDispenserBehavior;
import net.minecraft.block.DispenserBlock;
import net.minecraft.item.Items;

public class QIDispenserBehaviors {
    public static final FlowerItemDispenserBehavior FLOWER_BEHAVIOR = new FlowerItemDispenserBehavior();
    public static final QueueableDispenserBehavior FLOWERS = new QueueableDispenserBehavior(FLOWER_BEHAVIOR);

    public static void registerBehaviors() {
//        DispenserBlock.registerBehavior(Items.BOWL, new BowlItemDispenserBehavior());
//        FLOWERS.load();
    }
}

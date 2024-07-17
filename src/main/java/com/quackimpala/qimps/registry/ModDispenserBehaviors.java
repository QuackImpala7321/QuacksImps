package com.quackimpala.qimps.registry;

import com.quackimpala.qimps.block.dispenser.BowlItemDispenserBehavior;
import com.quackimpala.qimps.block.dispenser.FlowerItemDispenserBehavior;
import com.quackimpala.qimps.util.QueueableDispenserBehavior;
import net.minecraft.block.DispenserBlock;
import net.minecraft.item.Items;

public class ModDispenserBehaviors {
    public static final QueueableDispenserBehavior FLOWERS = new QueueableDispenserBehavior(new FlowerItemDispenserBehavior());

    public static void registerBehaviors() {
        DispenserBlock.registerBehavior(Items.BOWL, new BowlItemDispenserBehavior());
        FLOWERS.load();
    }
}

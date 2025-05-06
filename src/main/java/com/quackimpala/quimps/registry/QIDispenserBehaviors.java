package com.quackimpala.quimps.registry;

import com.quackimpala.quimps.block.dispenser.BowlItemDispenserBehavior;
import com.quackimpala.quimps.block.dispenser.FlowerItemDispenserBehavior;
import com.quackimpala.quimps.util.QueueableDispenserBehavior;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.FlowerBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;

import java.util.List;

public interface QIDispenserBehaviors {
    FlowerItemDispenserBehavior FLOWER_BEHAVIOR = new FlowerItemDispenserBehavior();
    QueueableDispenserBehavior FLOWERS = new QueueableDispenserBehavior(FLOWER_BEHAVIOR) {
        @SuppressWarnings("unchecked")
        @Override
        protected List<ItemConvertible> prepare() {
            final List<Item> flowerItems = Registries.ITEM.stream().filter(item ->
                    item instanceof BlockItem blockItem
                    && blockItem.getBlock() instanceof FlowerBlock).toList();

            return (List<ItemConvertible>) (List<?>) flowerItems;
        }
    };

    static void registerBehaviors() {
        DispenserBlock.registerBehavior(Items.BOWL, new BowlItemDispenserBehavior());
    }
}

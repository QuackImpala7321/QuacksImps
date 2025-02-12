package com.quackimpala.quimps.util;

import com.quackimpala.quimps.QuacksImps;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.item.ItemConvertible;

import java.util.ArrayList;
import java.util.List;

public abstract class QueueableDispenserBehavior {
    private final DispenserBehavior behavior;

    public QueueableDispenserBehavior(DispenserBehavior behavior) {
        this.behavior = behavior;
    }

    protected abstract List<ItemConvertible> prepare();

    public void reload() {
        QuacksImps.LOGGER.info("reloading behaviors");
        for (final ItemConvertible item : prepare())
            accept(item);
    }

    protected void accept(ItemConvertible item) {
        DispenserBlock.registerBehavior(item, behavior);
    }
}

package com.quackimpala.quimps.util;

import com.quackimpala.quimps.QuacksImps;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.item.ItemConvertible;

import java.util.ArrayList;
import java.util.List;

public class QueueableDispenserBehavior {
    private boolean loaded;
    private final List<ItemConvertible> queued;
    private final DispenserBehavior behavior;

    public QueueableDispenserBehavior(DispenserBehavior behavior) {
        loaded = false;
        queued = new ArrayList<>();
        this.behavior = behavior;
    }

    public void queue(ItemConvertible item) {
        if (isLoaded())
            accept(item);
        else
            queued.add(item);
    }

    public void load() {
        if (isLoaded()) {
            QuacksImps.LOGGER.error("This behavior has already been loaded!");
            return;
        }

        loaded = true;

        for (final ItemConvertible item : queued)
            accept(item);
        queued.clear();
    }

    protected void accept(ItemConvertible item) {
        DispenserBlock.registerBehavior(item, behavior);
    }

    public boolean isLoaded() {
        return loaded;
    }
}

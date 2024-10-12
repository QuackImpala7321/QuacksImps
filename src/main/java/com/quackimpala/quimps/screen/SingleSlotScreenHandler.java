package com.quackimpala.quimps.screen;

import com.quackimpala.quimps.registry.QIScreenHandlers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class SingleSlotScreenHandler extends ScreenHandler {
    private final Inventory inventory;

    public SingleSlotScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(1));
    }

    public SingleSlotScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(QIScreenHandlers.FEEDER_SCREEN, syncId);
        checkSize(inventory, 1);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);
        addSlot(new Slot(inventory, 0, 80, 35));
        for (int i = 0; i < 3; ++i)
            for (int j = 0; j < 9; ++j)
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
        for (int i = 0; i < 9; ++i)
            addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slotId) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotId);
        if (slot.hasStack()) {
            ItemStack stack2 = slot.getStack();
            stack = stack2.copy();
            if (slotId < 9 ? !this.insertItem(stack2, inventory.size(), slots.size(), true) : !this.insertItem(stack2, 0, inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (stack2.isEmpty()) {
                slot.setStackNoCallbacks(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            if (stack2.getCount() == stack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTakeItem(player, stack2);
        }
        return stack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return inventory.canPlayerUse(player);
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        inventory.onClose(player);
    }
}

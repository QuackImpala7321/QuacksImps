package com.quackimpala.qimps.block.entity;

import com.quackimpala.qimps.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.Generic3x3ContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public class PlacerBlockEntity extends LootableContainerBlockEntity {
    private DefaultedList<ItemStack> inventory;

    public PlacerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.PLACER, blockPos, blockState);
        inventory = DefaultedList.ofSize(size(), ItemStack.EMPTY);
    }

    public int getFirstNonEmptySlot() {
        for (int i = 0; i < inventory.size(); i++)
            if (!inventory.get(i).isEmpty())
                return i;
        return -1;
    }

    @Override
    protected Text getContainerName() {
        return Text.translatable("block.qimps.placer");
    }

    @Override
    protected DefaultedList<ItemStack> getHeldStacks() {
        return inventory;
    }

    @Override
    protected void setHeldStacks(DefaultedList<ItemStack> inventory) {
        this.inventory = inventory;
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new Generic3x3ContainerScreenHandler(syncId, playerInventory, this);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        inventory = DefaultedList.ofSize(size(), ItemStack.EMPTY);
        if (!readLootTable(nbt))
            Inventories.readNbt(nbt, inventory, registryLookup);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        if (!writeLootTable(nbt))
            Inventories.writeNbt(nbt, inventory, registryLookup);
    }

    @Override
    public int size() {
        return 9;
    }
}

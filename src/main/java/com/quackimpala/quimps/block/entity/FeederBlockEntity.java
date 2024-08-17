package com.quackimpala.quimps.block.entity;

import com.quackimpala.quimps.registry.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.LootableInventory;
import net.minecraft.inventory.SingleStackInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class FeederBlockEntity extends BlockEntity implements LootableInventory, SingleStackInventory.SingleStackBlockEntityInventory {
    private ItemStack stack;
    private RegistryKey<LootTable> lootTableId;
    private long lootTableSeed;

    public FeederBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FEEDER, pos, state);
        stack = ItemStack.EMPTY;
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
    }

    public ItemStack getStack() {
        return stack;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        if (!stack.isEmpty())
            nbt.put("item", stack.encode(registryLookup));
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        if (nbt.contains("item"))
            stack = ItemStack.fromNbt(registryLookup, nbt.getCompound("item")).orElse(ItemStack.EMPTY);
    }

    @Nullable
    @Override
    public RegistryKey<LootTable> getLootTable() {
        return lootTableId;
    }

    @Override
    public void setLootTable(@Nullable RegistryKey<LootTable> lootTable) {
        lootTableId = lootTable;
    }

    @Override
    public long getLootTableSeed() {
        return lootTableSeed;
    }

    @Override
    public void setLootTableSeed(long lootTableSeed) {
        this.lootTableSeed = lootTableSeed;
    }

    @Override
    public BlockEntity asBlockEntity() {
        return this;
    }
}

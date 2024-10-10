package com.quackimpala.quimps.block.entity;

import com.quackimpala.quimps.block.FeederBlock;
import com.quackimpala.quimps.registry.QIBlockEntities;
import com.quackimpala.quimps.screen.SingleSlotScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SingleStackInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FeederBlockEntity extends LootableContainerBlockEntity implements SingleStackInventory.SingleStackBlockEntityInventory {
    public static final int EAT_EVENT = 1;
    protected static final String ITEM_NBT = "item";

    private DefaultedList<ItemStack> stacks;

    public FeederBlockEntity(BlockPos pos, BlockState state) {
        super(QIBlockEntities.FEEDER, pos, state);
        stacks = DefaultedList.ofSize(1, ItemStack.EMPTY);
    }

    public static void tick(World world, BlockPos pos, BlockState state, FeederBlockEntity blockEntity) {
        if (!state.get(FeederBlock.FILLED))
            FeederBlock.setFilled(world, pos);
    }

    @Override
    public boolean onSyncedBlockEvent(int type, int data) {
        if (getWorld() != null && type == EAT_EVENT) {
            decreaseStack(1);
            FeederBlock.setFilled(getWorld(), getPos());
            return true;
        }
        return super.onSyncedBlockEvent(type, data);
    }

    @Override
    public void removeFromCopiedStackNbt(NbtCompound nbt) {
        super.removeFromCopiedStackNbt(nbt);
        nbt.remove(ITEM_NBT);
    }

    public void setStack(ItemStack stack) {
        generateLoot(null);
        stacks.set(0, stack);
    }

    public ItemStack getStack() {
        generateLoot(null);
        return stacks.get(0);
    }

    public boolean isFilled() {
        return !getStack().isEmpty();
    }

    @Override
    public ItemStack decreaseStack(int count) {
        generateLoot(null);
        return SingleStackBlockEntityInventory.super.decreaseStack(count);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        if (!writeLootTable(nbt) && !getStack().isEmpty())
            nbt.put(ITEM_NBT, getStack().encode(registryLookup));
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        stacks = DefaultedList.ofSize(size(), ItemStack.EMPTY);
        if (!readLootTable(nbt))
            setStack(ItemStack.fromNbtOrEmpty(registryLookup, nbt.getCompound(ITEM_NBT)));
    }

    @Override
    protected Text getContainerName() {
        return Text.translatable("block.quimps.feeder");
    }

    @Override
    protected DefaultedList<ItemStack> getHeldStacks() {
        return stacks;
    }

    @Override
    protected void setHeldStacks(DefaultedList<ItemStack> inventory) {
        stacks = inventory;
        FeederBlock.setFilled(getWorld(), getPos());
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new SingleSlotScreenHandler(syncId, playerInventory, this);
    }

    @Override
    public BlockEntity asBlockEntity() {
        return this;
    }
}

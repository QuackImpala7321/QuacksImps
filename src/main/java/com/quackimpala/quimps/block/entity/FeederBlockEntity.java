package com.quackimpala.quimps.block.entity;

import com.quackimpala.quimps.registry.QIBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.inventory.LootableInventory;
import net.minecraft.inventory.SingleStackInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FeederBlockEntity extends BlockEntity implements LootableInventory, SingleStackInventory.SingleStackBlockEntityInventory {
    public static final int EAT_EVENT = 1;
    protected static final String ITEM_NBT = "item";

    private ItemStack stack;
    private RegistryKey<LootTable> lootTableId;
    private long lootTableSeed;

    public FeederBlockEntity(BlockPos pos, BlockState state) {
        super(QIBlockEntities.FEEDER, pos, state);
        stack = ItemStack.EMPTY;
    }

    @Override
    protected void addComponents(ComponentMap.Builder builder) {
        super.addComponents(builder);
        builder.add(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(List.of(stack)));
    }

    @Override
    protected void readComponents(ComponentsAccess components) {
        super.readComponents(components);
        stack = components.getOrDefault(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT).copyFirstStack();
    }

    @Override
    public boolean onSyncedBlockEvent(int type, int data) {
        if (getWorld() != null && type == EAT_EVENT) {
            decreaseStack(1);
            return true;
        }
        return super.onSyncedBlockEvent(type, data);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createComponentlessNbt(registryLookup);
    }

    @Override
    public void removeFromCopiedStackNbt(NbtCompound nbt) {
        super.removeFromCopiedStackNbt(nbt);
        nbt.remove(ITEM_NBT);
    }

    public void setStack(ItemStack stack) {
        generateLoot(null);
        this.stack = stack;
    }

    public ItemStack getStack() {
        generateLoot(null);
        return stack;
    }

    @Override
    public ItemStack decreaseStack(int count) {
        generateLoot(null);
        return SingleStackBlockEntityInventory.super.decreaseStack(count);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        if (!writeLootTable(nbt) && !stack.isEmpty())
            nbt.put(ITEM_NBT, stack.encode(registryLookup));
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        if (!readLootTable(nbt) && nbt.contains(ITEM_NBT, NbtElement.COMPOUND_TYPE))
            stack = ItemStack.fromNbt(registryLookup, nbt.getCompound(ITEM_NBT)).orElse(ItemStack.EMPTY);
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

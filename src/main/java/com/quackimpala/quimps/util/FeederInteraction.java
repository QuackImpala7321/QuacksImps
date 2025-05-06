package com.quackimpala.quimps.util;

import com.quackimpala.quimps.QuacksImps;
import com.quackimpala.quimps.block.FeederBlock;
import com.quackimpala.quimps.block.entity.FeederBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FeederInteraction {
    private final Item resultItem;
    private final int count;

    public FeederInteraction(Item resultItem, int count) {
        this.resultItem = resultItem;
        this.count = count;
    }

    public void interact(World world, BlockPos pos, FeederBlockEntity feederBlockEntity) {
        QuacksImps.LOGGER.info(toString());
        final ItemStack newStack = new ItemStack(resultItem, count);
        if (!feederBlockEntity.isFilled()) {
            feederBlockEntity.setStack(newStack);
        } else if (!world.isClient()) {
            final Vec3d spawnPos = pos.toCenterPos().offset(world.getBlockState(pos).get(FeederBlock.FACING), 1.0);
            final ItemEntity itemEntity = new ItemEntity(world, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), newStack);
            world.spawnEntity(itemEntity);
        }
    }

    @Override
    public String toString() {
        return String.format("Item: %s, Count: %s", Registries.ITEM.getId(resultItem), count);
    }
}

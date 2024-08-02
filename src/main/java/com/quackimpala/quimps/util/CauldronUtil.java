package com.quackimpala.quimps.util;

import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.math.BlockPos;

public class CauldronUtil {
    private static DispenserBlockEntity DISPENSER_BLOCK_ENTITY;
    private static ItemStack OUTPUT;

    public static DispenserBlockEntity getDispenserBlockEntity() {
        return DISPENSER_BLOCK_ENTITY;
    }

    public static void setOutput(ItemStack output) {
        OUTPUT = output;
    }

    public static ItemStack getOutput() {
        return OUTPUT;
    }

    public static boolean tryInteractCauldron(ServerWorld world, BlockPos pos, BlockState state, ItemStack stack, DispenserBlockEntity dispenserBlockEntity) {
        final Block block = state.getBlock();
        if (!(block instanceof AbstractCauldronBlock cauldronBlock))
            return false;

        DISPENSER_BLOCK_ENTITY = dispenserBlockEntity;
        final CauldronBehavior behavior = cauldronBlock.behaviorMap.map().get(stack.getItem());
        final ItemActionResult result = behavior.interact(
                state, world, pos, null, Hand.MAIN_HAND, stack);

        return result.isAccepted();
    }
}

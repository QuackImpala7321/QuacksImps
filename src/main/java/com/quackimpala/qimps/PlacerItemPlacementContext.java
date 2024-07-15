package com.quackimpala.qimps;

import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class PlacerItemPlacementContext extends ItemPlacementContext {
    private final Direction facing;

    public PlacerItemPlacementContext(World world, BlockPos pos, ItemStack stack, Direction facing) {
        super(world, null, Hand.MAIN_HAND, stack,
                new BlockHitResult(pos.toCenterPos(), facing, pos, false));
        this.facing = facing;
    }

    @Override
    public Direction getPlayerLookDirection() {
        return facing.getOpposite();
    }

    @Override
    public Direction getVerticalPlayerLookDirection() {
        return facing == Direction.DOWN
                ? Direction.DOWN
                : Direction.UP;
    }
}

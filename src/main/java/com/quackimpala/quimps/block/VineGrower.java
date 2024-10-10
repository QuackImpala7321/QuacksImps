package com.quackimpala.quimps.block;

import com.quackimpala.quimps.QuacksImps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.VineBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.List;

public class VineGrower {
    public static final BooleanProperty CROPPED = BooleanProperty.of("cropped");

    private static final Direction[] GROWTH = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
    public static List<Direction> growthDirections(BlockState state, Random random) {
        final List<Direction> growth = new ArrayList<>();
        for (final Direction dir : GROWTH)
            if (state.get(VineBlock.getFacingProperty(dir)) && random.nextFloat() < 0.75f)
                growth.add(dir);
        return growth;
    }

    public static void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        final List<Direction> growth = growthDirections(state, random);
        if (growth.isEmpty()) {
            QuacksImps.LOGGER.info("empty");
            return;
        }
        final BlockPos down = pos.down();
        BlockState grownState = state.getBlock().getDefaultState();
        for (final Direction direction : growth)
            grownState = grownState.with(VineBlock.getFacingProperty(direction), true);
        world.setBlockState(down, grownState, Block.NOTIFY_LISTENERS);
    }
}

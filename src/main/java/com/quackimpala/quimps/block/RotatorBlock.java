package com.quackimpala.quimps.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class RotatorBlock extends Block {
    public static final DirectionProperty FACING = Properties.FACING;
    public static final BooleanProperty TRIGGERED = Properties.TRIGGERED;
    public static final BooleanProperty POSITIVE = BooleanProperty.of("positive");
    private static final int DELAY = 4;

    public static MapCodec<RotatorBlock> CODEC = createCodec(RotatorBlock::new);

    public RotatorBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(TRIGGERED, false)
                .with(POSITIVE, true)
        );
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, TRIGGERED, POSITIVE);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        world.setBlockState(pos, state.with(POSITIVE, !state.get(POSITIVE)));
        return ActionResult.success(world.isClient());
    }

    private void rotateBlock(BlockState state, ServerWorld world, BlockPos pos) {
        final Direction facing = state.get(FACING);
        final BlockPos targetPos = pos.offset(facing);
        final BlockState targetState = world.getBlockState(targetPos);

        if (facing.getAxis().isVertical()) {
            final BlockState newState = targetState.rotate(state.get(POSITIVE) ? BlockRotation.CLOCKWISE_90 : BlockRotation.COUNTERCLOCKWISE_90);
            if (newState.canPlaceAt(world, targetPos)) {
                world.setBlockState(targetPos, newState);
            }
            return;
        }

        final Collection<Property<?>> props = targetState.getProperties();
        if (props.contains(Properties.FACING)) {
            final Direction targetDir = targetState.get(Properties.FACING);
            if (targetDir.getAxis().isVertical()) {
                final BlockState newState = targetState.with(Properties.FACING, targetDir.getOpposite());
                if (newState.canPlaceAt(world, targetPos)) {
                    world.setBlockState(targetPos, targetState.with(Properties.FACING, targetDir.getOpposite()));
                }
            }
        } else if (props.contains(Properties.BLOCK_FACE)) {
            final BlockState newState = targetState.with(Properties.BLOCK_FACE,
                    cycle(targetState, Properties.BLOCK_FACE, state.get(POSITIVE)));
            if (newState.canPlaceAt(world, targetPos)) {
                world.setBlockState(targetPos, newState);
            }
        }
    }

    private <T extends Comparable<T>> T cycle(BlockState state, Property<T> property, boolean positive) {
        final Collection<T> values = property.getValues();
        final T current = state.get(property);
        return positive ? Util.next(values, current) : Util.previous(values, current);
    }

    private boolean isPowered(BlockState state, World world, BlockPos pos) {
        final Direction facing = state.get(FACING);
        for (final Direction dir : FACING.getValues()) {
            if (dir != facing && world.isEmittingRedstonePower(pos.offset(dir), dir)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        final boolean powered = isPowered(state, world, pos);
        final boolean triggered = state.get(TRIGGERED);

        if (powered && !triggered) {
            world.scheduleBlockTick(pos, this, DELAY);
            world.setBlockState(pos, state.with(TRIGGERED, true));
        } else if (!powered && triggered) {
            world.setBlockState(pos, state.with(TRIGGERED, false));
        }
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        rotateBlock(state, world, pos);
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
    }

    @Override
    protected MapCodec<? extends RotatorBlock> getCodec() {
        return CODEC;
    }
}

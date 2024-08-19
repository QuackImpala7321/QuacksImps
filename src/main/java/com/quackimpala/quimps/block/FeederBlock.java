package com.quackimpala.quimps.block;

import com.mojang.serialization.MapCodec;
import com.quackimpala.quimps.block.entity.FeederBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class FeederBlock extends BlockWithEntity {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

    public static MapCodec<FeederBlock> CODEC = createCodec(FeederBlock::new);
    protected static VoxelShape SHAPE = VoxelShapes.union(
            createCuboidShape(0, 13, 0, 16, 16, 16),
            createCuboidShape(1, 0, 1, 15, 13, 15)
    );

    public FeederBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState()
                .with(FACING, Direction.NORTH)
        );
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!(world.getBlockEntity(pos) instanceof FeederBlockEntity feederBlockEntity))
            return ItemActionResult.CONSUME;

        final ItemStack feederStack = feederBlockEntity.getStack();
        if (feederStack.isEmpty()) {
            if (!stack.isEmpty()) {
                player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
                feederBlockEntity.setStack(stack.splitUnlessCreative(1, player));
            }
        } else if (ItemStack.areItemsAndComponentsEqual(stack, feederStack) && feederStack.getCount() < feederStack.getMaxCount()) {
            player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));

            feederStack.increment(1);
            stack.decrementUnlessCreative(1, player);
        } else {
            final Direction facing = state.get(FACING);
            final Vec3d spawnPos = pos.offset(facing).toCenterPos();
            final ItemEntity itemEntity = new ItemEntity(world, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), feederStack.copyAndEmpty());
            itemEntity.setVelocity(facing.getVector().getX() * 0.3, facing.getVector().getY(), facing.getVector().getZ() * 0.3);

            world.spawnEntity(itemEntity);
        }
        feederBlockEntity.markDirty();
        world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);

        return ItemActionResult.success(world.isClient());
    }

    @Override
    protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }

    @Override
    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FeederBlockEntity(pos, state);
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        ItemScatterer.onStateReplaced(state, newState, world, pos);
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }
}

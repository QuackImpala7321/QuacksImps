package com.quackimpala.qimps.block;

import com.mojang.serialization.MapCodec;
import com.quackimpala.qimps.registry.ModBlockEntities;
import com.quackimpala.qimps.registry.ModStats;
import com.quackimpala.qimps.PlacerItemPlacementContext;
import com.quackimpala.qimps.block.entity.PlacerBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PlacerBlock extends BlockWithEntity {
    public static MapCodec<PlacerBlock> CODEC = createCodec(PlacerBlock::new);
    public static final DirectionProperty FACING = Properties.FACING;
    public static final BooleanProperty TRIGGERED = Properties.TRIGGERED;

    public PlacerBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(TRIGGERED, false)
        );
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, TRIGGERED);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient)
            return ActionResult.SUCCESS;

        final BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof PlacerBlockEntity placerBlockEntity) {
            player.openHandledScreen(placerBlockEntity);
            player.incrementStat(ModStats.INSPECT_PLACER);
        }

        return ActionResult.CONSUME;
    }

    private boolean tryPlace(ServerWorld world, BlockState state, BlockPos pos) {
        final Direction facing = state.get(FACING);
        final BlockPos targetPos = pos.offset(facing);
        if (!world.getBlockState(targetPos).getRegistryEntry().isIn(BlockTags.REPLACEABLE))
            return false;

        final Optional<PlacerBlockEntity> optional = world.getBlockEntity(pos, ModBlockEntities.PLACER);
        if (optional.isEmpty())
            return false;

        final PlacerBlockEntity placerBlockEntity = optional.get();
        final int slotId = placerBlockEntity.getFirstNonEmptySlot();
        if (slotId == -1)
            return false;

        final ItemStack stack = placerBlockEntity.getStack(slotId);
        if (!(stack.getItem() instanceof BlockItem blockItem))
            return false;

        final boolean result = blockItem.place(
                new PlacerItemPlacementContext(world, targetPos, stack, facing)).isAccepted();

        world.emitGameEvent(GameEvent.BLOCK_ACTIVATE, pos, GameEvent.Emitter.of(placerBlockEntity.getCachedState()));
        return result;
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        boolean powered = world.isReceivingRedstonePower(pos);
        boolean triggered = state.get(TRIGGERED);

        if (powered && !triggered) {
            world.scheduleBlockTick(pos, this, 4);
            world.setBlockState(pos, state.with(TRIGGERED, true), Block.NOTIFY_ALL);
        } else if (!powered && triggered)
            world.setBlockState(pos, state.with(TRIGGERED, false), Block.NOTIFY_ALL);
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        world.syncWorldEvent(
                tryPlace(world, state, pos) ? WorldEvents.DISPENSER_DISPENSES : WorldEvents.DISPENSER_FAILS,
                pos, 0);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PlacerBlockEntity(pos, state);
    }

    @Override
    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
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
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        ItemScatterer.onStateReplaced(state, newState, world, pos);
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }
}

package com.quackimpala.qimps;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.GameEvent.Emitter;

public interface DispenserMixinAccessor {
    BooleanProperty WAXED = BooleanProperty.of("waxed");

    default void wax(World world, BlockPos pos, BlockState state, PlayerEntity player, ItemStack stack) {
        final BlockState newState = state.with(WAXED, true);
        world.setBlockState(pos, newState, 0);

        world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, Emitter.of(player, newState));
        world.syncWorldEvent(player, WorldEvents.BLOCK_WAXED, pos, 0);

        if (player instanceof ServerPlayerEntity serverPlayerEntity)
            Criteria.ITEM_USED_ON_BLOCK.trigger(serverPlayerEntity, pos, stack);
    }

    boolean isWaxed(BlockState state);
}

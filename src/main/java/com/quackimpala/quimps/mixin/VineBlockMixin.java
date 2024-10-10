package com.quackimpala.quimps.mixin;

import com.quackimpala.quimps.block.VineGrower;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.VineBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.quackimpala.quimps.block.VineGrower.CROPPED;

@Mixin(VineBlock.class)
public abstract class VineBlockMixin extends Block implements Fertilizable {
    public VineBlockMixin(Settings settings) {
        super(settings);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!state.get(CROPPED) && stack.isIn(ConventionalItemTags.SHEAR_TOOLS)) {
            if (player instanceof ServerPlayerEntity serverPlayer)
                Criteria.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, stack);

            world.playSound(player, pos, SoundEvents.BLOCK_GROWING_PLANT_CROP, SoundCategory.BLOCKS, 1.0f, 1.0f);
            final BlockState cropped = state.with(CROPPED, true);
            world.setBlockState(pos, cropped);
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(player, cropped));
            if (player != null)
                stack.damage(1, player, LivingEntity.getSlotForHand(hand));
            return ItemActionResult.success(world.isClient);
        }
        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    @Override
    protected boolean hasRandomTicks(BlockState state) {
        return super.hasRandomTicks(state) && !state.get(CROPPED);
    }

    @Inject(
            method = "appendProperties",
            at = @At("TAIL")
    )
    private void appendPropertiesMixin(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(CROPPED);
    }

    @ModifyArg(
            method = "<init>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/VineBlock;setDefaultState(Lnet/minecraft/block/BlockState;)V"),
            index = 0
    )
    private BlockState defaultStateMixin(BlockState state) {
        return state.with(CROPPED, false);
    }

    @Override
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        if (state.get(VineBlock.UP)) return false;
        final BlockPos down = pos.down();
        final BlockState downState = world.getBlockState(down);
        return downState.isAir();
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return isFertilizable(world, pos, state);
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        VineGrower.grow(world, random, pos, state);
    }
}

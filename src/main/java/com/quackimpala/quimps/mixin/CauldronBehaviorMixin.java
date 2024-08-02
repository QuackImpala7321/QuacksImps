package com.quackimpala.quimps.mixin;

import com.quackimpala.quimps.util.CauldronUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(CauldronBehavior.class)
public interface CauldronBehaviorMixin {
    @Inject(
            method = "method_32220",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;",
                    shift = At.Shift.AFTER
            ),
            cancellable = true)
    private static void waterCauldronBottleBehaviorMixin(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, CallbackInfoReturnable<ItemActionResult> cir) {
        if (player != null)
            return;

        LeveledCauldronBlock.decrementFluidLevel(state, world, pos);
        world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
        world.emitGameEvent(null, GameEvent.FLUID_PICKUP, pos);

        cir.setReturnValue(ItemActionResult.CONSUME);
    }

    @Inject(
            method = "method_32222",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;",
                    shift = At.Shift.AFTER
            ),
            cancellable = true)
    private static void emptyCauldronPotionBehaviorMixin(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, CallbackInfoReturnable<ItemActionResult> cir) {
        if (player != null)
            return;

        world.setBlockState(pos, Blocks.WATER_CAULDRON.getDefaultState());
        world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
        world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);

        cir.setReturnValue(ItemActionResult.CONSUME);
    }

    @Inject(
            method = "method_32219",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;setStackInHand(Lnet/minecraft/util/Hand;Lnet/minecraft/item/ItemStack;)V",
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    private static void waterCauldronPotionBehaviorMixin(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, CallbackInfoReturnable<ItemActionResult> cir) {
        if (player != null)
            return;

        world.setBlockState(pos, state.cycle(LeveledCauldronBlock.LEVEL));
        world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
        world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);

        cir.setReturnValue(ItemActionResult.CONSUME);
    }

    @Inject(
            method = "emptyCauldron",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;",
                    shift = At.Shift.AFTER),
            cancellable = true
    )
    private static void emptyCauldronMixin(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, ItemStack output, Predicate<BlockState> fullPredicate, SoundEvent soundEvent, CallbackInfoReturnable<ItemActionResult> cir) {
        if (player != null)
            return;

        world.setBlockState(pos, Blocks.CAULDRON.getDefaultState());
        world.playSound(null, pos, soundEvent, SoundCategory.BLOCKS, 1.0F, 1.0F);
        world.emitGameEvent(null, GameEvent.FLUID_PICKUP, pos);

        CauldronUtil.setOutput(output);
        cir.setReturnValue(ItemActionResult.CONSUME);
    }

    @Inject(
            method = "fillCauldron",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;",
                    shift = At.Shift.AFTER),
            cancellable = true
    )
    private static void fillCauldronMixin(World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, BlockState state, SoundEvent soundEvent, CallbackInfoReturnable<ItemActionResult> cir) {
        if (player != null)
            return;

        world.setBlockState(pos, state);
        world.playSound(null, pos, soundEvent, SoundCategory.BLOCKS, 1.0F, 1.0F);
        world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);

        cir.setReturnValue(ItemActionResult.CONSUME);
    }
}

package com.quackimpala.qimps.block.dispenser;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.GoatEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.event.GameEvent;

import java.util.List;

public class BucketItemDispenserBehavior extends FallibleItemDispenserBehavior {
    private static final TagKey<EntityType<?>> MILKABLE_TAG = TagKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of("c", "milkable"));

    @Override
    public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        ServerWorld world = pointer.world();
        BlockPos blockPos = pointer.pos().offset(pointer.state().get(DispenserBlock.FACING));
        BlockState blockState = world.getBlockState(blockPos);
        Block block = blockState.getBlock();
        setSuccess(false);
        if (block instanceof FluidDrainable fluidDrainable) {
            ItemStack itemStack = fluidDrainable.tryDrainFluid(null, world, blockPos, blockState);
            setSuccess(!itemStack.isEmpty());
            if (isSuccess()) {
                world.emitGameEvent(null, GameEvent.FLUID_PICKUP, blockPos);
                Item item = itemStack.getItem();
                return this.decrementStackWithRemainder(pointer, stack, new ItemStack(item));
            } else {
                return super.dispenseSilently(pointer, stack);
            }
        } else if (tryMilkEntity(world, blockPos)) {
            final ItemStack milkStack = new ItemStack(Items.MILK_BUCKET);
            super.dispenseSilently(pointer, milkStack);
            return decrementStackWithRemainder(pointer, stack, milkStack);
        }

        return stack;
    }

    private boolean tryMilkEntity(ServerWorld world, BlockPos pos) {
        final List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class, new Box(pos), entity ->
                entity.getType().isIn(MILKABLE_TAG) && EntityPredicates.EXCEPT_SPECTATOR.test(entity));

        if (entities.isEmpty())
            return false;

        setSuccess(true);
        final LivingEntity entity = entities.get(0);
        if (entity instanceof GoatEntity goat)
            world.playSound(null, pos, goat.getMilkingSound(), SoundCategory.BLOCKS);
        else
            world.playSound(null, pos, SoundEvents.ENTITY_COW_MILK, SoundCategory.BLOCKS);

        return true;
    }
}

package com.quackimpala.quimps.mixin.dispenser_behavior;

import com.quackimpala.quimps.registry.tag.QIEntityTags;
import com.quackimpala.quimps.util.CauldronUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.GoatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(targets = "net.minecraft.block.dispenser.DispenserBehavior$16")
public abstract class BucketItemDispenserBehavior extends ItemDispenserBehavior {
    @Inject(
            method = "dispenseSilently",
            at = @At("HEAD"),
            cancellable = true
    )
    private void dispenseMixin(BlockPointer pointer, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        final ServerWorld world = pointer.world();
        final BlockPos targetPos = pointer.pos().offset(pointer.state().get(DispenserBlock.FACING));
        final BlockState targetState = world.getBlockState(targetPos);

        if (tryMilkEntity(world, targetPos)) {
            final ItemStack milkStack = new ItemStack(Items.MILK_BUCKET);
            super.dispenseSilently(pointer, milkStack);
            cir.setReturnValue(
                    decrementStackWithRemainder(pointer, stack, milkStack));
        } else if (CauldronUtil.tryInteractCauldron(world, targetPos, targetState, stack, pointer.blockEntity()))
            cir.setReturnValue(CauldronUtil.getOutput());
    }

    @Unique
    private boolean tryMilkEntity(ServerWorld world, BlockPos pos) {
        final List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class, new Box(pos), entity ->
                entity.getType().isIn(QIEntityTags.MILKABLE_TAG) && EntityPredicates.EXCEPT_SPECTATOR.test(entity));

        if (entities.isEmpty())
            return false;

        final LivingEntity entity = entities.get(0);
        if (entity instanceof GoatEntity goat)
            world.playSound(null, pos, goat.getMilkingSound(), SoundCategory.BLOCKS);
        else
            world.playSound(null, pos, SoundEvents.ENTITY_COW_MILK, SoundCategory.BLOCKS);

        return true;
    }
}

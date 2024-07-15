package com.quackimpala.qimps.block.dispenser;

import com.quackimpala.qimps.QuacksImps;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class BowlItemDispenserBehavior extends FallibleItemDispenserBehavior {
    @Override
    protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        final ServerWorld world = pointer.world();
        final BlockPos targetPos = pointer.pos().offset(pointer.state().get(DispenserBlock.FACING));
        final Box box = new Box(targetPos);

        final AtomicReference<MooshroomEntity> mooshroomEntityRef = new AtomicReference<>();
        final List<MooshroomEntity> entities = world.getEntitiesByClass(MooshroomEntity.class, box, mooshroomEntity -> {
            if (mooshroomEntityRef.get() != null || mooshroomEntity.isBaby())
                return false;

            if (mooshroomEntity.getVariant() == MooshroomEntity.Type.BROWN)
                mooshroomEntityRef.set(mooshroomEntity);

            return true;
        });
        setSuccess(!entities.isEmpty());
        if (!isSuccess())
            return stack;

        final MooshroomEntity mooshroomEntity;
        if (mooshroomEntityRef.get() == null)
            mooshroomEntity = entities.get(0);
        else
            mooshroomEntity = mooshroomEntityRef.get();

        final boolean hasStewEffects = mooshroomEntity.stewEffects != null;
        final ItemStack stewStack;
        if (hasStewEffects) {
            stewStack = new ItemStack(Items.SUSPICIOUS_STEW);
            stewStack.set(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS, mooshroomEntity.stewEffects);
            mooshroomEntity.stewEffects = null;
        } else
            stewStack = new ItemStack(Items.MUSHROOM_STEW);

        mooshroomEntity.playSound(hasStewEffects
                ? SoundEvents.ENTITY_MOOSHROOM_SUSPICIOUS_MILK
                : SoundEvents.ENTITY_MOOSHROOM_MILK,
                1.0F, 1.0F);

        super.dispenseSilently(pointer, stewStack);
        return decrementStackWithRemainder(pointer, stack, stewStack);
    }
}

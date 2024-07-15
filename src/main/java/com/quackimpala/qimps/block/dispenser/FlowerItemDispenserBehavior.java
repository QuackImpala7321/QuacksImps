package com.quackimpala.qimps.block.dispenser;

import com.quackimpala.qimps.QuacksImps;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.component.type.SuspiciousStewEffectsComponent;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Box;

import java.util.List;
import java.util.Optional;

public class FlowerItemDispenserBehavior extends ItemDispenserBehavior {
    @Override
    protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        final ServerWorld world = pointer.world();
        final Box box = new Box(pointer.pos().offset(
                pointer.state().get(DispenserBlock.FACING)
        ));

        final List<MooshroomEntity> entities = world.getEntitiesByClass(MooshroomEntity.class, box, mooshroomEntity ->
                !mooshroomEntity.isBaby() && mooshroomEntity.getVariant() == MooshroomEntity.Type.BROWN);

        if (entities.isEmpty())
            return super.dispenseSilently(pointer, stack);

        final MooshroomEntity mooshroomEntity = entities.get(0);
        if (mooshroomEntity.stewEffects != null)
            return super.dispenseSilently(pointer, stack);

        final Optional<SuspiciousStewEffectsComponent> optional = mooshroomEntity.getStewEffectFrom(stack);
        if (optional.isEmpty())
            return super.dispenseSilently(pointer, stack);

        mooshroomEntity.stewEffects = optional.get();
        stack.decrement(1);

        mooshroomEntity.playSound(SoundEvents.ENTITY_MOOSHROOM_EAT, 2.0F, 1.0F);
        return stack;
    }
}

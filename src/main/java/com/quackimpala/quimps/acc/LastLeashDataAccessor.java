package com.quackimpala.quimps.acc;

import com.mojang.datafixers.util.Either;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Leashable.LeashData;
import net.minecraft.entity.decoration.LeashKnotEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public interface LastLeashDataAccessor {
    String LAST_LEASH = "lastLeash";

    @Nullable
    LeashData getLastLeashData();
    void setLastLeashData(@Nullable LeashData lastLeashData);

    default void writeLastLeashData(NbtCompound nbt, LeashData data) {
        if (data == null)
            return;

        Either<UUID, BlockPos> either = data.unresolvedLeashData;
        final Entity holder = data.leashHolder;
        if (holder instanceof LeashKnotEntity leashKnotEntity) {
            either = Either.right(leashKnotEntity.getAttachedBlockPos());
        } else if (holder != null) {
            either = Either.left(holder.getUuid());
        }

        if (either == null)
            return;

        nbt.put(LAST_LEASH, either.map((uuid) -> {
            NbtCompound nbtCompound = new NbtCompound();
            nbtCompound.putUuid("UUID", uuid);
            return nbtCompound;
        }, NbtHelper::fromBlockPos));
    }

    default LeashData readLastLeashData(NbtCompound nbt) {
        if (nbt.contains(LAST_LEASH, NbtElement.COMPOUND_TYPE))
            return new LeashData(Either.left(nbt.getCompound(LAST_LEASH).getUuid("UUID")));

        if (nbt.contains(LAST_LEASH, NbtElement.INT_ARRAY_TYPE)) {
            final Optional<BlockPos> pos = NbtHelper.toBlockPos(nbt, LAST_LEASH);
            if (pos.isPresent())
                return new LeashData(Either.right(pos.get()));
        }

        return null;
    }
}

package com.quackimpala.qimps;

import com.quackimpala.qimps.block.entity.PlacerBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static final BlockEntityType<PlacerBlockEntity> PLACER = create("placer",
            BlockEntityType.Builder.create(PlacerBlockEntity::new, ModBlocks.PLACER));

    private static <T extends BlockEntity> BlockEntityType<T> create(String id, BlockEntityType.Builder<T> builder) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(QuacksImps.MOD_ID, id), builder.build());
    }

    public static void registerBlockEntities() { }
}

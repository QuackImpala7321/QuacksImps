package com.quackimpala.quimps.registry;

import com.quackimpala.quimps.QuacksImps;
import com.quackimpala.quimps.block.entity.FeederBlockEntity;
import com.quackimpala.quimps.block.entity.PlacerBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public interface QIBlockEntities {
    BlockEntityType<PlacerBlockEntity> PLACER = create("placer", PlacerBlockEntity::new, QIBlocks.PLACER);
    BlockEntityType<FeederBlockEntity> FEEDER = create("feeder", FeederBlockEntity::new, QIBlocks.FEEDER);

    private static <T extends BlockEntity> BlockEntityType<T> create(String id, BlockEntityType.BlockEntityFactory<T> factory, Block... blocks) {
        return create(id, BlockEntityType.Builder.create(factory, blocks));
    }
    private static <T extends BlockEntity> BlockEntityType<T> create(String id, BlockEntityType.Builder<T> builder) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(QuacksImps.MOD_ID, id), builder.build());
    }

    static void registerBlockEntities() { }
}

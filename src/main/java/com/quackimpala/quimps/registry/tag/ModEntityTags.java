package com.quackimpala.quimps.registry.tag;

import net.fabricmc.fabric.api.tag.convention.v2.TagUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModEntityTags {
    public static final TagKey<EntityType<?>> MILKABLE_TAG = register(Identifier.of(TagUtil.C_TAG_NAMESPACE, "milkable"));

    public static TagKey<EntityType<?>> register(Identifier id) {
        return TagKey.of(RegistryKeys.ENTITY_TYPE, id);
    }
}

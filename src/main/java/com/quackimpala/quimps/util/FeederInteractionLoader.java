package com.quackimpala.quimps.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.quackimpala.quimps.QuacksImps;
import com.quackimpala.quimps.block.entity.FeederBlockEntity;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FeederInteractionLoader extends SinglePreparationResourceReloader<Map<EntityType<?>, Map<Item, FeederInteraction>>> implements IdentifiableResourceReloadListener {
    private final Map<EntityType<?>, Map<Item, FeederInteraction>> interactionMap;

    public FeederInteractionLoader() {
        interactionMap = new HashMap<>();
    }

    public void interact(World world, BlockPos pos, FeederBlockEntity feederBlockEntity, Item item, Entity entity) {
        QuacksImps.LOGGER.info(entity.toString());
        final EntityType<?> entityType = entity.getType();
        if (interactionMap.containsKey(entityType)) {
            final Map<Item, FeederInteraction> entityInteractions = interactionMap.get(entityType);
            QuacksImps.LOGGER.info(item.toString());
            if (entityInteractions.containsKey(item)) {
                entityInteractions.get(item).interact(world, pos, feederBlockEntity);
            }
        }
    }

    private Map<Item, FeederInteraction> deserialize(JsonObject json) {
        final Map<Item, FeederInteraction> interactions = new HashMap<>();
        final JsonObject entries = json.getAsJsonObject("entries");
        for (final Map.Entry<String, JsonElement> entry : entries.entrySet()) {
            final Item breedItem = Registries.ITEM.get(Identifier.tryParse(entry.getKey()));
            final JsonObject result = entry.getValue().getAsJsonObject();
            final int count = result.get("count").getAsInt();
            final Item resultItem = Registries.ITEM.get(Identifier.tryParse(result.get("id").getAsString()));

            interactions.put(breedItem, new FeederInteraction(resultItem, count));
        }
        return interactions;
    }

    private Identifier entityIdFromResourceId(Identifier id) {
        final String namespace = id.getNamespace();
        final String resourcePath = id.getPath();
        final int lastSlashIdx = resourcePath.lastIndexOf('/') + 1;
        final String path = resourcePath.substring(lastSlashIdx, resourcePath.length() - 5);
        return Identifier.of(namespace, path);
    }

    @Override
    protected Map<EntityType<?>, Map<Item, FeederInteraction>> prepare(ResourceManager manager, Profiler profiler) {
        final Map<EntityType<?>, Map<Item, FeederInteraction>> preparedInteractions = new HashMap<>();
        for (final Map.Entry<Identifier, Resource> entry : manager.findResources("feeder_table", identifier -> identifier.getPath().endsWith(".json")).entrySet()) {
            try (BufferedReader reader = entry.getValue().getReader()) {
                final JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                final Identifier entityId = entityIdFromResourceId(entry.getKey());
                QuacksImps.LOGGER.info(entityId.toString());
                final EntityType<?> entityType = Registries.ENTITY_TYPE.get(entityId);
                if (preparedInteractions.containsKey(entityType)) {
                    preparedInteractions.get(entityType).putAll(deserialize(json));
                } else {
                    preparedInteractions.put(entityType, deserialize(json));
                }
            } catch (IOException e) {
                QuacksImps.LOGGER.warn("Could not load recipe {}", entry.getKey());
            }
        }
        return preparedInteractions;
    }

    @Override
    protected void apply(Map<EntityType<?>, Map<Item, FeederInteraction>> prepared, ResourceManager manager, Profiler profiler) {
        interactionMap.clear();
        interactionMap.putAll(prepared);
    }

    @Override
    public Identifier getFabricId() {
        return Identifier.of(QuacksImps.MOD_ID, "feeder_interaction_loader");
    }
}

package com.quackimpala.quimps.datagen;

import com.quackimpala.quimps.QuacksImps;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class QIAdvancementProvider extends FabricAdvancementProvider {
    public QIAdvancementProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(RegistryWrapper.WrapperLookup registryLookup, Consumer<AdvancementEntry> consumer) {
        new QIAdvancements().accept(consumer);
    }

    protected static class QIAdvancements implements Consumer<Consumer<AdvancementEntry>> {
        @Override
        public void accept(Consumer<AdvancementEntry> consumer) {
            // placer
            buildEntry(Advancement.Builder.create()
                .rewards(AdvancementRewards.Builder.recipe(Identifier.of(QuacksImps.MOD_ID, "placer")))
                .criterion("has_copper", InventoryChangedCriterion.Conditions.items(Items.COPPER_INGOT)), consumer,
                    "recipes/redstone/placer");

            // feeder
            buildEntry(Advancement.Builder.create()
                .rewards(AdvancementRewards.Builder.recipe(Identifier.of(QuacksImps.MOD_ID, "feeder")))
                .criterion("has_bricks", InventoryChangedCriterion.Conditions.items(Items.BRICK)), consumer,
                    "recipes/decorations/feeder");
        }

        private static AdvancementEntry buildEntry(Advancement.Builder builder, Consumer<AdvancementEntry> consumer, String path) {
            final AdvancementEntry entry = builder.build(Identifier.of(QuacksImps.MOD_ID, path));
            consumer.accept(entry);
            return entry;
        }
    }
}

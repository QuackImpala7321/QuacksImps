package com.quackimpala.quimps.util;

import com.quackimpala.quimps.QuacksImps;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.ArrayList;
import java.util.List;

public class QueueableLoader extends SinglePreparationResourceReloader<List<QueueableDispenserBehavior>> implements IdentifiableResourceReloadListener {
    private final List<QueueableDispenserBehavior> behaviors;

    public QueueableLoader() {
        behaviors = new ArrayList<>();
    }

    public void addBehavior(QueueableDispenserBehavior behavior) {
        behaviors.add(behavior);
    }

    @Override
    protected List<QueueableDispenserBehavior> prepare(ResourceManager manager, Profiler profiler) {
        return behaviors;
    }

    @Override
    protected void apply(List<QueueableDispenserBehavior> prepared, ResourceManager manager, Profiler profiler) {
        for (final QueueableDispenserBehavior behavior : prepared)
            behavior.reload();
    }

    @Override
    public Identifier getFabricId() {
        return Identifier.of(QuacksImps.MOD_ID, "queuable_loader");
    }
}

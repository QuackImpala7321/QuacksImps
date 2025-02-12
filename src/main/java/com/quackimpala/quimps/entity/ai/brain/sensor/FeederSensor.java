package com.quackimpala.quimps.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import com.quackimpala.quimps.block.FeederBlock;
import com.quackimpala.quimps.block.entity.FeederBlockEntity;
import com.quackimpala.quimps.entity.ai.brain.QIMemoryModules;
import com.quackimpala.quimps.registry.QIBlockEntities;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public class FeederSensor extends Sensor<AnimalEntity> {
    private final Predicate<ItemStack> temptations;

    public FeederSensor(Predicate<ItemStack> temptations) {
        super(20);
        this.temptations = temptations;
    }

    @Override
    protected void sense(ServerWorld world, AnimalEntity entity) {
        final Brain<?> brain = entity.getBrain();
        brain.remember(QIMemoryModules.TEMPTING_FEEDER, sampleClosest(world, entity));
    }

    private Optional<GlobalPos> sampleClosest(ServerWorld world, AnimalEntity entity) {
        final Box box = new Box(entity.getBlockPos()).expand(10, 2, 10);
        BlockPos closestPos = null;
        Path closestPath = null;
        for (int x = (int) box.minX; x <= box.maxX; x++)
            for (int y = (int) box.minY; y <= box.maxY; y++)
                for (int z = (int) box.minZ; z <= box.maxZ; z++) {
                    final BlockPos pos = new BlockPos(x, y, z);
                    final Optional<FeederBlockEntity> optionalF = entity.getWorld().getBlockEntity(pos, QIBlockEntities.FEEDER);
                    if (optionalF.isEmpty()) continue;
                    final FeederBlockEntity feeder = optionalF.get();

                    final Optional<Path> optionalP = tryGetPathForFeeder(feeder, entity);
                    if (optionalP.isEmpty()) continue;
                    final Path path = optionalP.get();
                    if (closestPath == null || path.getLength() < closestPath.getLength()) {
                        closestPos = pos;
                        closestPath = path;
                    }
                }
        if (closestPath == null)
            return Optional.empty();
        final RegistryKey<World> dimension = world.getRegistryKey();
        return Optional.of(GlobalPos.create(dimension, closestPos));
    }

    private Optional<Path> tryGetPathForFeeder(FeederBlockEntity feeder, AnimalEntity entity) {
        return isTemptedBy(feeder, entity) ?
                Optional.ofNullable(pathTo(feeder, entity))
                : Optional.empty();
    }

    private boolean isTemptedBy(FeederBlockEntity feeder, AnimalEntity entity) {
        return temptations.test(feeder.getStack()) && entity.canEat();
    }

    private Path pathTo(FeederBlockEntity feeder, AnimalEntity entity) {
        return entity.getNavigation().findPathTo(
                feeder.getPos().offset(feeder.getCachedState().get(FeederBlock.FACING)), 0);
    }

    @Override
    public Set<MemoryModuleType<?>> getOutputMemoryModules() {
        return ImmutableSet.of(QIMemoryModules.TEMPTING_FEEDER);
    }
}

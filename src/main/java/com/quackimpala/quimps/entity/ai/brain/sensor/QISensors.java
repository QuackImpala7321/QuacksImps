package com.quackimpala.quimps.entity.ai.brain.sensor;

import com.quackimpala.quimps.QuacksImps;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.sensor.TemptationsSensor;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public interface QISensors {
    Map<EntityType<? extends Entity>, SensorType<FeederSensor>> FEEDER_SENSORS = new HashMap<>();

    static void registerSensors() {}

    static SensorType<FeederSensor> getOrCreateFeederSensor(AnimalEntity entity, SensorType<TemptationsSensor> temptType) {
        if (!FEEDER_SENSORS.containsKey(entity.getType())) {
            final Identifier id = Registries.ENTITY_TYPE.getKey(entity.getType()).get().getValue();
            final String name = String.format("feeder-%s", id.toString().replaceAll(":", "/"));
            FEEDER_SENSORS.put(entity.getType(), register(name, () -> new FeederSensor(temptType.create().predicate)));
        }
        return FEEDER_SENSORS.get(entity.getType());
    }

    private static <U extends Sensor<?>> SensorType<U> register(String id, Supplier<U> factory) {
        return Registry.register(Registries.SENSOR_TYPE, Identifier.of(QuacksImps.MOD_ID, id), new SensorType<>(factory));
    }
}

package com.quackimpala.quimps;

import com.quackimpala.quimps.entity.ai.brain.QIMemoryModules;
import com.quackimpala.quimps.entity.ai.brain.sensor.QISensors;
import com.quackimpala.quimps.registry.*;
import com.quackimpala.quimps.util.QueueableLoader;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuacksImps implements ModInitializer {
    public static final String MOD_ID = "quimps";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final QueueableLoader QUEUEABLE_LOADER = new QueueableLoader();

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing the imps");

        QIBlocks.registerBlocks();
        QIBlockEntities.registerBlockEntities();
        QISoundEvents.registerSoundEvents();

        QIStats.registerStats();
        QIDispenserBehaviors.registerBehaviors();
        QIItems.populateItemGroups();
        QIScreenHandlers.registerScreenHandlers();

        QISensors.registerSensors();
        QIMemoryModules.registerMemoryModules();

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(QUEUEABLE_LOADER);
        QUEUEABLE_LOADER.addBehavior(QIDispenserBehaviors.FLOWERS);
    }
}

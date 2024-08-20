package com.quackimpala.quimps;

import com.quackimpala.quimps.registry.*;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuacksImps implements ModInitializer {
    public static String MOD_ID = "quimps";
    public static Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing the imps");

        QIBlocks.registerBlocks();
        QIBlockEntities.registerBlockEntities();
        QISoundEvents.registerSoundEvents();

        QIStats.registerStats();
        QIDispenserBehaviors.registerBehaviors();
        QIItems.populateItemGroups();
    }
}

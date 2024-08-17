package com.quackimpala.quimps;

import com.quackimpala.quimps.registry.*;
import com.quackimpala.quimps.registry.tag.ModEntityTags;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuacksImps implements ModInitializer {
    public static String MOD_ID = "quimps";
    public static Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing the imps");

        ModBlocks.registerBlocks();
        ModBlockEntities.registerBlockEntities();
        ModSoundEvents.registerSoundEvents();

        ModStats.registerStats();
        ModDispenserBehaviors.registerBehaviors();
        ModItems.populateItemGroups();
    }
}

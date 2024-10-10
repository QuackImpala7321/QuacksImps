package com.quackimpala.quimps;

import com.quackimpala.quimps.registry.*;
import com.quackimpala.quimps.screen.SingleSlotScreenHandler;
import net.fabricmc.api.ModInitializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuacksImps implements ModInitializer {
    public static final String MOD_ID = "quimps";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static ScreenHandlerType<SingleSlotScreenHandler> FEEDER_SCREEN;

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing the imps");
        FEEDER_SCREEN = registerScreenHandler("feeder", SingleSlotScreenHandler::new);

        QIBlocks.registerBlocks();
        QIBlockEntities.registerBlockEntities();
        QISoundEvents.registerSoundEvents();

        QIStats.registerStats();
        QIDispenserBehaviors.registerBehaviors();
        QIItems.populateItemGroups();
    }

    private static <T extends ScreenHandler> ScreenHandlerType<T> registerScreenHandler(String id, ScreenHandlerType.Factory<T> factory) {
        return Registry.register(Registries.SCREEN_HANDLER, Identifier.of(MOD_ID, id), new ScreenHandlerType<>(factory, FeatureFlags.VANILLA_FEATURES));
    }
}

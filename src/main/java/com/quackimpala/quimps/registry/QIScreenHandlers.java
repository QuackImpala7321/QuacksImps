package com.quackimpala.quimps.registry;

import com.quackimpala.quimps.QuacksImps;
import com.quackimpala.quimps.screen.SingleSlotScreenHandler;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public interface QIScreenHandlers {
    ScreenHandlerType<SingleSlotScreenHandler> FEEDER_SCREEN = registerScreenHandler("feeder", SingleSlotScreenHandler::new);

    static void registerScreenHandlers() {}

    private static <T extends ScreenHandler> ScreenHandlerType<T> registerScreenHandler(String id, ScreenHandlerType.Factory<T> factory) {
        return Registry.register(Registries.SCREEN_HANDLER, Identifier.of(QuacksImps.MOD_ID, id), new ScreenHandlerType<>(factory, FeatureFlags.VANILLA_FEATURES));
    }
}

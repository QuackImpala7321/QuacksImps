package com.quackimpala.quimps.client;

import com.quackimpala.quimps.client.gui.screen.SingleSlotScreen;
import com.quackimpala.quimps.registry.QIScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class QuacksImpsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(QIScreenHandlers.FEEDER_SCREEN, SingleSlotScreen::new);
    }
}

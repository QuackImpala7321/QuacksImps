package com.quackimpala.quimps.client;

import com.quackimpala.quimps.client.renderer.block.entity.FeederBlockEntityRenderer;
import com.quackimpala.quimps.registry.ModBlockEntities;
import com.quackimpala.quimps.registry.ModBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class QuacksImpsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.FEEDER, RenderLayer.getTranslucent());
        BlockEntityRendererFactories.register(ModBlockEntities.FEEDER, FeederBlockEntityRenderer::new);
    }
}

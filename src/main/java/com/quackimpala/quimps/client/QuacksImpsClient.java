package com.quackimpala.quimps.client;

import com.quackimpala.quimps.client.renderer.block.entity.FeederBlockEntityRenderer;
import com.quackimpala.quimps.registry.QIBlockEntities;
import com.quackimpala.quimps.registry.QIBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class QuacksImpsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(QIBlocks.FEEDER, RenderLayer.getTranslucent());
        BlockEntityRendererFactories.register(QIBlockEntities.FEEDER, FeederBlockEntityRenderer::new);
    }
}

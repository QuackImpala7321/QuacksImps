package com.quackimpala.quimps.client.gui.screen;

import com.quackimpala.quimps.QuacksImps;
import com.quackimpala.quimps.screen.SingleSlotScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SingleSlotScreen extends HandledScreen<SingleSlotScreenHandler> {
    private static final Identifier INVENTORY_TEXTURE = Identifier.of(QuacksImps.MOD_ID, "textures/gui/container/single_slot.png");

    public SingleSlotScreen(SingleSlotScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        final int x = (width - backgroundWidth) / 2;
        final int y = (height - backgroundHeight) / 2;

        context.drawTexture(INVENTORY_TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }
}

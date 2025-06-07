package com.wayacreate.wayacreatesays.client.gui.screens;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class WayaCreateGameModeScreen extends Screen {
    private static final Text TITLE = Text.translatable("selectWorld.gameMode");
    private static final Text WAYACREATE_INFO = Text.translatable("selectWorld.gameMode.wayacreate.info");
    
    private final Screen parent;
    
    public WayaCreateGameModeScreen(Screen parent) {
        super(TITLE);
        this.parent = parent;
    }
    
    @Override
    protected void init() {
        this.addDrawableChild(new ButtonWidget(
            this.width / 2 - 100, this.height / 4 + 48 + 24, 200, 20,
            Text.translatable("gameMode.wayacreate"),
            button -> {
                // Set game mode to WayaCreate mode
                if (this.client != null && this.client.interactionManager != null) {
                    this.client.interactionManager.setGameMode(com.wayacreate.wayacreatesays.game.WayaCreateGameModeType.WAYACREATE);
                }
                this.client.setScreen(this.parent);
            }
        ));
        
        this.addDrawableChild(new ButtonWidget(
            this.width / 2 - 100, this.height - 28, 200, 20,
            Text.translatable("gui.done"),
            button -> this.client.setScreen(this.parent)
        ));
    }
    
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        drawCenteredText(matrices, this.textRenderer, WAYACREATE_INFO, this.width / 2, 50, 0xAAAAAA);
        super.render(matrices, mouseX, mouseY, delta);
    }
}

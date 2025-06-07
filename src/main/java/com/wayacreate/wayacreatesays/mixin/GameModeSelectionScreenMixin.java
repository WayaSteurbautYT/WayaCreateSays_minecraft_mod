package com.wayacreate.wayacreatesays.mixin;

import com.wayacreate.wayacreatesays.client.gui.screens.WayaCreateGameModeScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreateWorldScreen.class)
public class GameModeSelectionScreenMixin extends Screen {
    protected GameModeSelectionScreenMixin(Text title) {
        super(title);
    }
    
    @Inject(method = "init", at = @At("TAIL"))
    private void addWayaCreateButton(CallbackInfo ci) {
        this.addDrawableChild(new ButtonWidget(
            this.width / 2 - 100, this.height / 4 + 48 + 24 * 2, 200, 20,
            Text.translatable("gameMode.wayacreate"),
            button -> {
                if (this.client != null) {
                    this.client.setScreen(new WayaCreateGameModeScreen(this));
                }
            }
        ));
    }
}

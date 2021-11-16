package fuzs.redstonetimer.client.screen;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.redstonetimer.RedstoneTimer;
import fuzs.redstonetimer.world.inventory.TimerMenu;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class TimerScreen extends Screen implements MenuAccess<TimerMenu> {
    private static final ResourceLocation TIMER_BACKGROUND_LOCATION = new ResourceLocation(RedstoneTimer.MOD_ID, "textures/gui/timer.png");

    private final TimerMenu menu;

    public TimerScreen(TimerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(NarratorChatListener.NO_TITLE);
        this.menu = pMenu;
    }

    @Override
    public TimerMenu getMenu() {
        return this.menu;
    }

    @Override
    public void onClose() {
        this.minecraft.player.closeContainer();
        super.onClose();
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(new Button(this.width / 2 + 43, this.height / 2 - 12, 40, 20, new TranslatableComponent("gui.timer.minus.milli", 200), (p_99024_) -> {
            this.sendButtonClick(-1);
        }));
        this.addRenderableWidget(new Button(this.width / 2 + 1, this.height / 2 - 12, 40, 20, new TranslatableComponent("gui.timer.minus.second", 1), (p_99024_) -> {
            this.sendButtonClick(-2);
        }));
        this.addRenderableWidget(new Button(this.width / 2 - 41, this.height / 2 - 12, 40, 20, new TranslatableComponent("gui.timer.minus.second", 10), (p_99024_) -> {
            this.sendButtonClick(-3);
        }));
        this.addRenderableWidget(new Button(this.width / 2 - 83, this.height / 2 - 12, 40, 20, new TranslatableComponent("gui.timer.minus.minute", 1), (p_99024_) -> {
            this.sendButtonClick(-4);
        }));
        this.addRenderableWidget(new Button(this.width / 2 - 83, this.height / 2 + 12, 40, 20, new TranslatableComponent("gui.timer.plus.milli", 200), (p_99024_) -> {
            this.sendButtonClick(1);
        }));
        this.addRenderableWidget(new Button(this.width / 2 - 41, this.height / 2 + 12, 40, 20, new TranslatableComponent("gui.timer.plus.second", 1), (p_99024_) -> {
            this.sendButtonClick(2);
        }));
        this.addRenderableWidget(new Button(this.width / 2 + 1, this.height / 2 + 12, 40, 20, new TranslatableComponent("gui.timer.plus.second", 10), (p_99024_) -> {
            this.sendButtonClick(3);
        }));
        this.addRenderableWidget(new Button(this.width / 2 + 43, this.height / 2 + 12, 40, 20, new TranslatableComponent("gui.timer.plus.minute", 1), (p_99024_) -> {
            this.sendButtonClick(4);
        }));
    }

    private void sendButtonClick(int pPageData) {
        if (this.menu.clickMenuButton(this.minecraft.player, pPageData)) {
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, pPageData);
        }
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pPoseStack);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TIMER_BACKGROUND_LOCATION);
        this.blit(pPoseStack,this.width / 2 - 88, this.height / 2 - 43, 0, 0, 176, 86);
        final TranslatableComponent text = new TranslatableComponent("gui.timer.interval", this.getMenu().getInterval());
        this.font.draw(pPoseStack, text, this.width / 2 - this.font.width(text.getVisualOrderText()) / 2, this.height / 2 - 32, 4210752);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        InputConstants.Key mouseKey = InputConstants.getKey(pKeyCode, pScanCode);
        if (super.keyPressed(pKeyCode, pScanCode, pModifiers)) {
            return true;
        } else if (this.minecraft.options.keyInventory.isActiveAndMatches(mouseKey)) {
            this.onClose();
            return true;
        }
        return false;
    }
}

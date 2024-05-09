package net.marsh.spacecraft.render.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.marsh.spacecraft.Spacecraft;
import net.marsh.spacecraft.render.area.EnergyInfoArea;
import net.marsh.spacecraft.render.menu.SteelFoundryMenu;
import net.marsh.spacecraft.util.MouseUtil;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.Optional;

@SuppressWarnings("ALL")
public class SteelFoundryScreen extends AbstractContainerScreen<SteelFoundryMenu> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Spacecraft.MOD_ID, "textures/gui/steel_foundry_screen.png");
    private EnergyInfoArea energyInfoArea;

    public SteelFoundryScreen(SteelFoundryMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
        assignEnergyInfoArea();
    }

    private void assignEnergyInfoArea() {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        energyInfoArea = new EnergyInfoArea(x + 8, y + 8, menu.blockEntity.getEnergyStorage());
    }

    @Override
    protected void renderLabels(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        renderEnergyAreaTooltips(pPoseStack, pMouseX, pMouseY, x, y);
    }

    private void renderEnergyAreaTooltips(PoseStack pPoseStack, int pMouseX, int pMouseY, int x, int y) {
        if(isMouseAboveArea(pMouseX, pMouseY, x, y, 8, 8, 16, 48)) {
            renderTooltip(pPoseStack, energyInfoArea.getTooltips(),
                    Optional.empty(), pMouseX - x, pMouseY - y);
        }
    }

    @Override
    protected void renderBg(PoseStack poseStack, float v, int i, int i1) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        this.blit(poseStack, x, y, 0, 0, imageWidth, imageHeight);

        renderCraftingProgressBar(poseStack, x, y);
        renderChargingProgressDiodes(poseStack, x, y);
        renderMoltenMetalPool(poseStack, x, y);
        energyInfoArea.draw(poseStack);
    }

    private void renderMoltenMetalPool(PoseStack poseStack, int x, int y) {
        if(menu.hasIronAndCarbon() && menu.isCharged()) {
            blit(poseStack, x + 68, y + 63, 0, 231, 39, 10);
        }
    }

    private void renderChargingProgressDiodes(PoseStack poseStack, int x, int y) {
        if(menu.isCharging()) {
            // White energy lines
            blit(poseStack, x + 25, y + 7, 176, 19, 71, 85);
            // Charging diodes
            blit(poseStack, x + 76, y + 13, 176, 0, 23, menu.getScaledChargingProgress());
            // Foundry drain outline
            blit(poseStack, x + 67, y + 63, 0, 245, 84, 11);
        }
    }

    private void renderCraftingProgressBar(PoseStack poseStack, int x, int y) {
        if(menu.isCrafting()) {
            blit(poseStack, x + 107, y + 69, 0, 241, menu.getScaledCraftingProgress(), 2);
        }
    }

    @Override
    public void render(PoseStack pPoseStack, int mouseX, int mouseY, float delta) {
        renderBackground(pPoseStack);
        super.render(pPoseStack, mouseX, mouseY, delta);
        renderTooltip(pPoseStack, mouseX, mouseY);
    }

    private boolean isMouseAboveArea(int pMouseX, int pMouseY, int x, int y, int offsetX, int offsetY, int width, int height) {
        return MouseUtil.isMouseOver(pMouseX, pMouseY, x + offsetX, y + offsetY, width, height);
    }
}

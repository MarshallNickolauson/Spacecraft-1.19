package net.marsh.spacecraft.render.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.marsh.spacecraft.Spacecraft;
import net.marsh.spacecraft.render.area.EnergyInfoArea;
import net.marsh.spacecraft.render.menu.CircuitFabricatorMenu;
import net.marsh.spacecraft.util.MouseUtil;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.Optional;

@SuppressWarnings("ALL")
public class CircuitFabricatorScreen extends AbstractContainerScreen<CircuitFabricatorMenu> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Spacecraft.MOD_ID, "textures/gui/circuit_fabricator_screen.png");
    private EnergyInfoArea energyInfoArea;

    public CircuitFabricatorScreen(CircuitFabricatorMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
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
        energyInfoArea = new EnergyInfoArea(x + 8, y + 17, menu.blockEntity.getEnergyStorage());
    }

    @Override
    protected void renderLabels(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        renderEnergyAreaTooltips(pPoseStack, pMouseX, pMouseY, x, y);
    }

    private void renderEnergyAreaTooltips(PoseStack pPoseStack, int pMouseX, int pMouseY, int x, int y) {
        if(isMouseAboveArea(pMouseX, pMouseY, x, y, 8, 17, 16, 48)) {
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

        this.blit(poseStack, x, y, 0, 0, imageWidth, imageHeight + 30);

        renderProgressBar(poseStack, x, y);
        renderDiamondToSiliconLine(poseStack, x, y);
        renderSiliconToRedstoneLine(poseStack, x, y);
        renderRedstoneDustAndTorchLine(poseStack, x, y);
        renderEnergyToOutputLine(poseStack, x, y);
        energyInfoArea.draw(poseStack);
    }

    private void renderEnergyToOutputLine(PoseStack poseStack, int x, int y) {
        if(menu.hasEnergy()) {
            blit(poseStack, x + 25, y + 78, 0, 238, 126, 18);
        }
    }

    private void renderRedstoneDustAndTorchLine(PoseStack poseStack, int x, int y) {
        if(menu.hasRedstoneTorch()) {
            blit(poseStack, x + 139, y + 37, 176, 54, 23, 48);
        }
    }

    private void renderSiliconToRedstoneLine(PoseStack poseStack, int x, int y) {
        if(menu.hasRedstoneDust()) {
            blit(poseStack, x + 91, y + 52, 176, 31, 30, 23);
        }
    }

    private void renderDiamondToSiliconLine(PoseStack poseStack, int x, int y) {
        if(menu.hasDiamondAndSilicon()) {
            blit(poseStack, x + 57, y + 24, 176, 10, 27, 21);
        }
    }

    private void renderProgressBar(PoseStack poseStack, int x, int y) {
        if(menu.isCrafting()) {
            blit(poseStack, x + 88, y + 20, 176, 0, menu.getScaledProgress(), 10);
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

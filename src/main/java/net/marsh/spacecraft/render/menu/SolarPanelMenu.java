package net.marsh.spacecraft.render.menu;

import net.marsh.spacecraft.block.ModBlocks;
import net.marsh.spacecraft.block.entity.SolarPanelBlockEntity;
import net.marsh.spacecraft.render.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;

@SuppressWarnings("ALL")
public class SolarPanelMenu extends AbstractMachineMenu {
    public final SolarPanelBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public SolarPanelMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, inv.player.level.getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(0)); //simpleContainerData matches the amount of data (progress and maxProgress = 2)
    }

    public SolarPanelMenu(int id, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.SOLAR_PANEL_MENU.get(), id, inv, entity, data, 1, ModBlocks.SOLAR_PANEL.get());
        blockEntity = (SolarPanelBlockEntity) entity;
        this.level = inv.player.level;
        this.data = data;

        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            this.addSlot(new SlotItemHandler(handler, 0, 8, 62));
        });

        addDataSlots(data);
    }

    public boolean isCollectingEnergy() {
        return level.isDay();
    }

    public SolarPanelBlockEntity getBlockEntity() {
        return this.blockEntity;
    }

    public int getFlameHeight() {
        int burnTime = data.get(0);
        return (int) Math.ceil((double) burnTime / 114.29);
    }
}

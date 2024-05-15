package net.marsh.spacecraft.render.menu;

import net.marsh.spacecraft.block.ModBlocks;
import net.marsh.spacecraft.block.entity.SteelFoundryBlockEntity;
import net.marsh.spacecraft.render.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;

@SuppressWarnings("ALL")
public class SteelFoundryMenu extends AbstractMachineMenu {
    public final SteelFoundryBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public SteelFoundryMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, inv.player.level.getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(4)); //simpleContainerData matches the amount of data (progress and maxProgress = 2)
    }

    public SteelFoundryMenu(int id, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.STEEL_FOUNDRY_MENU.get(), id, inv, entity, data, 4, ModBlocks.STEEL_FOUNDRY.get());
        blockEntity = (SteelFoundryBlockEntity) entity;
        this.level = inv.player.level;
        this.data = data;

        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            this.addSlot(new SlotItemHandler(handler, 0, 8, 62));
            this.addSlot(new SlotItemHandler(handler, 1, 68, 35));
            this.addSlot(new SlotItemHandler(handler, 2, 91, 35));
            this.addSlot(new SlotItemHandler(handler, 3, 152, 62));
        });

        addDataSlots(data);
    }

    public boolean isCrafting() {
        return data.get(0) > 0;
    }

    public boolean isCharging() {
        return data.get(2) > 0;
    }

    public boolean isCharged() {
        return data.get(2) == data.get(3);
    }

    public boolean hasIronAndCarbon() {
        return !this.slots.get(37).getItem().isEmpty() && !this.slots.get(38).getItem().isEmpty();
    }

    public SteelFoundryBlockEntity getBlockEntity() {
        return this.blockEntity;
    }

    public int getScaledCraftingProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        int progressArrowSize = 44; // This is the width in pixels

        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
    }

    public int getScaledChargingProgress() {
        int diodeProgress = this.data.get(2);
        int maxDiodeProgress = this.data.get(3);
        int progressArrowSize = 19; // This is the height in pixels

        return maxDiodeProgress != 0 && diodeProgress != 0 ? diodeProgress * progressArrowSize / maxDiodeProgress : 0;
    }
}

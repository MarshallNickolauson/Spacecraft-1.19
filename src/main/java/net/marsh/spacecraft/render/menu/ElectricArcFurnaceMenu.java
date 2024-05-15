package net.marsh.spacecraft.render.menu;

import net.marsh.spacecraft.block.ModBlocks;
import net.marsh.spacecraft.block.entity.ElectricArcFurnaceBlockEntity;
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
public class ElectricArcFurnaceMenu extends AbstractMachineMenu {
    public final ElectricArcFurnaceBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public ElectricArcFurnaceMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, inv.player.level.getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(2)); //simpleContainerData matches the amount of data (progress and maxProgress = 2)
    }

    public ElectricArcFurnaceMenu(int id, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.ELECTRIC_ARC_FURNACE_MENU.get(), id, inv, entity, data, 4, ModBlocks.ELECTRIC_ARC_FURNACE.get());
        blockEntity = (ElectricArcFurnaceBlockEntity) entity;
        this.level = inv.player.level;
        this.data = data;

        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            this.addSlot(new SlotItemHandler(handler, 0, 8, 62));
            this.addSlot(new SlotItemHandler(handler, 1, 44, 35));
            this.addSlot(new SlotItemHandler(handler, 2, 108, 35));
            this.addSlot(new SlotItemHandler(handler, 3, 134, 35));
        });

        addDataSlots(data);
    }

    public boolean isCrafting() {
        return data.get(0) > 0;
    }

    public ElectricArcFurnaceBlockEntity getBlockEntity() {
        return this.blockEntity;
    }

    public int getScaledProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        int progressArrowSize = 30; // This is the width in pixels of your arrow

        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
    }
}

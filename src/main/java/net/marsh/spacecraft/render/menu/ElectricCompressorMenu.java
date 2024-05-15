package net.marsh.spacecraft.render.menu;

import net.marsh.spacecraft.block.ModBlocks;
import net.marsh.spacecraft.block.entity.ElectricCompressorBlockEntity;
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
public class ElectricCompressorMenu extends AbstractMachineMenu {
    public final ElectricCompressorBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public ElectricCompressorMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, inv.player.level.getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(2)); //simpleContainerData matches the amount of data (progress and maxProgress = 2)
    }

    public ElectricCompressorMenu(int id, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.ELECTRIC_COMPRESSOR_MENU.get(), id, inv, entity, data, 12, ModBlocks.ELECTRIC_COMPRESSOR.get());
        blockEntity = (ElectricCompressorBlockEntity) entity;
        this.level = inv.player.level;
        this.data = data;

        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            this.addSlot(new SlotItemHandler(handler, 0, 8, 61));
            this.addSlot(new SlotItemHandler(handler, 1, 30, 17));
            this.addSlot(new SlotItemHandler(handler, 2, 48, 17));
            this.addSlot(new SlotItemHandler(handler, 3, 66, 17));
            this.addSlot(new SlotItemHandler(handler, 4, 30, 35));
            this.addSlot(new SlotItemHandler(handler, 5, 48, 35));
            this.addSlot(new SlotItemHandler(handler, 6, 66, 35));
            this.addSlot(new SlotItemHandler(handler, 7, 30, 53));
            this.addSlot(new SlotItemHandler(handler, 8, 48, 53));
            this.addSlot(new SlotItemHandler(handler, 9, 66, 53));
            this.addSlot(new SlotItemHandler(handler, 10, 148, 22));
            this.addSlot(new SlotItemHandler(handler, 11, 148, 48));
        });

        addDataSlots(data);
    }

    public boolean isCrafting() {
        return data.get(0) > 0;
    }

    public ElectricCompressorBlockEntity getBlockEntity() {
        return this.blockEntity;
    }

    public int getScaledProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        int progressArrowSize = 53; // This is the width in pixels of your arrow

        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
    }
}

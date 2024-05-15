package net.marsh.spacecraft.render.menu;

import net.marsh.spacecraft.block.ModBlocks;
import net.marsh.spacecraft.block.entity.CircuitFabricatorBlockEntity;
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
public class CircuitFabricatorMenu extends AbstractMachineMenu {
    public final CircuitFabricatorBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public CircuitFabricatorMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, inv.player.level.getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(2)); //simpleContainerData matches the amount of data (progress and maxProgress = 2)
    }

    public CircuitFabricatorMenu(int id, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.CIRCUIT_FABRICATOR_MENU.get(), id, inv, entity, data, 7, ModBlocks.CIRCUIT_FABRICATOR.get(), 110);
        blockEntity = (CircuitFabricatorBlockEntity) entity;
        this.level = inv.player.level;
        this.data = data;

        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            this.addSlot(new SlotItemHandler(handler, 0, 8, 72));
            this.addSlot(new SlotItemHandler(handler, 1, 40, 18));
            this.addSlot(new SlotItemHandler(handler, 2, 74, 46));
            this.addSlot(new SlotItemHandler(handler, 3, 74, 64));
            this.addSlot(new SlotItemHandler(handler, 4, 122, 46));
            this.addSlot(new SlotItemHandler(handler, 5, 145, 20));
            this.addSlot(new SlotItemHandler(handler, 6, 152, 86));
        });

        addDataSlots(data);
    }

    public boolean isCrafting() {
        return data.get(0) > 0;
    }

    public boolean hasDiamondAndSilicon() {
        return !this.slots.get(37).getItem().isEmpty() && !this.slots.get(38).getItem().isEmpty();
    }

    public boolean hasRedstoneDust() {
        return hasDiamondAndSilicon() && !this.slots.get(39).getItem().isEmpty() && !this.slots.get(40).getItem().isEmpty();
    }

    public boolean hasRedstoneTorch() {
        return hasRedstoneDust() && !this.slots.get(41).getItem().isEmpty();
    }

    public boolean hasEnergy() {
        return this.blockEntity.getEnergyStorage().getEnergyStored() > 0;
    }

    public CircuitFabricatorBlockEntity getBlockEntity() {
        return this.blockEntity;
    }

    public int getScaledProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        int progressBarSize = 51; // This is the width in pixels of the bar

        return maxProgress != 0 && progress != 0 ? progress * progressBarSize / maxProgress : 0;
    }
}

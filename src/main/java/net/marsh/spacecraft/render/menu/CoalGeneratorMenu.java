package net.marsh.spacecraft.render.menu;

import net.marsh.spacecraft.block.ModBlocks;
import net.marsh.spacecraft.block.entity.CoalGeneratorBlockEntity;
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
public class CoalGeneratorMenu extends AbstractMachineMenu {
    public final CoalGeneratorBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public CoalGeneratorMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, inv.player.level.getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(1)); //simpleContainerData matches the amount of data (progress and maxProgress = 2)
    }

    public CoalGeneratorMenu(int id, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.COAL_GENERATOR_MENU.get(), id, inv, entity, data, 2, ModBlocks.COAL_GENERATOR.get());
        blockEntity = (CoalGeneratorBlockEntity) entity;
        this.level = inv.player.level;
        this.data = data;

        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            this.addSlot(new SlotItemHandler(handler, 0, 80, 61));
            this.addSlot(new SlotItemHandler(handler, 1, 152, 61));
        });

        addDataSlots(data);
    }

    public boolean isBurningCoal() {
        return data.get(0) > 0;
    }

    public CoalGeneratorBlockEntity getBlockEntity() {
        return this.blockEntity;
    }

    public int getFlameHeight() {
        int burnTime = data.get(0);
        return (int) Math.ceil((double) burnTime / 114.29);
    }
}

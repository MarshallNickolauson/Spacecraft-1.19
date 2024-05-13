package net.marsh.spacecraft.block.entity;

import net.marsh.spacecraft.block.ModBlockEntities;
import net.marsh.spacecraft.block.custom.SolarPanelBlock;
import net.marsh.spacecraft.networking.ModMessages;
import net.marsh.spacecraft.networking.packet.SolarPanelEnergySyncS2CPacket;
import net.marsh.spacecraft.render.menu.SolarPanelMenu;
import net.marsh.spacecraft.util.ModBlockEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("ALL")
public class SolarPanelBlockEntity extends AbstractMachineBlockEntity {

    public SolarPanelBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SOLAR_PANEL.get(), pos, state);
    }

    @Override
    protected ContainerData createContainerData() {
        return new ContainerData() {
            @Override
            public int get(int index) {
                return 0;
            }

            @Override
            public void set(int index, int value) {
                return;
            }

            @Override
            public int getCount() {
                return 0;
            }
        };
    }

    @Override
    protected ItemStackHandler createItemHandler() {
        return new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return switch (slot) {
                    case 0 -> stack.getItem() == Items.DIAMOND;
                    default -> super.isItemValid(slot, stack);
                };
            }
        };
    }

    @Override
    protected int[] getSlotsForUp() {
        return null;
    }

    @Override
    protected int[] getSlotsForDown() {
        return null;
    }

    @Override
    protected int[] getSlotsForSides() {
        return null;
    }

    @Override
    protected ModBlockEnergyStorage createEnergyStorage() {
        return new ModBlockEnergyStorage(3000, 250) {
            @Override
            public void onEnergyChanged() {
                setChanged();
                ModMessages.sendToClients(new SolarPanelEnergySyncS2CPacket(this.energy, getBlockPos()));
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Solar Panel");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new SolarPanelMenu(id, inventory, this, this.data);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SolarPanelBlockEntity entity) {
        if (level.isClientSide()) {
            return;
        }

        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }


        if (level.isDay()) {
            level.setBlockAndUpdate(pos, state.setValue(SolarPanelBlock.LIT, true));
            setChanged(level, pos, state);
            if (entity.ENERGY_STORAGE.getEnergyStored() < entity.ENERGY_STORAGE.getMaxEnergyStored()) {
                entity.ENERGY_STORAGE.receiveEnergy(31, false);
                setChanged(level, pos, state);
            }
        }

        loadEnergyBar(entity, pos);

        if (!level.isDay()) {
            level.setBlockAndUpdate(pos, state.setValue(SolarPanelBlock.LIT, false));
            setChanged(level, pos, state);
        }

    }

    private static void loadEnergyBar(SolarPanelBlockEntity entity, BlockPos pos) {
        if (entity.ENERGY_STORAGE.getEnergyStored() == entity.ENERGY_STORAGE.getMaxEnergyStored()) {
            ModMessages.sendToClients(new SolarPanelEnergySyncS2CPacket(entity.ENERGY_STORAGE.getEnergyStored(), pos));
        }
    }
}

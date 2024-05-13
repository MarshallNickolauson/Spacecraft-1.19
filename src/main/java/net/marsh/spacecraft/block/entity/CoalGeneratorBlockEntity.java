package net.marsh.spacecraft.block.entity;

import net.marsh.spacecraft.block.ModBlockEntities;
import net.marsh.spacecraft.block.custom.CoalGeneratorBlock;
import net.marsh.spacecraft.item.ModItems;
import net.marsh.spacecraft.item.custom.BatteryItem;
import net.marsh.spacecraft.networking.ModMessages;
import net.marsh.spacecraft.networking.packet.CoalGeneratorEnergySyncS2CPacket;
import net.marsh.spacecraft.render.menu.CoalGeneratorMenu;
import net.marsh.spacecraft.util.ModBlockEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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
public class CoalGeneratorBlockEntity extends AbstractMachineBlockEntity {

    private int burnTime = 0;

    public CoalGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COAL_GENERATOR.get(), pos, state);
    }

    @Override
    protected ContainerData createContainerData() {
        return new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> CoalGeneratorBlockEntity.this.burnTime;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> CoalGeneratorBlockEntity.this.burnTime = value;
                };
            }

            @Override
            public int getCount() {
                return 1;
            }
        };
    }

    @Override
    protected ItemStackHandler createItemHandler() {
        return new ItemStackHandler(2) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return switch (slot) {
                    case 0 -> stack.getItem() == Items.COAL;
                    case 1 -> stack.getItem() == ModItems.BATTERY.get();
                    default -> super.isItemValid(slot, stack);
                };
            }
        };
    }

    @Override
    protected int[] getSlotsForUp() {
        return new int[]{0};
    }

    @Override
    protected int[] getSlotsForDown() {
        return null;
    }

    @Override
    protected int[] getSlotsForSides() {
        return new int[]{0};
    }

    @Override
    protected ModBlockEnergyStorage createEnergyStorage() {
        return new ModBlockEnergyStorage(2000, 250) {
            @Override
            public void onEnergyChanged() {
                setChanged();
                ModMessages.sendToClients(new CoalGeneratorEnergySyncS2CPacket(this.energy, getBlockPos()));
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Coal Generator");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new CoalGeneratorMenu(id, inventory, this, this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.putInt("coal_generator_burn_time", this.burnTime);
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        burnTime = nbt.getInt("coal_generator_burn_time");
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CoalGeneratorBlockEntity entity) {
        if (level.isClientSide()) {
            return;
        }

        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        ItemStack batteryStack = entity.itemHandler.getStackInSlot(1);
        if (!batteryStack.isEmpty() && batteryStack.getItem() instanceof BatteryItem batteryItem) {
            CompoundTag batteryTag = batteryStack.getOrCreateTag();
            int currentEnergy = batteryTag.getInt("battery_energy_amount");

            //System.out.println(currentEnergy);

            if (currentEnergy < batteryItem.getMaxEnergyStored() && entity.ENERGY_STORAGE.getEnergyStored() > 0) {
                batteryTag.putInt("battery_energy_amount", currentEnergy + 25);

                entity.ENERGY_STORAGE.extractEnergy(25, false);
            }

        }

        if (inventory.getItem(0).getItem() == Items.COAL && entity.burnTime == 0) {
            inventory.getItem(0).setCount((inventory.getItem(0).getCount()) - 1);
            entity.burnTime = 1600;
        }

        if (entity.burnTime > 0) {
            entity.burnTime--;
            level.setBlockAndUpdate(pos, state.setValue(CoalGeneratorBlock.LIT, true));
            setChanged(level, pos, state);
            if (entity.ENERGY_STORAGE.getEnergyStored() < entity.ENERGY_STORAGE.getMaxEnergyStored()) {
                entity.ENERGY_STORAGE.receiveEnergy(21, false);
                setChanged(level, pos, state);
            }
        }

        loadEnergyBar(entity, pos);

        if (entity.burnTime == 0) {
            level.setBlockAndUpdate(pos, state.setValue(CoalGeneratorBlock.LIT, false));
            setChanged(level, pos, state);
        }

    }

    private static void loadEnergyBar(CoalGeneratorBlockEntity entity, BlockPos pos) {
        if (entity.ENERGY_STORAGE.getEnergyStored() == entity.ENERGY_STORAGE.getMaxEnergyStored()) {
            ModMessages.sendToClients(new CoalGeneratorEnergySyncS2CPacket(entity.ENERGY_STORAGE.getEnergyStored(), pos));
        }
    }
}

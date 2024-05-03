package net.marsh.spacecraft.block.entity;

import net.marsh.spacecraft.block.custom.CoalGeneratorBlock;
import net.marsh.spacecraft.block.custom.SolarPanelBlock;
import net.marsh.spacecraft.networking.ModMessages;
import net.marsh.spacecraft.networking.packet.SolarPanelEnergySyncS2CPacket;
import net.marsh.spacecraft.screen.SolarPanelMenu;
import net.marsh.spacecraft.util.ModBlockEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class SolarPanelBlockEntity extends BlockEntity implements MenuProvider {

    private final ItemStackHandler itemHandler = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot) {
                //TODO change case 1 to a battery type later on. Make abstract class
                case 0 -> stack.getItem() == Items.DIAMOND;
                default -> super.isItemValid(slot, stack);
            };
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.empty();

    private final ModBlockEnergyStorage ENERGY_STORAGE;
    private Direction facing;
    private Direction energyOutputDirection1;
    private Direction energyOutputDirection2;
    protected final ContainerData data;

    public SolarPanelBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SOLAR_PANEL.get(), pos, state);
        this.facing = state.getValue(SolarPanelBlock.FACING);
        this.energyOutputDirection1 = state.getValue(SolarPanelBlock.ENERGY_OUTPUT_DIRECTION_1);
        this.energyOutputDirection2 = state.getValue(SolarPanelBlock.ENERGY_OUTPUT_DIRECTION_2);

        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return 0;
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                };
            }

            @Override
            public int getCount() {
                return 0;
            }
        };

        this.ENERGY_STORAGE = new ModBlockEnergyStorage(2000, 31) {
            @Override
            public void onEnergyChanged() {
                setChanged();
                ModMessages.sendToClients(new SolarPanelEnergySyncS2CPacket(this.energy, getBlockPos()));
            }

            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                if (!level.isDay()) {
                    return 0;
                }
                return super.receiveEnergy(maxReceive, simulate);
            }

            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
                if (SolarPanelBlockEntity.this.facing == SolarPanelBlockEntity.this.energyOutputDirection1 || SolarPanelBlockEntity.this.facing == SolarPanelBlockEntity.this.energyOutputDirection2) {
                    return super.extractEnergy(maxExtract, simulate);
                } else {
                    return 0;
                }
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

    public IEnergyStorage getEnergyStorage() {
        return ENERGY_STORAGE;
    }

    public void setEnergyLevel(int energy) {
        this.ENERGY_STORAGE.setEnergy(energy);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            BlockState blockState = getBlockState();
            Direction facingDirection = blockState.getValue(SolarPanelBlock.FACING);

            // Determine the direction based on the facing direction of the block
            Direction energyDirection = switch (facingDirection) {
                case NORTH -> Direction.SOUTH;
                case SOUTH -> Direction.NORTH;
                case WEST -> Direction.EAST;
                case EAST -> Direction.WEST;
                default -> Direction.EAST; // Default to EAST if facing direction is not recognized
            };

            if (side == energyDirection || side == Direction.DOWN) {
                return lazyEnergyHandler.cast();
            }
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
        lazyEnergyHandler = LazyOptional.of(() -> ENERGY_STORAGE);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
        lazyEnergyHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.put("inventory", itemHandler.serializeNBT());
        nbt.putInt("solar_panel_energy", ENERGY_STORAGE.getEnergyStored());

        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        ENERGY_STORAGE.setEnergy(nbt.getInt("solar_panel_energy"));
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
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

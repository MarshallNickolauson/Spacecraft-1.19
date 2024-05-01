package net.marsh.spacecraft.block.entity;

import net.marsh.spacecraft.block.custom.CoalGeneratorBlock;
import net.marsh.spacecraft.networking.ModMessages;
import net.marsh.spacecraft.networking.packet.CoalGeneratorEnergySyncS2CPacket;
import net.marsh.spacecraft.screen.CoalGeneratorMenu;
import net.marsh.spacecraft.util.ModEnergyStorage;
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

public class CoalGeneratorBlockEntity extends BlockEntity implements MenuProvider {

    private final ItemStackHandler itemHandler = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot) {
                case 0 -> stack.getItem() == Items.COAL;
                //TODO change case 1 to a battery type later on. Make abstract class
                case 1 -> stack.getItem() == Items.DIAMOND;
                default -> super.isItemValid(slot, stack);
            };
        }
    };

    private final ModEnergyStorage ENERGY_STORAGE = new ModEnergyStorage(2500, 100) {
        @Override
        public void onEnergyChanged() {
            setChanged();
            ModMessages.sendToClients(new CoalGeneratorEnergySyncS2CPacket(this.energy, getBlockPos()));
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            if (burnTime > 0) {
                return super.receiveEnergy(maxReceive, simulate);
            } else {
                return 0;
            }
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private final Map<Direction, LazyOptional<WrappedHandler>> directionWrappedHandlerMap =
            Map.of(
                    Direction.UP, LazyOptional.of(() -> new WrappedHandler(itemHandler, (index) -> index == 0, (index, stack) -> itemHandler.isItemValid(0, stack))),
                    Direction.DOWN, LazyOptional.of(() -> new WrappedHandler(itemHandler, (i) -> i == 1, (i, s) -> false)),
                    Direction.NORTH, LazyOptional.of(() -> new WrappedHandler(itemHandler, (index) -> index == 0, (index, stack) -> itemHandler.isItemValid(0, stack))),
                    Direction.SOUTH, LazyOptional.of(() -> new WrappedHandler(itemHandler, (i) -> i == 0, (i, s) -> false)),
                    Direction.EAST, LazyOptional.of(() -> new WrappedHandler(itemHandler, (i) -> i == 0, (i, s) -> false)),
                    Direction.WEST, LazyOptional.of(() -> new WrappedHandler(itemHandler, (index) -> index == 0, (index, stack) -> itemHandler.isItemValid(0, stack)))
                    );

    private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.empty();

    protected final ContainerData data;
    private int burnTime = 0;

    public CoalGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COAL_GENERATOR.get(), pos, state);
        this.data = new ContainerData() {
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
    public Component getDisplayName() {
        return Component.literal("Coal Generator");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new CoalGeneratorMenu(id, inventory, this, this.data);
    }

    public IEnergyStorage getEnergyStorage() {
        return ENERGY_STORAGE;
    }

    public void setEnergyLevel(int energy) {
        this.ENERGY_STORAGE.setEnergy(energy);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ENERGY && side == Direction.EAST) {
            return lazyEnergyHandler.cast();
        }

        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if(side == null) {
                return lazyItemHandler.cast();
            }

            if(directionWrappedHandlerMap.containsKey(side)) {
                Direction localDir = this.getBlockState().getValue(CoalGeneratorBlock.FACING);

                if(side == Direction.UP || side == Direction.DOWN) {
                    return directionWrappedHandlerMap.get(side).cast();
                }

                return switch (localDir) {
                    default -> directionWrappedHandlerMap.get(side.getOpposite()).cast();
                    case EAST -> directionWrappedHandlerMap.get(side.getClockWise()).cast();
                    case SOUTH -> directionWrappedHandlerMap.get(side).cast();
                    case WEST -> directionWrappedHandlerMap.get(side.getCounterClockWise()).cast();
                };
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
        nbt.putInt("coal_generator_burn_time", this.burnTime);
        nbt.putInt("coal_generator_energy", ENERGY_STORAGE.getEnergyStored());

        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        burnTime = nbt.getInt("coal_generator_burn_time");
        ENERGY_STORAGE.setEnergy(nbt.getInt("coal_generator_energy"));
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CoalGeneratorBlockEntity entity) {
        if (level.isClientSide()) {
            return;
        }

        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        if (inventory.getItem(0).getItem() == Items.COAL && entity.burnTime == 0) {
            inventory.getItem(0).setCount((inventory.getItem(0).getCount()) - 1);
            entity.burnTime = 1600; // Resets burn time
        }

        if (entity.burnTime > 0) {
            entity.burnTime--;
            state.setValue(CoalGeneratorBlock.LIT, true);
            level.setBlockAndUpdate(pos, state.setValue(CoalGeneratorBlock.LIT, true));
            setChanged(level, pos, state);
            if (entity.ENERGY_STORAGE.getEnergyStored() < entity.ENERGY_STORAGE.getMaxEnergyStored()) {
                entity.ENERGY_STORAGE.receiveEnergy(25, false);
                setChanged(level, pos, state);
            }
        }
        if (entity.ENERGY_STORAGE.getEnergyStored() == entity.ENERGY_STORAGE.getMaxEnergyStored()) {
            ModMessages.sendToClients(new CoalGeneratorEnergySyncS2CPacket(entity.ENERGY_STORAGE.getEnergyStored(), pos));
        }

        if (entity.burnTime == 0) {
            state.setValue(CoalGeneratorBlock.LIT, false);
            level.setBlockAndUpdate(pos, state.setValue(CoalGeneratorBlock.LIT, false));
            setChanged(level, pos, state);
        }

    }
}

package net.marsh.spacecraft.block.entity;

import net.marsh.spacecraft.block.ModBlockEntities;
import net.marsh.spacecraft.block.custom.CircuitFabricatorBlock;
import net.marsh.spacecraft.item.ModItems;
import net.marsh.spacecraft.networking.ModMessages;
import net.marsh.spacecraft.networking.packet.CircuitFabricatorEnergySyncS2CPacket;
import net.marsh.spacecraft.render.menu.CircuitFabricatorMenu;
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
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
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

import java.util.Optional;

public class CircuitFabricatorBlockEntity extends BlockEntity implements MenuProvider {

    private final ItemStackHandler itemHandler = new ItemStackHandler(7) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot) {
                case 0 -> stack.getItem() == Items.DIAMOND;
                case 1 -> stack.getItem() == Items.DIAMOND;
                case 2 -> stack.getItem() == ModItems.RAW_SILICON.get();
                case 3 -> stack.getItem() == ModItems.RAW_SILICON.get();
                case 4 -> stack.getItem() == Items.REDSTONE;
                case 5 -> stack.getItem() == Items.REDSTONE_TORCH;
                case 6 -> stack.getItem() == ModItems.BASIC_WAFER.get();
                default -> super.isItemValid(slot, stack);
            };
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.empty();

    private ModBlockEnergyStorage ENERGY_STORAGE;
    private static final int ENERGY_REQUIRED = 100;
    private final Direction facing;
    private final Direction energyInputDirection;
    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 200;

    public CircuitFabricatorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CIRCUIT_FABRICATOR.get(), pos, state);
        this.facing = state.getValue(CircuitFabricatorBlock.FACING);
        this.energyInputDirection = state.getValue(CircuitFabricatorBlock.ENERGY_INPUT_DIRECTION);

        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> CircuitFabricatorBlockEntity.this.progress;
                    case 1 -> CircuitFabricatorBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> CircuitFabricatorBlockEntity.this.progress = value;
                    case 1 -> CircuitFabricatorBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };

        this.ENERGY_STORAGE = new ModBlockEnergyStorage(10000, 1000) {

            @Override
            public void onEnergyChanged() {
                setChanged();
                ModMessages.sendToClients(new CircuitFabricatorEnergySyncS2CPacket(this.energy, getBlockPos()));
            }

            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                if (CircuitFabricatorBlockEntity.this.facing != CircuitFabricatorBlockEntity.this.energyInputDirection) {
                    return 0;
                }

                return super.receiveEnergy(maxExtract, simulate);
            }

            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
                if (!hasRecipeInProgress()) {
                    return 0;
                }

                return super.extractEnergy(maxExtract, simulate);
            }

            private boolean hasRecipeInProgress() {
                return hasRecipe(CircuitFabricatorBlockEntity.this) && progress < maxProgress;
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Circuit Fabricator");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new CircuitFabricatorMenu(id, inventory, this, this.data);
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
            Direction facingDirection = blockState.getValue(CircuitFabricatorBlock.FACING);

            // Determine the direction based on the facing direction of the block
            Direction energyDirection = switch (facingDirection) {
                case NORTH -> Direction.SOUTH;
                case SOUTH -> Direction.NORTH;
                case WEST -> Direction.EAST;
                case EAST -> Direction.WEST;
                default -> Direction.EAST; // Default to EAST if facing direction is not recognized
            };

            if (side == energyDirection) {
                return lazyEnergyHandler.cast();
            }
        }

        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
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
        nbt.putInt("circuit_fabricator_progress", this.progress);
        nbt.putInt("circuit_fabricator.energy", ENERGY_STORAGE.getEnergyStored());

        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        progress = nbt.getInt("circuit_fabricator_progress");
        ENERGY_STORAGE.setEnergy(nbt.getInt("circuit_fabricator.energy"));
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CircuitFabricatorBlockEntity entity) {
        if (level.isClientSide()) {
            return;
        }

        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        //TODO remove when battery is made
        if (entity.itemHandler.getStackInSlot(0).getItem() == Items.DIAMOND) {
            entity.ENERGY_STORAGE.receiveEnergy(1000, false);
        }

        if (hasRecipe(entity) && entity.ENERGY_STORAGE.getEnergyStored() > 0) {
            entity.progress++;
            entity.ENERGY_STORAGE.extractEnergy(ENERGY_REQUIRED, false);
            level.setBlockAndUpdate(pos, state.setValue(CircuitFabricatorBlock.LIT, true));
            setChanged(level, pos, state);

            if (entity.progress == entity.maxProgress) {
                craftItem(entity);
            }
        } else {
            entity.resetProgress();
            level.setBlockAndUpdate(pos, state.setValue(CircuitFabricatorBlock.LIT, false));
            setChanged(level, pos, state);
        }

        loadEnergyBar(entity, pos);

        if (entity.ENERGY_STORAGE.getEnergyStored() == 0) {
            entity.resetProgress();
            setChanged(level, pos, state);
        }
    }

    private static void loadEnergyBar(CircuitFabricatorBlockEntity entity, BlockPos pos) {
        if (entity.ENERGY_STORAGE.getEnergyStored() == entity.ENERGY_STORAGE.getMaxEnergyStored()) {
            ModMessages.sendToClients(new CircuitFabricatorEnergySyncS2CPacket(entity.ENERGY_STORAGE.getEnergyStored(), pos));
        }
    }

    private void resetProgress() {
        this.progress = 0;
    }

    private static void craftItem(CircuitFabricatorBlockEntity entity) {
        ItemStack inputStack = entity.itemHandler.getStackInSlot(1);
        Optional<SmeltingRecipe> recipe = entity.level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(inputStack), entity.level);

        if (hasRecipe(entity)) {
            entity.itemHandler.extractItem(1, 1, false);
            entity.itemHandler.extractItem(2, 1, false);
            entity.itemHandler.extractItem(3, 1, false);
            entity.itemHandler.extractItem(4, 1, false);
            entity.itemHandler.extractItem(5, 1, false);

            entity.itemHandler.setStackInSlot(6, new ItemStack(ModItems.BASIC_WAFER.get(),
                    entity.itemHandler.getStackInSlot(6).getCount() + 1));

            entity.resetProgress();
        }
    }

    private static boolean hasRecipe(CircuitFabricatorBlockEntity entity) {
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        return (
                (inventory.getItem(1).getItem() == Items.DIAMOND)
                && (inventory.getItem(2).getItem() == ModItems.RAW_SILICON.get())
                && (inventory.getItem(3).getItem() == ModItems.RAW_SILICON.get())
                && (inventory.getItem(4).getItem() == Items.REDSTONE)
                && (inventory.getItem(5).getItem() == Items.REDSTONE_TORCH)
                && canInsertItemIntoOutputSlot(inventory, ModItems.BASIC_WAFER.get().getDefaultInstance())
        );
    }

    private static boolean canInsertItemIntoOutputSlot(SimpleContainer inventory, ItemStack stack) {
        return(
                (inventory.getItem(6).getItem() == stack.getItem() || inventory.getItem(6).isEmpty()) &&
                (inventory.getItem(6).getMaxStackSize() > inventory.getItem(6).getCount())
        );
    }
}
















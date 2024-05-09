package net.marsh.spacecraft.block.entity;

import net.marsh.spacecraft.block.ModBlockEntities;
import net.marsh.spacecraft.block.WrappedHandler;
import net.marsh.spacecraft.block.custom.SteelFoundryBlock;
import net.marsh.spacecraft.item.ModItems;
import net.marsh.spacecraft.networking.ModMessages;
import net.marsh.spacecraft.networking.packet.SteelFoundryEnergySyncS2CPacket;
import net.marsh.spacecraft.render.menu.SteelFoundryMenu;
import net.marsh.spacecraft.sound.ModSounds;
import net.marsh.spacecraft.util.ModBlockEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
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

@SuppressWarnings("ALL")
public class SteelFoundryBlockEntity extends BlockEntity implements MenuProvider {

    private final ItemStackHandler itemHandler = new ItemStackHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot) {
                //TODO change case 1 to a battery type later on. Make abstract class
                case 0 -> stack.getItem() == Items.DIAMOND;
                case 1 -> stack.getItem() == Items.IRON_INGOT;
                case 2 -> stack.getItem() == ModItems.CARBON_FRAGMENTS.get();
                case 3 -> false;
                default -> super.isItemValid(slot, stack);
            };
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private final Map<Direction, LazyOptional<WrappedHandler>> directionWrappedHandlerMap =
            Map.of(
                    Direction.UP, LazyOptional.of(() -> new WrappedHandler(itemHandler, (index) -> index == 1, (index, stack) -> itemHandler.isItemValid(1, stack))),
                    Direction.DOWN, LazyOptional.of(() -> new WrappedHandler(itemHandler, (i) -> i == 2, (i, s) -> false)),
                    Direction.NORTH, LazyOptional.of(() -> new WrappedHandler(itemHandler, (index) -> index == 1, (index, stack) -> itemHandler.isItemValid(1, stack))),
                    Direction.SOUTH, LazyOptional.of(() -> new WrappedHandler(itemHandler, (i) -> i == 2, (i, s) -> false)),
                    Direction.WEST, LazyOptional.of(() -> new WrappedHandler(itemHandler, (i) -> i == 2, (i, s) -> false)),
                    Direction.EAST, LazyOptional.of(() -> new WrappedHandler(itemHandler, (index) -> index == 1, (index, stack) -> itemHandler.isItemValid(1, stack)))
            );

    private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.empty();

    private final ModBlockEnergyStorage ENERGY_STORAGE;
    private static final int ENERGY_REQUIRED = 51;
    private final Direction facing;
    private final Direction energyInputDirection;
    protected final ContainerData data;
    private int craftingProgress = 0;
    private int maxCraftingProgress = 100;
    private int diodeChargeProgress = 0;
    private int maxDiodeCharge = 100;
    private boolean isSoundOn = false;

    public SteelFoundryBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.STEEL_FOUNDRY.get(), pos, state);
        this.facing = state.getValue(SteelFoundryBlock.FACING);
        this.energyInputDirection = state.getValue(SteelFoundryBlock.ENERGY_INPUT_DIRECTION);

        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> SteelFoundryBlockEntity.this.craftingProgress;
                    case 1 -> SteelFoundryBlockEntity.this.maxCraftingProgress;
                    case 2 -> SteelFoundryBlockEntity.this.diodeChargeProgress;
                    case 3 -> SteelFoundryBlockEntity.this.maxDiodeCharge;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> SteelFoundryBlockEntity.this.craftingProgress = value;
                    case 1 -> SteelFoundryBlockEntity.this.maxCraftingProgress = value;
                    case 2 -> SteelFoundryBlockEntity.this.diodeChargeProgress = value;
                    case 3 -> SteelFoundryBlockEntity.this.maxDiodeCharge = value;
                }
            }

            @Override
            public int getCount() {
                return 4;
            }
        };

        this.ENERGY_STORAGE = new ModBlockEnergyStorage(3000, 250) {

            @Override
            public void onEnergyChanged() {
                setChanged();
                ModMessages.sendToClients(new SteelFoundryEnergySyncS2CPacket(this.energy, getBlockPos()));
            }

            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                if (SteelFoundryBlockEntity.this.facing != SteelFoundryBlockEntity.this.energyInputDirection) {
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
                return hasRecipe(SteelFoundryBlockEntity.this) && craftingProgress < maxCraftingProgress;
            }

        };
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Steel Foundry");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new SteelFoundryMenu(id, inventory, this, this.data);
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
            Direction facingDirection = blockState.getValue(SteelFoundryBlock.FACING);

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
            if (side == null) {
                return lazyItemHandler.cast();
            }

            if (directionWrappedHandlerMap.containsKey(side)) {
                Direction localDir = this.getBlockState().getValue(SteelFoundryBlock.FACING);

                if (side == Direction.UP || side == Direction.DOWN) {
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
        nbt.putInt("steel_foundry_progress", this.craftingProgress);
        nbt.putInt("steel_foundry.energy", ENERGY_STORAGE.getEnergyStored());

        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        craftingProgress = nbt.getInt("steel_foundry_progress");
        ENERGY_STORAGE.setEnergy(nbt.getInt("steel_foundry.energy"));
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SteelFoundryBlockEntity entity) {
        if (level.isClientSide()) {
            return;
        }

        //TODO remove when battery is made
        if (entity.itemHandler.getStackInSlot(0).getItem() == Items.DIAMOND) {
            entity.ENERGY_STORAGE.receiveEnergy(100, false);
        }

        if (entity.ENERGY_STORAGE.getEnergyStored() > 0 && entity.diodeChargeProgress < entity.maxDiodeCharge) {
            entity.diodeChargeProgress++;
            setChanged(level, pos, state);
        }

        //TODO needs to be a flag to only turn it on once. Make global variable

        if(entity.diodeChargeProgress > 0) {
            entity.ENERGY_STORAGE.extractEnergy(ENERGY_REQUIRED, false);
            setChanged(level, pos, state);
            if(!entity.isSoundOn) {
                level.playSound(null, pos, ModSounds.STEEL_FOUNDRY_SOUND.get(), SoundSource.RECORDS, 0.5f, 1.0f);
                level.setBlockAndUpdate(pos, state.setValue(SteelFoundryBlock.LIT, true));
                entity.isSoundOn = true;
            }
        } else {
            level.setBlockAndUpdate(pos, state.setValue(SteelFoundryBlock.LIT, false));
            level.playSound(null, pos, null, SoundSource.RECORDS, 0.0f, 0.0f);
            entity.isSoundOn = false;
            setChanged(level, pos, state);
        }

        if(entity.diodeChargeProgress >= entity.maxDiodeCharge) {

            if (hasRecipe(entity)) {
                entity.craftingProgress++;

                if (entity.craftingProgress >= entity.maxCraftingProgress) {
                    craftItem(entity);
                }
            } else {
                entity.resetCraftingProgress();
                setChanged(level, pos, state);
            }
        }

        loadEnergyBar(entity, pos);

        if (entity.ENERGY_STORAGE.getEnergyStored() == 0) {
            level.playSound(null, pos, null, SoundSource.BLOCKS, 0.0f, 0.0f);
            entity.resetCraftingProgress();
            entity.resetChargingProgress();
            setChanged(level, pos, state);
        }
    }

    private static void loadEnergyBar(SteelFoundryBlockEntity entity, BlockPos pos) {
        if (entity.ENERGY_STORAGE.getEnergyStored() == entity.ENERGY_STORAGE.getMaxEnergyStored()) {
            ModMessages.sendToClients(new SteelFoundryEnergySyncS2CPacket(entity.ENERGY_STORAGE.getEnergyStored(), pos));
        }
    }

    private void resetChargingProgress() {
        this.diodeChargeProgress = 0;
    }

    private void resetCraftingProgress() {
        this.craftingProgress = 0;
    }

    private static void craftItem(SteelFoundryBlockEntity entity) {
        if (hasRecipe(entity)) {
            entity.itemHandler.extractItem(1, 1, false);
            entity.itemHandler.extractItem(2, 1, false);
            entity.itemHandler.setStackInSlot(3, new ItemStack(ModItems.STEEL_INGOT.get(),
                    entity.itemHandler.getStackInSlot(3).getCount() + 1));

            entity.resetCraftingProgress();
        }
    }

    private static boolean hasRecipe(SteelFoundryBlockEntity entity) {
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        return (
                (inventory.getItem(1).getItem() == Items.IRON_INGOT)
                && (inventory.getItem(2).getItem() == ModItems.CARBON_FRAGMENTS.get())
        );
    }

    private static boolean canInsertItemIntoOutputSlot(SimpleContainer inventory, ItemStack stack) {
        return (inventory.getItem(3).getItem() == stack.getItem() || inventory.getItem(3).isEmpty())
                && (inventory.getItem(3).getMaxStackSize() > inventory.getItem(3).getCount());
    }
}
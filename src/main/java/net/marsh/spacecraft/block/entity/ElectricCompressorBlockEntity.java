package net.marsh.spacecraft.block.entity;

import net.marsh.spacecraft.block.ModBlockEntities;
import net.marsh.spacecraft.block.custom.ElectricCompressorBlock;
import net.marsh.spacecraft.networking.ModMessages;
import net.marsh.spacecraft.networking.packet.ElectricCompressorEnergySyncS2CPacket;
import net.marsh.spacecraft.recipe.ElectricCompressorRecipe;
import net.marsh.spacecraft.render.menu.ElectricCompressorMenu;
import net.marsh.spacecraft.util.ModBlockEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
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

import java.util.Optional;

@SuppressWarnings("ALL")
public class ElectricCompressorBlockEntity extends BlockEntity implements MenuProvider {

    private final ItemStackHandler itemHandler = new ItemStackHandler(12) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot) {
                //TODO change case 1 to a battery type later on. Make abstract class
                case 0 -> stack.getItem() == Items.DIAMOND;
                case 10 -> false; // Prevent item insert into output slot
                case 11 -> false; // Prevent item insert into output slot
                default -> super.isItemValid(slot, stack);
            };
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.empty();

    private final ModBlockEnergyStorage ENERGY_STORAGE;
    private static final int ENERGY_REQUIRED = 21;
    private final Direction facing;
    private final Direction energyInputDirection;
    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 100;

    public ElectricCompressorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ELECTRIC_COMPRESSOR.get(), pos, state);
        this.facing = state.getValue(ElectricCompressorBlock.FACING);
        this.energyInputDirection = state.getValue(ElectricCompressorBlock.ENERGY_INPUT_DIRECTION);

        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> ElectricCompressorBlockEntity.this.progress;
                    case 1 -> ElectricCompressorBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> ElectricCompressorBlockEntity.this.progress = value;
                    case 1 -> ElectricCompressorBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };

        this.ENERGY_STORAGE = new ModBlockEnergyStorage(1000, 100) {

            @Override
            public void onEnergyChanged() {
                setChanged();
                ModMessages.sendToClients(new ElectricCompressorEnergySyncS2CPacket(this.energy, getBlockPos()));
            }

            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                if (ElectricCompressorBlockEntity.this.facing != ElectricCompressorBlockEntity.this.energyInputDirection) {
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
                return hasRecipe(ElectricCompressorBlockEntity.this) && progress < maxProgress;
            }

        };
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Electric Compressor");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new ElectricCompressorMenu(id, inventory, this, this.data);
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
            Direction facingDirection = blockState.getValue(ElectricCompressorBlock.FACING);

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
        nbt.putInt("electric_compressor_progress", this.progress);
        nbt.putInt("electric_compressor.energy", ENERGY_STORAGE.getEnergyStored());

        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        progress = nbt.getInt("electric_compressor_progress");
        ENERGY_STORAGE.setEnergy(nbt.getInt("electric_compressor.energy"));
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ElectricCompressorBlockEntity entity) {
        if (level.isClientSide()) {
            return;
        }

        //TODO remove when battery is made
        if (entity.itemHandler.getStackInSlot(0).getItem() == Items.DIAMOND) {
            entity.ENERGY_STORAGE.receiveEnergy(50, false);
        }

        if (hasRecipe(entity)) {
            entity.progress++;
            entity.ENERGY_STORAGE.extractEnergy(ENERGY_REQUIRED, false);
            level.setBlockAndUpdate(pos, state.setValue(ElectricCompressorBlock.LIT, true));
            setChanged(level, pos, state);

            if (entity.progress >= entity.maxProgress) {
                craftItem(entity);
            }
        } else {
            entity.resetProgress();
            level.setBlockAndUpdate(pos, state.setValue(ElectricCompressorBlock.LIT, false));
            setChanged(level, pos, state);
        }

        loadEnergyBar(entity, pos);

        if (entity.ENERGY_STORAGE.getEnergyStored() == 0) {
            entity.resetProgress();
            setChanged(level, pos, state);
        }
    }

    private static void loadEnergyBar(ElectricCompressorBlockEntity entity, BlockPos pos) {
        if (entity.ENERGY_STORAGE.getEnergyStored() == entity.ENERGY_STORAGE.getMaxEnergyStored()) {
            ModMessages.sendToClients(new ElectricCompressorEnergySyncS2CPacket(entity.ENERGY_STORAGE.getEnergyStored(), pos));
        }
    }

    private void resetProgress() {
        this.progress = 0;
    }

    private static void craftItem(ElectricCompressorBlockEntity entity) {
        Level level = entity.level;
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        Optional<ElectricCompressorRecipe> recipe = level.getRecipeManager()
                .getRecipeFor(ElectricCompressorRecipe.Type.INSTANCE, inventory, level);

        if(hasRecipe(entity)) {
            for (int i = 1; i <= 9; i++) {
                entity.itemHandler.extractItem(i, 1, false);
            }

            entity.itemHandler.setStackInSlot(10, new ItemStack(recipe.get().getResultItem().getItem(),
                    entity.itemHandler.getStackInSlot(10).getCount() + 1));

            entity.itemHandler.setStackInSlot(11, new ItemStack(recipe.get().getResultItem().getItem(),
                    entity.itemHandler.getStackInSlot(11).getCount() + 1));

            BlockPos pos = entity.getBlockPos();
            level.playSound(null, pos, SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS, 0.5f, 1.0f);

            entity.resetProgress();
        }
    }

    private static boolean hasRecipe(ElectricCompressorBlockEntity entity) {
        Level level = entity.level;
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        Optional<ElectricCompressorRecipe> recipe = level.getRecipeManager()
                .getRecipeFor(ElectricCompressorRecipe.Type.INSTANCE, inventory, level);

        return recipe.isPresent() && canInsertItemIntoOutputSlot(inventory, recipe.get().getResultItem());
    }

    private static boolean canInsertItemIntoOutputSlot(SimpleContainer inventory, ItemStack stack) {
        return(
                (inventory.getItem(10).getItem() == stack.getItem() || inventory.getItem(10).isEmpty()) &&
                        (inventory.getItem(11).getItem() == stack.getItem() || inventory.getItem(11).isEmpty()) &&
                        (inventory.getItem(10).getMaxStackSize() > inventory.getItem(10).getCount()) &&
                        (inventory.getItem(11).getMaxStackSize() > inventory.getItem(11).getCount())
        );
    }
}
















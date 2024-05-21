package net.marsh.spacecraft.block.entity;

import net.marsh.spacecraft.block.NewWrappedHandler;
import net.marsh.spacecraft.block.custom.AbstractMachineBlock;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
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
public abstract class AbstractMachineBlockEntity extends BlockEntity implements MenuProvider {
    protected ModBlockEnergyStorage ENERGY_STORAGE;
    protected ItemStackHandler itemHandler;
    protected final ContainerData data;
    protected LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    protected LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.empty();

    private int lastEnergyStored;

    public AbstractMachineBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
        this.itemHandler = createItemHandler();
        this.ENERGY_STORAGE = createEnergyStorage();
        this.data = createContainerData();
        this.lastEnergyStored = 0;
    }

    protected abstract ContainerData createContainerData();

    protected abstract ItemStackHandler createItemHandler();

    protected abstract int[] getSlotsForUp();
    protected abstract int[] getSlotsForDown();
    protected abstract int[] getSlotsForSides();

    protected int[] getSlotsForFace(Direction pSide) {
        return switch (pSide) {
            case DOWN -> getSlotsForDown();
            case UP -> getSlotsForUp();
            default -> getSlotsForSides();
        };
    }

    private final Map<Direction, LazyOptional<NewWrappedHandler>> directionWrappedHandlerMap =
            Map.of( Direction.UP, LazyOptional.of(() -> new NewWrappedHandler(itemHandler, getSlotsForFace(Direction.UP))),
                    Direction.DOWN, LazyOptional.of(() -> new NewWrappedHandler(itemHandler, getSlotsForFace(Direction.DOWN))),
                    Direction.NORTH, LazyOptional.of(() -> new NewWrappedHandler(itemHandler, getSlotsForFace(Direction.NORTH))),
                    Direction.SOUTH, LazyOptional.of(() -> new NewWrappedHandler(itemHandler, getSlotsForFace(Direction.SOUTH))),
                    Direction.WEST, LazyOptional.of(() -> new NewWrappedHandler(itemHandler, getSlotsForFace(Direction.WEST))),
                    Direction.EAST, LazyOptional.of(() -> new NewWrappedHandler(itemHandler, getSlotsForFace(Direction.EAST))));

    protected abstract ModBlockEnergyStorage createEnergyStorage();

    @Override
    public abstract Component getDisplayName();

    @Nullable
    @Override
    public abstract AbstractContainerMenu createMenu(int i, Inventory inventory, Player player);

    public IEnergyStorage getEnergyStorage() { return ENERGY_STORAGE; }

    public void setEnergyLevel(int energy) { this.ENERGY_STORAGE.setEnergy(energy); }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY && side == getBlockState().getValue(AbstractMachineBlock.FACING).getOpposite()) {
            return lazyEnergyHandler.cast();
        }

        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if(side == null || !directionWrappedHandlerMap.containsKey(side)) {
                return lazyItemHandler.cast();
            }
            return directionWrappedHandlerMap.get(side).cast();
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
        nbt.putInt("energy", ENERGY_STORAGE.getEnergyStored());
        super.saveAdditional(nbt);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        ENERGY_STORAGE.setEnergy(nbt.getInt("energy"));
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        assert this.level != null;
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public int getLastEnergyStored() {
        return lastEnergyStored;
    }

    public void setLastEnergyStored(int energyStored) {
        this.lastEnergyStored = energyStored;
    }
}

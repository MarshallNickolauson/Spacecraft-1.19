package net.marsh.spacecraft.block.entity;

import net.marsh.spacecraft.block.ModBlockEntities;
import net.marsh.spacecraft.block.custom.ElectricFurnaceBlock;
import net.marsh.spacecraft.networking.ModMessages;
import net.marsh.spacecraft.networking.packet.ElectricFurnaceEnergySyncS2CPacket;
import net.marsh.spacecraft.render.menu.ElectricFurnaceMenu;
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
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@SuppressWarnings("ALL")
public class ElectricFurnaceBlockEntity extends AbstractMachineBlockEntity {

    private static final int ENERGY_REQUIRED = 10;
    private int progress = 0;
    private int maxProgress = 100;

    public ElectricFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ELECTRIC_FURNACE.get(), pos, state);
    }

    @Override
    protected ContainerData createContainerData() {
        return new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> ElectricFurnaceBlockEntity.this.progress;
                    case 1 -> ElectricFurnaceBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> ElectricFurnaceBlockEntity.this.progress = value;
                    case 1 -> ElectricFurnaceBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    protected ItemStackHandler createItemHandler() {
        return new ItemStackHandler(3) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return switch (slot) {
                    case 0 -> stack.getItem() == Items.DIAMOND;
                    case 1 -> true;
                    case 2 -> false;
                    default -> super.isItemValid(slot, stack);
                };
            }
        };
    }

    @Override
    protected int[] getSlotsForUp() {
        return new int[]{1};
    }

    @Override
    protected int[] getSlotsForDown() {
        return new int[]{2};
    }

    @Override
    protected int[] getSlotsForSides() {
        return new int[]{1};
    }


    @Override
    protected ModBlockEnergyStorage createEnergyStorage() {
        return new ModBlockEnergyStorage(1000, 100) {

            @Override
            public void onEnergyChanged() {
                setChanged();
                ModMessages.sendToClients(new ElectricFurnaceEnergySyncS2CPacket(this.energy, getBlockPos()));
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Electric Furnace");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new ElectricFurnaceMenu(id, inventory, this, this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.putInt("electric_furnace_progress", this.progress);
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        progress = nbt.getInt("electric_furnace_progress");
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ElectricFurnaceBlockEntity entity) {
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
            level.setBlockAndUpdate(pos, state.setValue(ElectricFurnaceBlock.LIT, true));
            setChanged(level, pos, state);

            if (entity.progress >= entity.maxProgress) {
                craftItem(entity);
            }
        } else {
            entity.resetProgress();
            level.setBlockAndUpdate(pos, state.setValue(ElectricFurnaceBlock.LIT, false));
            setChanged(level, pos, state);
        }

        loadEnergyBar(entity, pos);

        if (entity.ENERGY_STORAGE.getEnergyStored() == 0) {
            entity.resetProgress();
            setChanged(level, pos, state);
        }
    }

    private static void loadEnergyBar(ElectricFurnaceBlockEntity entity, BlockPos pos) {
        if (entity.ENERGY_STORAGE.getEnergyStored() == entity.ENERGY_STORAGE.getMaxEnergyStored()) {
            ModMessages.sendToClients(new ElectricFurnaceEnergySyncS2CPacket(entity.ENERGY_STORAGE.getEnergyStored(), pos));
        }
    }

    private void resetProgress() {
        this.progress = 0;
    }

    private static void craftItem(ElectricFurnaceBlockEntity entity) {
        ItemStack inputStack = entity.itemHandler.getStackInSlot(1);
        Optional<SmeltingRecipe> recipe = entity.level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(inputStack), entity.level);

        if (hasRecipe(entity)) {
            entity.itemHandler.extractItem(1, 1, false);
            entity.itemHandler.setStackInSlot(2, new ItemStack(recipe.get().getResultItem().getItem(),
                    entity.itemHandler.getStackInSlot(2).getCount() + 1));

            entity.resetProgress();
        }
    }

    private static boolean hasRecipe(ElectricFurnaceBlockEntity entity) {
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        Optional<SmeltingRecipe> recipe = entity.level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(entity.itemHandler.getStackInSlot(1)), entity.level);

        return recipe.isPresent() && canInsertItemIntoOutputSlot(inventory, recipe.get().getResultItem());
    }

    private static boolean canInsertItemIntoOutputSlot(SimpleContainer inventory, ItemStack stack) {
        return (inventory.getItem(2).getItem() == stack.getItem() || inventory.getItem(2).isEmpty())
                && (inventory.getItem(2).getMaxStackSize() > inventory.getItem(2).getCount());
    }
}
















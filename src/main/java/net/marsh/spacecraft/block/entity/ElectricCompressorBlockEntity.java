package net.marsh.spacecraft.block.entity;

import net.marsh.spacecraft.block.ModBlockEntities;
import net.marsh.spacecraft.block.custom.ElectricCompressorBlock;
import net.marsh.spacecraft.networking.ModMessages;
import net.marsh.spacecraft.networking.packet.ElectricCompressorEnergySyncS2CPacket;
import net.marsh.spacecraft.recipe.ElectricCompressorRecipe;
import net.marsh.spacecraft.render.menu.ElectricCompressorMenu;
import net.marsh.spacecraft.util.ModBlockEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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

import java.util.Optional;

@SuppressWarnings("ALL")
public class ElectricCompressorBlockEntity extends AbstractMachineBlockEntity {

    private static final int ENERGY_REQUIRED = 21;
    private int progress = 0;
    private int maxProgress = 100;

    public ElectricCompressorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ELECTRIC_COMPRESSOR.get(), pos, state);
    }

    @Override
    protected ContainerData createContainerData() {
        return new ContainerData() {
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
    }

    @Override
    protected ItemStackHandler createItemHandler() {
        return new ItemStackHandler(12) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return switch (slot) {
                    //TODO change case 1 to a battery type later on. Make abstract class
                    case 0 -> stack.getItem() == Items.DIAMOND;
                    case 10 -> false;
                    case 11 -> false;
                    default -> super.isItemValid(slot, stack);
                };
            }
        };
    }

    @Override
    protected int[] getSlotsForUp() {
        return new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
    }

    @Override
    protected int[] getSlotsForDown() {
        return new int[]{10, 11};
    }

    @Override
    protected int[] getSlotsForSides() {
        return new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
    }

    @Override
    protected ModBlockEnergyStorage createEnergyStorage() {
        return new ModBlockEnergyStorage(1000, 100) {
            @Override
            public void onEnergyChanged() {
                setChanged();
                ModMessages.sendToClients(new ElectricCompressorEnergySyncS2CPacket(this.energy, getBlockPos()));
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

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.putInt("electric_compressor_progress", this.progress);
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        progress = nbt.getInt("electric_compressor_progress");
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
















package net.marsh.spacecraft.block.entity;

import net.marsh.spacecraft.block.ModBlockEntities;
import net.marsh.spacecraft.block.custom.SteelFoundryBlock;
import net.marsh.spacecraft.item.ModItems;
import net.marsh.spacecraft.networking.ModMessages;
import net.marsh.spacecraft.networking.packet.SteelFoundryEnergySyncS2CPacket;
import net.marsh.spacecraft.render.menu.SteelFoundryMenu;
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

public class SteelFoundryBlockEntity extends AbstractMachineBlockEntity {

    private static final int ENERGY_REQUIRED = 51;
    private int craftingProgress = 0;
    private int maxCraftingProgress = 100;
    private int diodeChargeProgress = 0;
    private int maxDiodeCharge = 100;

    public SteelFoundryBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.STEEL_FOUNDRY.get(), pos, state);
    }

    @Override
    protected ContainerData createContainerData() {
        return new ContainerData() {
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
    }

    @Override
    protected ItemStackHandler createItemHandler() {
        return new ItemStackHandler(4) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return switch (slot) {
                    case 0 -> stack.getItem() == Items.DIAMOND;
                    case 1 -> stack.getItem() == Items.IRON_INGOT;
                    case 2 -> stack.getItem() == ModItems.CARBON_FRAGMENTS.get();
                    case 3 -> false;
                    default -> super.isItemValid(slot, stack);
                };
            }
        };
    }

    @Override
    protected int[] getSlotsForUp() {
        return new int[]{1, 2};
    }

    @Override
    protected int[] getSlotsForDown() {
        return new int[]{3};
    }

    @Override
    protected int[] getSlotsForSides() {
        return new int[]{1, 2};
    }

    @Override
    protected ModBlockEnergyStorage createEnergyStorage() {
        return new ModBlockEnergyStorage(3000, 250) {
            @Override
            public void onEnergyChanged() {
                setChanged();
                ModMessages.sendToClients(new SteelFoundryEnergySyncS2CPacket(this.energy, getBlockPos()));
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

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.putInt("steel_foundry_crafting_progress", this.craftingProgress);
        nbt.putInt("steel_foundry_diode_progress", this.diodeChargeProgress);
        super.saveAdditional(nbt);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        craftingProgress = nbt.getInt("steel_foundry_crafting_progress");
        diodeChargeProgress = nbt.getInt("steel_foundry_diode_progress");
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

        if(entity.diodeChargeProgress > 0) {
            entity.ENERGY_STORAGE.extractEnergy(ENERGY_REQUIRED, false);
            level.setBlockAndUpdate(pos, state.setValue(SteelFoundryBlock.LIT, true));
            setChanged(level, pos, state);
        } else {
            level.setBlockAndUpdate(pos, state.setValue(SteelFoundryBlock.LIT, false));
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
                && canInsertItemIntoOutputSlot(inventory, ModItems.STEEL_INGOT.get().getDefaultInstance())
        );
    }

    private static boolean canInsertItemIntoOutputSlot(SimpleContainer inventory, ItemStack stack) {
        return (inventory.getItem(3).getItem() == stack.getItem() || inventory.getItem(3).isEmpty())
                && (inventory.getItem(3).getMaxStackSize() > inventory.getItem(3).getCount());
    }
}
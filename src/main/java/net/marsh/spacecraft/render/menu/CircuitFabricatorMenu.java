package net.marsh.spacecraft.render.menu;

import net.marsh.spacecraft.block.ModBlocks;
import net.marsh.spacecraft.block.entity.CircuitFabricatorBlockEntity;
import net.marsh.spacecraft.item.ModItems;
import net.marsh.spacecraft.render.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;

public class CircuitFabricatorMenu extends AbstractContainerMenu {
    public final CircuitFabricatorBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public CircuitFabricatorMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, inv.player.level.getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(2)); //simpleContainerData matches the amount of data (progress and maxProgress = 2)
    }

    public CircuitFabricatorMenu(int id, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.CIRCUIT_FABRICATOR_MENU.get(), id);
        checkContainerSize(inv, 7);
        blockEntity = (CircuitFabricatorBlockEntity) entity;
        this.level = inv.player.level;
        this.data = data;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            this.addSlot(new SlotItemHandler(handler, 0, 8, 72));
            this.addSlot(new SlotItemHandler(handler, 1, 40, 18));
            this.addSlot(new SlotItemHandler(handler, 2, 74, 46));
            this.addSlot(new SlotItemHandler(handler, 3, 74, 64));
            this.addSlot(new SlotItemHandler(handler, 4, 122, 46));
            this.addSlot(new SlotItemHandler(handler, 5, 145, 20));
            this.addSlot(new SlotItemHandler(handler, 6, 152, 86));
        });

        addDataSlots(data);
    }

    public boolean isCrafting() {
        return data.get(0) > 0;
    }

    public boolean hasDiamondAndSilicon() {
//        Slot 36diamond
//        Slot 37diamond
//        Slot 38raw_silicon
//        Slot 39raw_silicon
//        Slot 40redstone
//        Slot 41redstone_torch
//        Slot 42air

        ItemStack slot1Stack = this.slots.get(37).getItem();
        ItemStack slot2Stack = this.slots.get(38).getItem();
        ItemStack slot3Stack = this.slots.get(39).getItem();

        boolean hasDiamond = !slot1Stack.isEmpty() && slot1Stack.getItem() == Items.DIAMOND;
        boolean hasSilicon = !slot2Stack.isEmpty() && slot2Stack.getItem() == ModItems.RAW_SILICON.get() &&
                !slot3Stack.isEmpty() && slot3Stack.getItem() == ModItems.RAW_SILICON.get();

        return hasDiamond && hasSilicon;
    }

    public boolean hasRedstoneDust() {
        if (hasDiamondAndSilicon()) {
            ItemStack slotStack = this.slots.get(40).getItem();
            return !slotStack.isEmpty() && slotStack.getItem() == Items.REDSTONE;
        }

        return false;
    }

    public boolean hasRedstoneTorch() {
        if (hasRedstoneDust()) {
            ItemStack slotStack = this.slots.get(41).getItem();
            return !slotStack.isEmpty() && slotStack.getItem() == Items.REDSTONE_TORCH;
        }

        return false;
    }

    public boolean hasEnergy() {
        return this.blockEntity.getEnergyStorage().getEnergyStored() > 0;
    }

    public CircuitFabricatorBlockEntity getBlockEntity() {
        return this.blockEntity;
    }

    public int getScaledProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        int progressBarSize = 51; // This is the width in pixels of the bar

        return maxProgress != 0 && progress != 0 ? progress * progressBarSize / maxProgress : 0;
    }


    // CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
    // must assign a slot number to each of the slots used by the GUI.
    // For this container, we can see both the tile inventory's slots as well as the player inventory slots and the hotbar.
    // Each time we add a Slot to the container, it automatically increases the slotIndex, which means
    //  0 - 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 - 8)
    //  9 - 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 - 35)
    //  36 - 44 = TileInventory slots, which map to our TileEntity slot numbers 0 - 8)
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    // THIS YOU HAVE TO DEFINE!
    private static final int TE_INVENTORY_SLOT_COUNT = 7;  // must be the number of slots you have!

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Check if the slot clicked is one of the vanilla container slots
        if (index < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                    + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        } else if (index < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + index);
            return ItemStack.EMPTY;
        }
        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, ModBlocks.CIRCUIT_FABRICATOR.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 110 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 168));
        }
    }
}

package net.marsh.spacecraft.render.menu;

import net.marsh.spacecraft.block.entity.AbstractMachineBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

@SuppressWarnings("ALL")
public abstract class AbstractMachineMenu extends AbstractContainerMenu {
    public final AbstractMachineBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;
    private final int inventorySlotCount;
    private final int screenInventoryYPixelStartPosition;
    private final Block block;
    private static final int DEFAULT_SCREEN_INVENTORY_Y_PIXEL_START_POSITION = 84;

    protected AbstractMachineMenu(
            MenuType<?> type,
            int id,
            Inventory inv,
            BlockEntity entity,
            ContainerData data,
            int inventorySlotCount,
            Block block
    ) {
        this(type, id, inv, entity, data, inventorySlotCount, block, DEFAULT_SCREEN_INVENTORY_Y_PIXEL_START_POSITION);
    }

    protected AbstractMachineMenu(
            MenuType<?> type,
            int id,
            Inventory inv,
            BlockEntity entity,
            ContainerData data,
            int inventorySlotCount,
            Block block,
            int screenInventoryYPixelStartPosition
    ) {

        super(type, id);
        checkContainerSize(inv, inventorySlotCount);
        this.blockEntity = (AbstractMachineBlockEntity) entity;
        this.level = inv.player.level;
        this.data = data;
        this.inventorySlotCount = inventorySlotCount;
        this.block = block;
        this.screenInventoryYPixelStartPosition = screenInventoryYPixelStartPosition;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, this.block);
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, this.screenInventoryYPixelStartPosition + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, this.screenInventoryYPixelStartPosition + 58));
        }
    }

    // CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
    private static final int VANILLA_SLOT_COUNT = 36;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        if (index < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                    + this.inventorySlotCount, false)) {
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        } else if (index < TE_INVENTORY_FIRST_SLOT_INDEX + this.inventorySlotCount) {
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + index);
            return ItemStack.EMPTY;
        }
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }
}

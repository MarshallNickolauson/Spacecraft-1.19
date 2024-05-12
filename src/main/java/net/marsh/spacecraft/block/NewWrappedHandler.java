package net.marsh.spacecraft.block;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/*
 * WrappedHandler by noeppi_noeppi
 * under https://github.com/ModdingX/LibX/blob/1.19/LICENSE
 *
 * Constructor modified by Marshall Nickolauson for simpler mapping
 *
 */
public class NewWrappedHandler implements IItemHandlerModifiable {
    private final IItemHandlerModifiable handler;
    private final Predicate<Integer> extract;
    private final BiPredicate<Integer, ItemStack> insert;

    public NewWrappedHandler(IItemHandlerModifiable itemHandler, int[] slotsForFace) {
        this.handler = itemHandler;
        this.extract = index -> {
            for (int slot : slotsForFace) {
                if (index == slot) {
                    return true;
                }
            }
            return false;
        };
        this.insert = (index, stack) -> {
            for (int slot : slotsForFace) {
                if (index == slot) {
                    return itemHandler.isItemValid(slot, stack);
                }
            }
            return false;
        };
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        this.handler.setStackInSlot(slot, stack);
    }

    @Override
    public int getSlots() {
        return this.handler.getSlots();
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.handler.getStackInSlot(slot);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        return this.insert.test(slot, stack) ? this.handler.insertItem(slot, stack, simulate) : stack;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return this.extract.test(slot) ? this.handler.extractItem(slot, amount, simulate) : ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {
        return this.handler.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return this.insert.test(slot, stack) && this.handler.isItemValid(slot, stack);
    }
}
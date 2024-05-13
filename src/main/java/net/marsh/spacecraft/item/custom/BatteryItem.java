package net.marsh.spacecraft.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BatteryItem extends Item implements INBTSerializable<Tag> {
    public int energy;
    protected final int capacity;
    protected final int maxReceive;
    protected final int maxExtract;

    public BatteryItem(Properties properties, int capacity, int maxTransfer) {
        super(properties.stacksTo(1).durability(capacity));
        this.capacity = capacity;
        this.maxReceive = maxTransfer;
        this.maxExtract = maxTransfer;
        this.energy = 0;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltips, TooltipFlag context) {
        int energy = Math.min(this.energy, this.capacity); // Ensure energy doesn't exceed capacity
        tooltips.add(Component.translatable("tooltip.spacecraft.energy_remaining", Component.literal(String.valueOf(energy)).withStyle(ChatFormatting.GREEN)));
        super.appendHoverText(stack, level, tooltips, context);
    }

    @Override
    public boolean isBarVisible(ItemStack pStack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack pStack) {
        int maxWidth = 13; // Maximum width of the progress bar
        double energyRatio = (double) this.getEnergyStored() / (double) this.getMaxEnergyStored();
        int width = (int) Math.round(energyRatio * maxWidth);
        return Math.min(maxWidth, width);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        double scale = 1.0 - Math.max(0.0, (double) this.getEnergyStored() / (double) this.getMaxEnergyStored());
        return ((int)(255 * scale) << 16) + (((int)(255 * ( 1.0 - scale))) << 8);
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return true;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        // Calculate the remaining durability based on energy stored
        return this.capacity - this.energy;
    }

    @Override
    public int getDamage(ItemStack stack) {
        // Calculate the damage value based on energy stored
        return this.capacity - this.energy;
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        // Calculate the energy based on the provided damage
        this.energy = this.capacity - damage;
    }

    @Override
    public int getEnchantmentValue() {
        return -1;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairMaterial) {
        return false;
    }

    public int getEnergyStored() {
        return this.energy;
    }

    public int getMaxEnergyStored() {
        return this.capacity;
    }

    public boolean canExtract() {
        return this.maxExtract > 0;
    }

    public boolean canReceive() {
        return this.energy < this.capacity;
    }

    @Override
    public Tag serializeNBT() {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putInt("battery_energy_amount", this.energy);
        return compoundTag;
    }

    @Override
    public void deserializeNBT(Tag tag) {
        if (tag instanceof CompoundTag) {
            CompoundTag compoundTag = (CompoundTag) tag;
            this.energy = compoundTag.getInt("battery_energy_amount");
        }
    }
}

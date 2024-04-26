package net.marsh.spacecraft.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ModCreativeModTab {
    public static final CreativeModeTab SPACECRAFT_TAB = new CreativeModeTab("spacecraft_tab") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(Items.IRON_INGOT);
        }
    };
}

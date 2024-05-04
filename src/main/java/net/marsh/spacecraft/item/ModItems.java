package net.marsh.spacecraft.item;

import net.marsh.spacecraft.Spacecraft;
import net.marsh.spacecraft.item.custom.BatteryItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Spacecraft.MOD_ID);

    public static final RegistryObject<Item> CARBON_FRAGMENTS = ITEMS.register("carbon_fragments", () -> new Item(new Item.Properties().tab(ModCreativeModTab.SPACECRAFT_TAB)));
    public static final RegistryObject<Item> RAW_ALUMINUM = ITEMS.register("raw_aluminum", () -> new Item(new Item.Properties().tab(ModCreativeModTab.SPACECRAFT_TAB)));
    public static final RegistryObject<Item> RAW_LEAD = ITEMS.register("raw_lead", () -> new Item(new Item.Properties().tab(ModCreativeModTab.SPACECRAFT_TAB)));
    public static final RegistryObject<Item> RAW_METEORIC_IRON = ITEMS.register("raw_meteoric_iron", () -> new Item(new Item.Properties().tab(ModCreativeModTab.SPACECRAFT_TAB)));
    public static final RegistryObject<Item> RAW_PALLADIUM = ITEMS.register("raw_palladium", () -> new Item(new Item.Properties().tab(ModCreativeModTab.SPACECRAFT_TAB)));
    public static final RegistryObject<Item> RAW_SILICON = ITEMS.register("raw_silicon", () -> new Item(new Item.Properties().tab(ModCreativeModTab.SPACECRAFT_TAB)));
    public static final RegistryObject<Item> RAW_TIN = ITEMS.register("raw_tin", () -> new Item(new Item.Properties().tab(ModCreativeModTab.SPACECRAFT_TAB)));
    public static final RegistryObject<Item> RAW_TITANIUM = ITEMS.register("raw_titanium", () -> new Item(new Item.Properties().tab(ModCreativeModTab.SPACECRAFT_TAB)));
    public static final RegistryObject<Item> RAW_URANIUM = ITEMS.register("raw_uranium", () -> new Item(new Item.Properties().tab(ModCreativeModTab.SPACECRAFT_TAB)));

    public static final RegistryObject<Item> ALUMINUM_INGOT = ITEMS.register("aluminum_ingot", () -> new Item(new Item.Properties().tab(ModCreativeModTab.SPACECRAFT_TAB)));
    public static final RegistryObject<Item> LEAD_INGOT = ITEMS.register("lead_ingot", () -> new Item(new Item.Properties().tab(ModCreativeModTab.SPACECRAFT_TAB)));
    public static final RegistryObject<Item> STEEL_INGOT = ITEMS.register("steel_ingot", () -> new Item(new Item.Properties().tab(ModCreativeModTab.SPACECRAFT_TAB)));
    public static final RegistryObject<Item> TIN_INGOT = ITEMS.register("tin_ingot", () -> new Item(new Item.Properties().tab(ModCreativeModTab.SPACECRAFT_TAB)));
    public static final RegistryObject<Item> TITANIUM_INGOT = ITEMS.register("titanium_ingot", () -> new Item(new Item.Properties().tab(ModCreativeModTab.SPACECRAFT_TAB)));
    public static final RegistryObject<Item> URANIUM_INGOT = ITEMS.register("uranium_ingot", () -> new Item(new Item.Properties().tab(ModCreativeModTab.SPACECRAFT_TAB)));
    public static final RegistryObject<Item> PALLADIUM_INGOT = ITEMS.register("palladium_ingot", () -> new Item(new Item.Properties().tab(ModCreativeModTab.SPACECRAFT_TAB)));
    public static final RegistryObject<Item> METEORIC_IRON_INGOT = ITEMS.register("meteoric_iron_ingot", () -> new Item(new Item.Properties().tab(ModCreativeModTab.SPACECRAFT_TAB)));

    public static final RegistryObject<Item> COMPRESSED_IRON = ITEMS.register("compressed_iron", () -> new Item(new Item.Properties().tab(ModCreativeModTab.SPACECRAFT_TAB)));
    public static final RegistryObject<Item> COMPRESSED_ALUMINUM = ITEMS.register("compressed_aluminum", () -> new Item(new Item.Properties().tab(ModCreativeModTab.SPACECRAFT_TAB)));
    public static final RegistryObject<Item> COMPRESSED_BRONZE = ITEMS.register("compressed_bronze", () -> new Item(new Item.Properties().tab(ModCreativeModTab.SPACECRAFT_TAB)));
    public static final RegistryObject<Item> COMPRESSED_COPPER = ITEMS.register("compressed_copper", () -> new Item(new Item.Properties().tab(ModCreativeModTab.SPACECRAFT_TAB)));
    public static final RegistryObject<Item> COMPRESSED_METEORIC_IRON = ITEMS.register("compressed_meteoric_iron", () -> new Item(new Item.Properties().tab(ModCreativeModTab.SPACECRAFT_TAB)));
    public static final RegistryObject<Item> COMPRESSED_PALLADIUM = ITEMS.register("compressed_palladium", () -> new Item(new Item.Properties().tab(ModCreativeModTab.SPACECRAFT_TAB)));
    public static final RegistryObject<Item> COMPRESSED_STEEL = ITEMS.register("compressed_steel", () -> new Item(new Item.Properties().tab(ModCreativeModTab.SPACECRAFT_TAB)));
    public static final RegistryObject<Item> COMPRESSED_TIN = ITEMS.register("compressed_tin", () -> new Item(new Item.Properties().tab(ModCreativeModTab.SPACECRAFT_TAB)));
    public static final RegistryObject<Item> COMPRESSED_TITANIUM = ITEMS.register("compressed_titanium", () -> new Item(new Item.Properties().tab(ModCreativeModTab.SPACECRAFT_TAB)));
    public static final RegistryObject<Item> COMPRESSED_LEAD = ITEMS.register("compressed_lead", () -> new Item(new Item.Properties().tab(ModCreativeModTab.SPACECRAFT_TAB)));

    public static final RegistryObject<Item> BATTERY = ITEMS.register("battery", () -> new BatteryItem(new Item.Properties().tab(ModCreativeModTab.SPACECRAFT_TAB), 10000, 100));

    public static void register(IEventBus eventBus) { ITEMS.register(eventBus); }
}

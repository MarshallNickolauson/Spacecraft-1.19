package net.marsh.spacecraft.render;

import net.marsh.spacecraft.Spacecraft;
import net.marsh.spacecraft.render.menu.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Spacecraft.MOD_ID);

    public static final RegistryObject<MenuType<CoalGeneratorMenu>> COAL_GENERATOR_MENU = registerMenuType(CoalGeneratorMenu::new, "coal_generator_menu");
    public static final RegistryObject<MenuType<ElectricFurnaceMenu>> ELECTRIC_FURNACE_MENU = registerMenuType(ElectricFurnaceMenu::new, "electric_furnace_menu");
    public static final RegistryObject<MenuType<ElectricArcFurnaceMenu>> ELECTRIC_ARC_FURNACE_MENU = registerMenuType(ElectricArcFurnaceMenu::new, "electric_arc_furnace_menu");
    public static final RegistryObject<MenuType<SolarPanelMenu>> SOLAR_PANEL_MENU = registerMenuType(SolarPanelMenu::new, "solar_panel_menu");
    public static final RegistryObject<MenuType<CircuitFabricatorMenu>> CIRCUIT_FABRICATOR_MENU = registerMenuType(CircuitFabricatorMenu::new, "circuit_fabricator_menu");
    public static final RegistryObject<MenuType<ElectricCompressorMenu>> ELECTRIC_COMPRESSOR_MENU = registerMenuType(ElectricCompressorMenu::new, "electric_compressor_menu");

    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenuType(IContainerFactory<T> factory, String name) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }
    public static void register(IEventBus eventBus) { MENUS.register(eventBus); }
}

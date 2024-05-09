package net.marsh.spacecraft;

import com.mojang.logging.LogUtils;
import net.marsh.spacecraft.block.ModBlocks;
import net.marsh.spacecraft.block.ModBlockEntities;
import net.marsh.spacecraft.networking.ModMessages;
import net.marsh.spacecraft.recipe.ModRecipes;
import net.marsh.spacecraft.render.*;
import net.marsh.spacecraft.item.ModItems;
import net.marsh.spacecraft.render.menu.CircuitFabricatorMenu;
import net.marsh.spacecraft.render.screen.*;
import net.marsh.spacecraft.world.feature.ModConfiguredFeatures;
import net.marsh.spacecraft.world.feature.ModPlacedFeatures;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(Spacecraft.MOD_ID)
public class Spacecraft {
    public static final String MOD_ID = "spacecraft";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Spacecraft() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);

        ModConfiguredFeatures.register(modEventBus);
        ModPlacedFeatures.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModRecipes.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        ModMessages.register();
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            MenuScreens.register(ModMenuTypes.COAL_GENERATOR_MENU.get(), CoalGeneratorScreen::new);
            MenuScreens.register(ModMenuTypes.ELECTRIC_FURNACE_MENU.get(), ElectricFurnaceScreen::new);
            MenuScreens.register(ModMenuTypes.ELECTRIC_ARC_FURNACE_MENU.get(), ElectricArcFurnaceScreen::new);
            MenuScreens.register(ModMenuTypes.SOLAR_PANEL_MENU.get(), SolarPanelScreen::new);
            MenuScreens.register(ModMenuTypes.CIRCUIT_FABRICATOR_MENU.get(), CircuitFabricatorScreen::new);
            MenuScreens.register(ModMenuTypes.ELECTRIC_COMPRESSOR_MENU.get(), ElectricCompressorScreen::new);
            MenuScreens.register(ModMenuTypes.STEEL_FOUNDRY_MENU.get(), SteelFoundryScreen::new);
        }
    }
}

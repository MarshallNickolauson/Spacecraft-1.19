package net.marsh.spacecraft;

import com.mojang.logging.LogUtils;
import net.marsh.spacecraft.block.ModBlocks;
import net.marsh.spacecraft.block.entity.ModBlockEntities;
import net.marsh.spacecraft.screen.CoalGeneratorScreen;
import net.marsh.spacecraft.screen.ModMenuTypes;
import net.marsh.spacecraft.item.ModItems;
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

        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            MenuScreens.register(ModMenuTypes.COAL_GENERATOR_MENU.get(), CoalGeneratorScreen::new);
        }
    }
}

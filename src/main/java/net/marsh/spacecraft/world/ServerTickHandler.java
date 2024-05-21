package net.marsh.spacecraft.world;

import net.marsh.spacecraft.Spacecraft;
import net.marsh.spacecraft.block.networked.EnergyNetworkManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Spacecraft.MOD_ID, value = Dist.DEDICATED_SERVER)
public class ServerTickHandler {

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            EnergyNetworkManager.INSTANCE.tickNetworks(event.getServer().overworld());
            EnergyNetworkManager.INSTANCE.printAllNetworkDistributions();
        }
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        // Clear all networks when the server starts
        EnergyNetworkManager.INSTANCE.clearAllNetworks();
    }
}
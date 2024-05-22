package net.marsh.spacecraft.block.networked;

import net.minecraft.world.level.Level;

import java.util.HashSet;
import java.util.Set;

public class EnergyNetworkManager {
    public static final EnergyNetworkManager INSTANCE = new EnergyNetworkManager();
    private final Set<EnergyNetwork> networks = new HashSet<>();

    public void registerNetwork(EnergyNetwork network) {
        networks.add(network);
    }

    public void unregisterNetwork(EnergyNetwork network) {
        networks.remove(network);
    }

    public void clearAllNetworks() {
        networks.clear();
    }

    public void tickNetworks(Level level) {
        for (EnergyNetwork network : networks) {
            network.updateNetwork();
            network.distributeEnergy();
        }
    }

    public void printAllNetworkDistributions() {
        System.out.println(networks.size());
//        for (EnergyNetwork network : networks) {
//            System.out.println("-----");
//            network.printNetworkContents();
//        }
    }
}

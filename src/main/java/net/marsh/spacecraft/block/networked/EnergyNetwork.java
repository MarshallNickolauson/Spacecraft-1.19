package net.marsh.spacecraft.block.networked;

import net.marsh.spacecraft.block.custom.AbstractMachineBlock;
import net.marsh.spacecraft.block.entity.AbstractMachineBlockEntity;
import net.marsh.spacecraft.block.entity.WireBlockEntity;
import net.marsh.spacecraft.util.WireConnectionType;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class EnergyNetwork {
    private final Set<WireBlockEntity> wires = new HashSet<>();
    private final Set<AbstractMachineBlockEntity> generators = new HashSet<>();
    private final Set<AbstractMachineBlockEntity> machines = new HashSet<>();

    public void addWire(WireBlockEntity wire) {
        wires.add(wire);
        updateNetwork();
    }

    public void removeWire(WireBlockEntity wire) {
        wires.remove(wire);
        updateNetwork(); //TODO this call might be unnecessary
    }

    public void clear() {
        wires.clear();
        generators.clear();
        machines.clear();
    }

    public void printNetworkContents() {
        System.out.println("wires: " + wires.size());
        System.out.println("machines: " + machines.size());
        System.out.println("generators: " + generators.size());
    }

    public void updateNetwork() {
        Queue<BlockEntity> toVisit = new LinkedList<>();
        Set<BlockEntity> visited = new HashSet<>();

        for (WireBlockEntity wire : wires) {
            toVisit.add(wire);
            visited.add(wire);
        }

        while (!toVisit.isEmpty()) {
            BlockEntity current = toVisit.poll();
            if (current instanceof WireBlockEntity) {
                wires.add((WireBlockEntity) current);
            } else if (current instanceof AbstractMachineBlockEntity) {
                AbstractMachineBlockEntity machineBlockEntity = (AbstractMachineBlockEntity) current;
                if (current.getBlockState().getValue(AbstractMachineBlock.ENERGY_CONNECTION_TYPE) == WireConnectionType.ENERGY_OUTPUT) {
                    generators.add(machineBlockEntity);
                } else if (current.getBlockState().getValue(AbstractMachineBlock.ENERGY_CONNECTION_TYPE) == WireConnectionType.ENERGY_INPUT) {
                    machines.add(machineBlockEntity);
                }
            }

            for (Direction direction : Direction.values()) {
                BlockEntity adjacentEntity = current.getLevel().getBlockEntity(current.getBlockPos().relative(direction));
                if (adjacentEntity != null && !visited.contains(adjacentEntity)) {
                    if (adjacentEntity.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite()).isPresent()) {
                        toVisit.add(adjacentEntity);
                        visited.add(adjacentEntity);
                    }
                }
            }
        }
    }

    public void distributeEnergy() {

        //TODO redo the logic of this if needed

//        int totalEnergy = 0;
//        for (AbstractMachineBlockEntity generator : generators) {
//            IEnergyStorage energyStorage = null;
//
//            for (Direction direction : Direction.values()) {
//                LazyOptional<IEnergyStorage> energyOpt = generator.getCapability(ForgeCapabilities.ENERGY, direction);
//                if (energyOpt.isPresent()) {
//                    energyStorage = energyOpt.orElse(null);
//                    break;
//                }
//            }
//
//            if (energyStorage != null) {
//                totalEnergy += energyStorage.getEnergyStored();
//            }
//        }
//
//        Set<AbstractMachineBlockEntity> machinesThatCanReceive = new HashSet<>();
//        for (AbstractMachineBlockEntity machine : machines) {
//            IEnergyStorage energyStorage = null;
//
//            for (Direction direction : Direction.values()) {
//                LazyOptional<IEnergyStorage> energyOpt = machine.getCapability(ForgeCapabilities.ENERGY, direction);
//                if (energyOpt.isPresent()) {
//                    energyStorage = energyOpt.orElse(null);
//                    break;
//                }
//            }
//
//            if (energyStorage != null && energyStorage.canReceive()) {
//                machinesThatCanReceive.add(machine);
//            }
//        }
//
//        int machineCount = machinesThatCanReceive.size();
//
//        int energyPerMachine = 0;
//        if (machineCount != 0) {
//            energyPerMachine = totalEnergy / machineCount;
//        }
//
//        for (AbstractMachineBlockEntity machine : machinesThatCanReceive) {
//            IEnergyStorage energyStorage = null;
//
//            for (Direction direction : Direction.values()) {
//                LazyOptional<IEnergyStorage> energyOpt = machine.getCapability(ForgeCapabilities.ENERGY, direction);
//                if (energyOpt.isPresent()) {
//                    energyStorage = energyOpt.orElse(null);
//                    break;
//                }
//            }
//
//            if (energyStorage != null) {
//                energyStorage.receiveEnergy(energyPerMachine, false);
//            }
//        }
//
//        int extractPerGenerator = energyPerMachine * machineCount;
//        for (AbstractMachineBlockEntity generator : generators) {
//            IEnergyStorage energyStorage = null;
//
//            for (Direction direction : Direction.values()) {
//                LazyOptional<IEnergyStorage> energyOpt = generator.getCapability(ForgeCapabilities.ENERGY, direction);
//                if (energyOpt.isPresent()) {
//                    energyStorage = energyOpt.orElse(null);
//                    break;
//                }
//            }
//
//            if (energyStorage != null) {
//                totalEnergy += energyStorage.extractEnergy(extractPerGenerator, false);
//            }
//        }
    }

    //TODO build merge of networks
    public void merge(EnergyNetwork otherNetwork) {
        this.wires.addAll(otherNetwork.wires);
        this.generators.addAll(otherNetwork.generators);
        this.machines.addAll(otherNetwork.machines);
        otherNetwork.clear();
        EnergyNetworkManager.INSTANCE.unregisterNetwork(otherNetwork);
        updateNetwork();
    }

    //TODO build split of networks

    public boolean isEmpty() {
        return wires.isEmpty() && generators.isEmpty() && machines.isEmpty();
    }
}

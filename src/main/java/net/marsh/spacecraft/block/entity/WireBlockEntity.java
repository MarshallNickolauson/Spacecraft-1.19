package net.marsh.spacecraft.block.entity;

import net.marsh.spacecraft.block.ModBlockEntities;
import net.marsh.spacecraft.block.networked.EnergyNetwork;
import net.marsh.spacecraft.block.networked.EnergyNetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("ALL")
public class WireBlockEntity extends BlockEntity {

    private LazyOptional<IEnergyStorage> energyStorage = LazyOptional.empty();
    private EnergyNetwork energyNetwork = null;

    public WireBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.WIRE_BLOCK.get(), pPos, pBlockState);
    }

    public void updateConnection() {
        if (level == null || level.isClientSide()) return;

        // Find adjacent wire block entities
        Set<WireBlockEntity> adjacentWireEntities = new HashSet<>();
        for (Direction direction : Direction.values()) {
            BlockEntity adjacentEntity = level.getBlockEntity(worldPosition.relative(direction));
            if (adjacentEntity instanceof WireBlockEntity) {
                adjacentWireEntities.add((WireBlockEntity) adjacentEntity);
            }
        }

        if (adjacentWireEntities.size() > 0) {
            // Check if any adjacent wire entity belongs to a network
            EnergyNetwork existingNetwork = null;
            for (WireBlockEntity adjacentWireEntity : adjacentWireEntities) {
                if (adjacentWireEntity.getEnergyNetwork() != null) {
                    existingNetwork = adjacentWireEntity.getEnergyNetwork();
                    break;
                }
            }

            if (existingNetwork != null) {
                this.energyNetwork = existingNetwork;
                energyNetwork.addWire(this);
            } else {
                System.out.println("updateConnection method in wireblockentity not working!");
            }

            // Merge adjacent networks
//            for (WireBlockEntity adjacentWireEntity : adjacentWireEntities) {
//                if (adjacentWireEntity.getEnergyNetwork() != this.energyNetwork) {
//                    this.energyNetwork.merge(adjacentWireEntity.getEnergyNetwork());
//                }
//            }

        }

        if (this.energyNetwork == null) {
            this.energyNetwork = new EnergyNetwork();
            EnergyNetworkManager.INSTANCE.registerNetwork(this.energyNetwork);
            energyNetwork.addWire(this);
        } else {
            System.out.println("Nothing was done!");
        }
    }

    public EnergyNetwork getEnergyNetwork() {
        return energyNetwork;
    }

    public void remove() {
        if (energyNetwork != null) {
            System.out.println("removing wire...");
            energyNetwork.removeWire(this);
            System.out.println("wires left: " + energyNetwork.getWires().size());
            if (energyNetwork.getWires().size() == 0) {
                energyNetwork.clear();
                EnergyNetworkManager.INSTANCE.unregisterNetwork(this.energyNetwork);
            }
        }
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, WireBlockEntity wireBlockEntity) {
        if (level.isClientSide()) {
            wireBlockEntity.updateConnection();
        }
    }
}

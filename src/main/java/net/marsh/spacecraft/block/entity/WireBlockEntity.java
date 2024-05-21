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
    private EnergyNetwork energyNetwork;

    public WireBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.WIRE_BLOCK.get(), pPos, pBlockState);
        this.energyNetwork = null;
    }

    public void updateConnection() {
        if (level == null || level.isClientSide()) return;

        System.out.println("got here");

        // Find adjacent wire block entities
        Set<WireBlockEntity> adjacentWireEntities = new HashSet<>();
        for (Direction direction : Direction.values()) {
            BlockEntity adjacentEntity = level.getBlockEntity(worldPosition.relative(direction));
            if (adjacentEntity instanceof WireBlockEntity) {
                adjacentWireEntities.add((WireBlockEntity) adjacentEntity);
            }
        }

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
        } else {
            this.energyNetwork = new EnergyNetwork();
            EnergyNetworkManager.INSTANCE.registerNetwork(this.energyNetwork);
        }

        energyNetwork.clear();
        energyNetwork.addWire(this);

        // Merge adjacent networks
        for (WireBlockEntity adjacentWireEntity : adjacentWireEntities) {
            if (adjacentWireEntity.getEnergyNetwork() != this.energyNetwork) {
                this.energyNetwork.merge(adjacentWireEntity.getEnergyNetwork());
            }
        }
    }

    public EnergyNetwork getEnergyNetwork() {
        return energyNetwork;
    }

    public void remove() {
        if (energyNetwork != null) {
            energyNetwork.removeWire(this);
            if (energyNetwork.isEmpty()) {
                EnergyNetworkManager.INSTANCE.unregisterNetwork(energyNetwork);
            }
        }
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, WireBlockEntity wireBlockEntity) {
        if (level.isClientSide()) {
            return;
        }
    }
}

package net.marsh.spacecraft.block.entity;

import net.marsh.spacecraft.block.ModBlockEntities;
import net.marsh.spacecraft.block.custom.CoalGeneratorBlock;
import net.marsh.spacecraft.util.WireConnectionType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.energy.IEnergyStorage;

@SuppressWarnings("ALL")
public class WireBlockEntity extends BlockEntity {
    private WireConnectionType energyConnectionType = WireConnectionType.WIRE;

    public WireBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.WIRE_BLOCK.get(), pPos, pBlockState);
    }

    public WireConnectionType getEnergyConnectionType() {
        return energyConnectionType;
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.energyConnectionType = WireConnectionType.valueOf(nbt.getString("energy_connection_type"));
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putString("energy_connection_type", energyConnectionType.name());
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, WireBlockEntity wireBlockEntity) {
        if (level.isClientSide()) {
            return;
        }

        for (Direction direction : Direction.values()) {

            BlockPos neighborPos = blockPos.relative(direction);
            BlockState neighborState = level.getBlockState(neighborPos);

            if (neighborState.hasProperty(CoalGeneratorBlock.ENERGY_CONNECTION_TYPE) && neighborState.getValue(CoalGeneratorBlock.ENERGY_CONNECTION_TYPE) == WireConnectionType.ENERGY_OUTPUT) {
                System.out.println(direction);

                if (level.getBlockEntity(neighborPos) instanceof CoalGeneratorBlockEntity neighborBlockEntity) {
                    System.out.println("True 2");

                    IEnergyStorage energyStorage = neighborBlockEntity.getEnergyStorage();
                    if (energyStorage.getEnergyStored() > 0) {
                        System.out.println("True 3");
                    }
                }
            }
        }
    }
}

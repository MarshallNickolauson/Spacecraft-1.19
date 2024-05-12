package net.marsh.spacecraft.block.custom;

import net.marsh.spacecraft.block.ModBlockEntities;
import net.marsh.spacecraft.block.entity.WireBlockEntity;
import net.marsh.spacecraft.util.WireConnectionType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("ALL")
public class WireBlock extends BaseEntityBlock {
    public static final EnumProperty<WireConnectionType> ENERGY_CONNECTION_TYPE = EnumProperty.create("energy_connection_type", WireConnectionType.class);

    public WireBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(ENERGY_CONNECTION_TYPE, WireConnectionType.WIRE)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(ENERGY_CONNECTION_TYPE);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new WireBlockEntity(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.WIRE_BLOCK.get(), WireBlockEntity::tick);
    }
}

package net.marsh.spacecraft.block.custom;

import net.marsh.spacecraft.block.entity.CoalGeneratorBlockEntity;
import net.marsh.spacecraft.util.WireConnectionType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;

@SuppressWarnings("ALL")
public abstract class AbstractMachineBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = BooleanProperty.create("lit");
    public static final DirectionProperty ENERGY_FLOW_DIRECTION = DirectionProperty.create("energy_flow", Direction.values());
    public static final EnumProperty<WireConnectionType> ENERGY_CONNECTION_TYPE = EnumProperty.create("energy_connection_type", WireConnectionType.class);

    protected AbstractMachineBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRot) {
        return pState.setValue(FACING, pRot.rotate(pState.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, LIT, ENERGY_FLOW_DIRECTION, ENERGY_CONNECTION_TYPE);
    }

    /* BLOCK ENTITY BELOW */

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);

            //TODO change this to be abstract later
            if (blockEntity instanceof CoalGeneratorBlockEntity) {
                ((CoalGeneratorBlockEntity) blockEntity).drops();
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }
}

package net.marsh.spacecraft.block.custom;

import net.marsh.spacecraft.block.ModBlockEntities;
import net.marsh.spacecraft.block.entity.SteelFoundryBlockEntity;
import net.marsh.spacecraft.sound.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("ALL")
public class SteelFoundryBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = BooleanProperty.create("lit");
    public static final DirectionProperty ENERGY_INPUT_DIRECTION = DirectionProperty.create("energy_input", Direction.values());

    public SteelFoundryBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(LIT, false)
                .setValue(ENERGY_INPUT_DIRECTION, Direction.NORTH)
        );
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Direction facing = pContext.getHorizontalDirection().getOpposite();
        return this.defaultBlockState()
                .setValue(FACING, facing)
                .setValue(ENERGY_INPUT_DIRECTION, facing);
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
        pBuilder.add(FACING, LIT, ENERGY_INPUT_DIRECTION);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable BlockGetter pLevel, List<Component> components, TooltipFlag pFlag) {
        components.add(Component.literal("Melts iron and coal fragments into steel using electric arc diodes.").withStyle(ChatFormatting.WHITE));
        components.add(Component.literal("Consumes 51FE/tick").withStyle(ChatFormatting.RED));
        super.appendHoverText(pStack, pLevel, components, pFlag);
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
            if (blockEntity instanceof SteelFoundryBlockEntity) {
                ((SteelFoundryBlockEntity) blockEntity).drops();
                pLevel.playSound(null, pPos, null, SoundSource.RECORDS, 0.0f, 0.0f);
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide()) {
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if(entity instanceof SteelFoundryBlockEntity) {
                NetworkHooks.openScreen(((ServerPlayer)pPlayer), (SteelFoundryBlockEntity)entity, pPos);
            } else {
                throw new IllegalStateException("Our Container provider is missing!");
            }
        }

        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SteelFoundryBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.STEEL_FOUNDRY.get(), SteelFoundryBlockEntity::tick);
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        if ((Boolean) pState.getValue(LIT)) {
            double xPos = (double)pPos.getX() + 0.5;
            double yPos = (double)pPos.getY();
            double zPos = (double)pPos.getZ() + 0.5;
            if (pRandom.nextDouble() < 0.1) {
                pLevel.playLocalSound(xPos, yPos, zPos, ModSounds.STEEL_FOUNDRY_SOUND.get(), SoundSource.BLOCKS, 0.4F, 1.0F, false);
            }

            Direction facing = (Direction)pState.getValue(FACING);
            Direction.Axis $$8 = facing.getAxis();
            double randomFloat = pRandom.nextDouble() * 0.6 - 0.3;
            double xAxis = $$8 == Direction.Axis.X ? (double)facing.getStepX() * 0.52 : randomFloat;
            double yAxis = pRandom.nextDouble() * 6.0 / 16.0;
            double zAxis = $$8 == Direction.Axis.Z ? (double)facing.getStepZ() * 0.52 : randomFloat;
            pLevel.addParticle(ParticleTypes.SMOKE, xPos + xAxis, yPos + yAxis, zPos + zAxis, 0.0, 0.0, 0.0);
            pLevel.addParticle(ParticleTypes.LAVA, xPos + xAxis, yPos + yAxis, zPos + zAxis, 0.0, 0.0, 0.0);
        }
    }
}

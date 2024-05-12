package net.marsh.spacecraft.block.custom;

import net.marsh.spacecraft.block.ModBlockEntities;
import net.marsh.spacecraft.block.entity.CoalGeneratorBlockEntity;
import net.marsh.spacecraft.util.WireConnectionType;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("ALL")
public class CoalGeneratorBlock extends AbstractMachineBlock {

    public CoalGeneratorBlock() {
        super(WireConnectionType.ENERGY_OUTPUT);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable BlockGetter pLevel, List<Component> components, TooltipFlag pFlag) {
        components.add(Component.literal("Generates energy by burning coal.").withStyle(ChatFormatting.WHITE));
        components.add(Component.literal("Generates 21FE/tick").withStyle(ChatFormatting.GREEN));
        super.appendHoverText(pStack, pLevel, components, pFlag);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        //TODO can this be abstract too?
        return new CoalGeneratorBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        //TODO make this method abstract to override
        return createTickerHelper(type, ModBlockEntities.COAL_GENERATOR.get(), CoalGeneratorBlockEntity::tick);
    }
}

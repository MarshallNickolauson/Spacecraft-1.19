package net.marsh.spacecraft.networking.packet;

import net.marsh.spacecraft.block.entity.CoalGeneratorBlockEntity;
import net.marsh.spacecraft.screen.CoalGeneratorMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CoalGeneratorEnergySyncS2CPacket {
    private final int energy;
    private final BlockPos pos;

    public CoalGeneratorEnergySyncS2CPacket(int energy, BlockPos pos) {
        this.energy = energy;
        this.pos = pos;
    }

    public CoalGeneratorEnergySyncS2CPacket(FriendlyByteBuf buf) {
        this.energy = buf.readInt();
        this.pos = buf.readBlockPos();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(energy);
        buf.writeBlockPos(pos);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {

            if(Minecraft.getInstance().level.getBlockEntity(pos) instanceof CoalGeneratorBlockEntity blockEntity) {
                blockEntity.setEnergyLevel(energy);

                if(Minecraft.getInstance().player.containerMenu instanceof CoalGeneratorMenu menu &&
                        menu.getBlockEntity().getBlockPos().equals(pos)) {
                    blockEntity.setEnergyLevel(energy);

                }
            }
        });
        return true;
    }
}

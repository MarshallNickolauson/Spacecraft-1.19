package net.marsh.spacecraft.networking.packet;

import net.marsh.spacecraft.block.entity.ElectricCompressorBlockEntity;
import net.marsh.spacecraft.render.menu.ElectricCompressorMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@SuppressWarnings("ALL")
public class ElectricCompressorEnergySyncS2CPacket {
    private final int energy;
    private final BlockPos pos;

    public ElectricCompressorEnergySyncS2CPacket(int energy, BlockPos pos) {
        this.energy = energy;
        this.pos = pos;
    }

    public ElectricCompressorEnergySyncS2CPacket(FriendlyByteBuf buf) {
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

            if(Minecraft.getInstance().level.getBlockEntity(pos) instanceof ElectricCompressorBlockEntity blockEntity) {
                blockEntity.setEnergyLevel(energy);

                if(Minecraft.getInstance().player.containerMenu instanceof ElectricCompressorMenu menu &&
                        menu.getBlockEntity().getBlockPos().equals(pos)) {
                    blockEntity.setEnergyLevel(energy);

                }
            }
        });
        return true;
    }
}

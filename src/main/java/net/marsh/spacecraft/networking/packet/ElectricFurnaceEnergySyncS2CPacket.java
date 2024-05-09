package net.marsh.spacecraft.networking.packet;

import net.marsh.spacecraft.block.entity.ElectricFurnaceBlockEntity;
import net.marsh.spacecraft.render.menu.ElectricFurnaceMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@SuppressWarnings("ALL")
public class ElectricFurnaceEnergySyncS2CPacket {
    private final int energy;
    private final BlockPos pos;

    public ElectricFurnaceEnergySyncS2CPacket(int energy, BlockPos pos) {
        this.energy = energy;
        this.pos = pos;
    }

    public ElectricFurnaceEnergySyncS2CPacket(FriendlyByteBuf buf) {
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

            if(Minecraft.getInstance().level.getBlockEntity(pos) instanceof ElectricFurnaceBlockEntity blockEntity) {
                blockEntity.setEnergyLevel(energy);

                if(Minecraft.getInstance().player.containerMenu instanceof ElectricFurnaceMenu menu &&
                        menu.getBlockEntity().getBlockPos().equals(pos)) {
                    blockEntity.setEnergyLevel(energy);

                }
            }
        });
        return true;
    }
}

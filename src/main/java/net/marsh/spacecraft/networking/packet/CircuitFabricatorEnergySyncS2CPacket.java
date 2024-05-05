package net.marsh.spacecraft.networking.packet;

import net.marsh.spacecraft.block.entity.CircuitFabricatorBlockEntity;
import net.marsh.spacecraft.block.entity.ElectricFurnaceBlockEntity;
import net.marsh.spacecraft.render.menu.CircuitFabricatorMenu;
import net.marsh.spacecraft.render.menu.ElectricFurnaceMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CircuitFabricatorEnergySyncS2CPacket {
    private final int energy;
    private final BlockPos pos;

    public CircuitFabricatorEnergySyncS2CPacket(int energy, BlockPos pos) {
        this.energy = energy;
        this.pos = pos;
    }

    public CircuitFabricatorEnergySyncS2CPacket(FriendlyByteBuf buf) {
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

            if(Minecraft.getInstance().level.getBlockEntity(pos) instanceof CircuitFabricatorBlockEntity blockEntity) {
                blockEntity.setEnergyLevel(energy);

                if(Minecraft.getInstance().player.containerMenu instanceof CircuitFabricatorMenu menu &&
                        menu.getBlockEntity().getBlockPos().equals(pos)) {
                    blockEntity.setEnergyLevel(energy);

                }
            }
        });
        return true;
    }
}

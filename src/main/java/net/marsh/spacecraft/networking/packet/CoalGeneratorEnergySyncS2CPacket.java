package net.marsh.spacecraft.networking.packet;

import net.marsh.spacecraft.block.entity.CoalGeneratorBlockEntity;
import net.marsh.spacecraft.block.entity.ModBlockEntities;
import net.marsh.spacecraft.screen.CoalGeneratorMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
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

        System.out.println("handle function" + energy);

        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            System.out.println("Reaching level 0");

            System.out.println(Minecraft.getInstance().level.getBlockEntity(pos, ModBlockEntities.COAL_GENERATOR.get()));
            System.out.println(pos);

            if (Minecraft.getInstance().level == null || Minecraft.getInstance().level.getBlockEntity(pos) == null) {
                System.out.println("Block entity is not loaded yet");
                return; // Exit the method if the block entity is not loaded
            }

            if(Minecraft.getInstance().level.getBlockEntity(pos) instanceof CoalGeneratorBlockEntity blockEntity) {
                blockEntity.setEnergyLevel(energy);

                System.out.println("Reaching level 1");

                System.out.println(Minecraft.getInstance().player.containerMenu.getType());

                if(Minecraft.getInstance().player.containerMenu instanceof CoalGeneratorMenu menu &&
                        menu.getBlockEntity().getBlockPos().equals(pos)) {
                    blockEntity.setEnergyLevel(energy);

                    System.out.println("Reaching level 2");
                }
            }
        });
        return true;
    }
}

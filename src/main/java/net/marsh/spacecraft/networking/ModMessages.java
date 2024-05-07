package net.marsh.spacecraft.networking;

import net.marsh.spacecraft.Spacecraft;
import net.marsh.spacecraft.networking.packet.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModMessages {
    private static SimpleChannel INSTANCE;

    private static int packetId = 0;
    private static int id() {
        return packetId++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(Spacecraft.MOD_ID, "mod_messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        net.messageBuilder(CoalGeneratorEnergySyncS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(CoalGeneratorEnergySyncS2CPacket::new)
                .encoder(CoalGeneratorEnergySyncS2CPacket::toBytes)
                .consumerMainThread(CoalGeneratorEnergySyncS2CPacket::handle)
                .add();

        net.messageBuilder(ElectricFurnaceEnergySyncS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ElectricFurnaceEnergySyncS2CPacket::new)
                .encoder(ElectricFurnaceEnergySyncS2CPacket::toBytes)
                .consumerMainThread(ElectricFurnaceEnergySyncS2CPacket::handle)
                .add();

        net.messageBuilder(ElectricArcFurnaceEnergySyncS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ElectricArcFurnaceEnergySyncS2CPacket::new)
                .encoder(ElectricArcFurnaceEnergySyncS2CPacket::toBytes)
                .consumerMainThread(ElectricArcFurnaceEnergySyncS2CPacket::handle)
                .add();

        net.messageBuilder(SolarPanelEnergySyncS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SolarPanelEnergySyncS2CPacket::new)
                .encoder(SolarPanelEnergySyncS2CPacket::toBytes)
                .consumerMainThread(SolarPanelEnergySyncS2CPacket::handle)
                .add();

        net.messageBuilder(CircuitFabricatorEnergySyncS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(CircuitFabricatorEnergySyncS2CPacket::new)
                .encoder(CircuitFabricatorEnergySyncS2CPacket::toBytes)
                .consumerMainThread(CircuitFabricatorEnergySyncS2CPacket::handle)
                .add();

        net.messageBuilder(ElectricCompressorEnergySyncS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ElectricCompressorEnergySyncS2CPacket::new)
                .encoder(ElectricCompressorEnergySyncS2CPacket::toBytes)
                .consumerMainThread(ElectricCompressorEnergySyncS2CPacket::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static <MSG> void sendToClients(MSG message) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }
}

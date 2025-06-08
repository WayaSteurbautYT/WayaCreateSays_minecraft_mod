package com.wayacreate.wayacreatesays.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;

public class ClientNetworking {

    public static void sendFunctionCommand(String functionPath) {
        PacketByteBuf buf = PacketByteBufs.create();
        ExecuteFunctionPacket packet = new ExecuteFunctionPacket(functionPath);
        packet.write(buf);
        ClientPlayNetworking.send(ExecuteFunctionPacket.PACKET_ID, buf);
    }

    public static void sendStartAutoSpeedrunPacket() {
        ClientPlayNetworking.send(StartAutoSpeedrunPacket.PACKET_ID, PacketByteBufs.empty());
    }

    public static void sendStopAutoSpeedrunPacket() {
        ClientPlayNetworking.send(StopAutoSpeedrunPacket.PACKET_ID, PacketByteBufs.empty());
    }

    public static void sendAttackDragonPacket() {
        ClientPlayNetworking.send(AttackDragonPacket.PACKET_ID, PacketByteBufs.empty());
    }
}

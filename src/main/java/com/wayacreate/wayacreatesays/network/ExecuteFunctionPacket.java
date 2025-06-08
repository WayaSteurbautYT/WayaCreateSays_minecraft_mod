package com.wayacreate.wayacreatesays.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class ExecuteFunctionPacket {
    public static final Identifier PACKET_ID = new Identifier("wayacreatesays", "execute_function");

    private final String functionPath;

    public ExecuteFunctionPacket(String functionPath) {
        this.functionPath = functionPath;
    }

    public ExecuteFunctionPacket(PacketByteBuf buf) {
        this.functionPath = buf.readString();
    }

    public void write(PacketByteBuf buf) {
        buf.writeString(this.functionPath);
    }

    public String getFunctionPath() {
        return functionPath;
    }
}

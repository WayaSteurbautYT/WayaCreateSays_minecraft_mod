package com.wayacreate.wayacreatesays.network;

import com.wayacreate.wayacreatesays.WayaCreateSaysMod;
import net.minecraft.util.Identifier;

public class StartAutoSpeedrunPacket {
    public static final Identifier PACKET_ID = new Identifier(WayaCreateSaysMod.MOD_ID, "start_auto_speedrun");

    // This packet needs no fields as its reception is the signal.
    // A constructor or write/read methods for an empty buffer are not strictly necessary
    // when using PacketByteBufs.empty() for sending and just checking the ID on receive.
}

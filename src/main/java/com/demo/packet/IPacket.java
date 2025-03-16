package com.demo.packet;

import java.nio.ByteOrder;

public interface IPacket {

    byte[] createPacket(ByteOrder byteOrder);
}

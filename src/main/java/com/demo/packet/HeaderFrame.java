package com.demo.packet;

import com.demo.util.TypeHelper;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@Slf4j
@Getter
@Setter
@ToString
@AllArgsConstructor
public class HeaderFrame implements IPacket{

    public static final int FRAME_SIZE = 10;

    private int stx;
    private int opCode;
    private int id;
    private int dataLength;

    public static final int STX = 0x02;

    public HeaderFrame(int stx, byte[] headerArray, ByteOrder byteOrder) {

        if (headerArray == null || headerArray.length < FRAME_SIZE) {
           log.error("Invalid header array: insufficient data.");
        }

        ByteBuffer byteBuffer = ByteBuffer.wrap(headerArray);
        if (byteOrder != null) {
            byteBuffer.order(byteOrder);
        }

        this.stx = stx;
        this.opCode = TypeHelper.unsignedByteToInt(byteBuffer.get());
        this.id = byteBuffer.getInt();
        this.dataLength = byteBuffer.getInt();
    }

    @Override
    public byte[] createPacket(ByteOrder byteOrder) {

        ByteBuffer byteBuffer = ByteBuffer.allocate(FRAME_SIZE);
        if (byteOrder != null) { byteBuffer.order(byteOrder); }

        byteBuffer.put((byte)STX);
        byteBuffer.put((byte)opCode);
        byteBuffer.putInt(id);
        byteBuffer.putInt(dataLength);

        return byteBuffer.array();
    }
}

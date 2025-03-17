package com.demo.packet;

import com.demo.util.TypeHelper;
import lombok.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class TailFrame implements IPacket{

    public static final int FRAME_SIZE = 2;

    private int checkSum;
    private int etx = 0x03;

    public TailFrame(byte[] tailArray, ByteOrder byteOrder) {

        ByteBuffer byteBuffer = ByteBuffer.wrap(tailArray);
        if (byteOrder != null) { byteBuffer.order(byteOrder); }

        this.checkSum = TypeHelper.unsignedByteToInt(byteBuffer.get());
        this.etx = TypeHelper.unsignedByteToInt(byteBuffer.get());
    }

    @Override
    public byte[] createPacket(ByteOrder byteOrder) {

        ByteBuffer byteBuffer = ByteBuffer.allocate(TailFrame.FRAME_SIZE);
        if (byteOrder != null) { byteBuffer.order(byteOrder); }

        byteBuffer.put((byte)this.checkSum);
        byteBuffer.put((byte)this.etx);

        return byteBuffer.array();
    }
}

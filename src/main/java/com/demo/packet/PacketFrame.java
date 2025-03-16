package com.demo.packet;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@Getter
@Setter
@ToString
public class PacketFrame implements IPacket{

    private HeaderFrame headerFrame;
    private byte[] body;
    private TailFrame tailFrame;

    public PacketFrame(HeaderFrame headerFrame, byte[] body, TailFrame tailFrame) {
        this.headerFrame = headerFrame;
        this.body = body;
        this.tailFrame = tailFrame;
    }

    @Override
    public byte[] createPacket(ByteOrder byteOrder) {

        ByteBuffer byteBuffer = ByteBuffer.allocate( HeaderFrame.FRAME_SIZE + body.length + TailFrame.FRAME_SIZE);
        if (byteOrder != null) { byteBuffer.order(byteOrder); }

        byteBuffer.put(this.headerFrame.createPacket(byteOrder));
        if (body != null && body.length != 0) {
            byteBuffer.put(body);
        }
        byteBuffer.put(this.tailFrame.createPacket(byteOrder));

        return byteBuffer.array();
    }
}

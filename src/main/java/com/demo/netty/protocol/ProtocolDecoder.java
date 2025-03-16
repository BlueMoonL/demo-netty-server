package com.demo.netty.protocol;

import com.demo.netty.config.NettyConfig;
import com.demo.packet.HeaderFrame;
import com.demo.packet.PacketFrame;
import com.demo.packet.TailFrame;
import com.demo.properties.NettyProperties;
import com.demo.util.TypeHelper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProtocolDecoder extends ByteToMessageDecoder {

    private static final int MAX_PACKET_SIZE = 2048;

    private final NettyProperties nettyProperties;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

        if (byteBuf.readableBytes() < HeaderFrame.FRAME_SIZE) {
            log.debug("byteBuf.readableBytes() less thne Header Frame Size : {} / {}", byteBuf.readableBytes(), HeaderFrame.FRAME_SIZE);
            return;
        }

        int stx = byteBuf.readByte();
        if (stx != HeaderFrame.STX) {
            log.error("Wrong Packet : {}", stx);
            byteBuf.markReaderIndex();
            return;
        }

        // 헤더
        byte[] headerByteArray = byteBuf.readBytes(HeaderFrame.FRAME_SIZE - 1).array();
        HeaderFrame headerFrame = new HeaderFrame(stx, headerByteArray, nettyProperties.getByteOrder());

        if (headerFrame.getDataLength() > MAX_PACKET_SIZE) {
            log.error("Wrong Packet - Header data size is to long : {} / {}", headerFrame.getDataLength(), MAX_PACKET_SIZE);
            byteBuf.clear();
            return;
        }

        if (byteBuf.readableBytes() < headerFrame.getDataLength() + TailFrame.FRAME_SIZE) {
            byteBuf.resetReaderIndex();
            return;
        }

        // 바디
        ByteBuf dataBuf = byteBuf.readBytes(headerFrame.getDataLength());

        //테일
        TailFrame tailFrame = new TailFrame(byteBuf.readBytes(TailFrame.FRAME_SIZE).array(), nettyProperties.getByteOrder());

        byteBuf.markReaderIndex();

        int checkSum = checkFrame(headerFrame.createPacket(nettyProperties.getByteOrder()), dataBuf.array());
        if (checkSum != tailFrame.getCheckSum()) {
            log.error("Wrong Packet - CheckSum Error : {} / {}", checkSum, tailFrame.getCheckSum());
            return;
        }

        PacketFrame packetFrame = new PacketFrame(headerFrame, dataBuf.array(), tailFrame);

        list.add(packetFrame);

        log.info("Receive Packet : {}", packetFrame);
    }

    private int checkFrame(byte[] header, byte[] data) {
        byte checkSum = 0;

        for (byte b : header) 	{ checkSum ^= b; }
        for (byte b : data)		{ checkSum ^= b; }

        return TypeHelper.unsignedByteToInt(checkSum);
    }
}

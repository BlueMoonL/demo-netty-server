package com.demo.netty.protocol;

import com.demo.packet.PacketFrame;
import com.demo.properties.NettyProperties;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProtocolEncoder extends MessageToByteEncoder<Object> {

    private final NettyProperties nettyProperties;

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object message, ByteBuf byteBuf) throws Exception {

        if (!(message instanceof PacketFrame)) {
            log.error("Wrong Packet - Msg not instancof PacketFrame(IP - {})",
                    String.format("%s:%d", ((InetSocketAddress)channelHandlerContext.channel().remoteAddress()).getHostName(),
                    ((InetSocketAddress)channelHandlerContext.channel().remoteAddress()).getPort())
            );
        }

        PacketFrame packetFrame = (PacketFrame) message;

        byteBuf.writeBytes(packetFrame.createPacket(nettyProperties.getByteOrder()));

        log.info("Send Packet : {}", packetFrame);
    }
}

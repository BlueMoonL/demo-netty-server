package com.demo.netty.config;

import com.demo.netty.handler.ServerConnectionHandler;
import com.demo.netty.handler.ServerDistributeHandler;
import com.demo.netty.protocol.ProtocolDecoder;
import com.demo.netty.protocol.ProtocolEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {


    private final ServerConnectionHandler serverConnectionHandler;
    private final ProtocolDecoder protocolDecoder;
    private final ProtocolEncoder protocolEncoder;
    private final ServerDistributeHandler serverDistributeHandler;

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {

        ChannelPipeline channelPipeline = socketChannel.pipeline();

        channelPipeline.addLast("ServerConnectionHandler", serverConnectionHandler);
        channelPipeline.addLast("ProtocolDecoder", protocolDecoder);
        channelPipeline.addLast("ProtocolEncoder", protocolEncoder);
        channelPipeline.addLast("ServerDistributeHandler", serverDistributeHandler);
    }
}

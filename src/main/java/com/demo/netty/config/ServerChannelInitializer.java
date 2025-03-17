package com.demo.netty.config;

import com.demo.netty.handler.ServerConnectionHandler;
import com.demo.netty.handler.ServerDistributeHandler;
import com.demo.netty.protocol.ProtocolDecoder;
import com.demo.netty.protocol.ProtocolEncoder;
import com.demo.properties.NettyProperties;
import com.demo.service.DataProcessService;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

@Component
@Scope("prototype")
@RequiredArgsConstructor
public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {


    private final NettyProperties nettyProperties;
    private final Executor taskExecutor;
    private final DataProcessService dataProcessService;

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {

        ChannelPipeline channelPipeline = socketChannel.pipeline();

        channelPipeline.addLast("ServerConnectionHandler", new ServerConnectionHandler(nettyProperties));
        channelPipeline.addLast("ProtocolDecoder", new ProtocolDecoder(nettyProperties));
        channelPipeline.addLast("ProtocolEncoder", new ProtocolEncoder(nettyProperties));
        channelPipeline.addLast("ServerDistributeHandler", new ServerDistributeHandler(taskExecutor, dataProcessService));
    }
}

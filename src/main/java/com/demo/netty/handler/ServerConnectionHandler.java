package com.demo.netty.handler;

import com.demo.properties.NettyProperties;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerConnectionHandler extends IdleStateHandler {

    public ServerConnectionHandler(NettyProperties nettyProperties) {
        super(nettyProperties.getReaderIdleTimeSeconds(), nettyProperties.getWriterIdleTimeSeconds(), nettyProperties.getAllIdleTimeSeconds());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        log.info("Connected Channel #{}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        log.info("Disconnect Client #{}", ctx.channel().remoteAddress());
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {

        log.info("Idle Channel Terminate #{}", ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Channel Exception Caught #{}", ctx.channel().remoteAddress());
        log.error("Exception Message : {}", cause.getMessage());
    }
}

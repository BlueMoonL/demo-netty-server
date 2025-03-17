package com.demo.netty.handler;

import com.demo.packet.PacketFrame;
import com.demo.service.DataProcessService;
import com.demo.worker.PacketProcessorRunner;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executor;

@Slf4j
@RequiredArgsConstructor
public class ServerDistributeHandler extends SimpleChannelInboundHandler<Object> {

    private final Executor taskExecutor;
    private final DataProcessService dataProcessService;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {

        if (o == null) {
            log.error("Message is null");
            return;
        }

        taskExecutor.execute(new PacketProcessorRunner(channelHandlerContext.channel(), (PacketFrame) o, dataProcessService));
    }
}

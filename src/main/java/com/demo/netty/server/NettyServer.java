package com.demo.netty.server;

import com.demo.properties.NettyProperties;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Slf4j
@Component
@RequiredArgsConstructor
public class NettyServer {

    private final ServerBootstrap serverBootstrapFactory;
    private final NettyProperties nettyProperties;
    private ChannelFuture channelFuture;

    public void start() {

        try {
            log.info("Netty 서버 시작...");
            channelFuture = serverBootstrapFactory.bind(nettyProperties.getPort()).sync();
        } catch (Exception e) {
            log.error("Netty 서버 실행 중 오류 발생");
            log.info(e.getMessage());
        }
    }

    @PreDestroy
    public void stop() {

        if(channelFuture != null) {
            channelFuture.channel().close();
        }
        log.info("Netty 서버 종료 중...");
    }
}

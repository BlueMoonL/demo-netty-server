package com.demo.netty.config;

import com.demo.properties.NettyProperties;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;

@Slf4j
@Configuration
@EnableConfigurationProperties(NettyProperties.class)
@RequiredArgsConstructor
public class NettyConfig {

    private final ServerChannelInitializer serverChannelInitializer;
    private final NettyProperties nettyProperties;

    private EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    @Bean(name = "serverBootstrap")
    public ServerBootstrap serverBootstrap() {

        ServerBootstrap serverBootstrap = new ServerBootstrap();

        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(serverChannelInitializer)
                .option(ChannelOption.AUTO_READ, nettyProperties.getAutoRead())
                .option(ChannelOption.TCP_NODELAY, nettyProperties.getTcpNoDelay())
                .option(ChannelOption.SO_RCVBUF, nettyProperties.getSoRcvBuf())
                .option(ChannelOption.SO_SNDBUF, nettyProperties.getSoSndBuf())
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, nettyProperties.getConnectTimeOut())
                .option(ChannelOption.SO_KEEPALIVE, nettyProperties.getKeepAlive());

        return serverBootstrap;
    }

    @PreDestroy
    public void shutdown() {

        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        log.info("Netty 서버 종료 완료");
    }
}

package com.demo.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NettyClientTest {

    private static final String HOST = "127.0.0.1"; // 서버의 호스트 주소
    private static final int PORT = 8080;          // 서버의 포트 번호
    private static final int CLIENT_COUNT = 100;   // 클라이언트 수

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup(); // 클라이언트용 스레드 그룹
        List<Channel> channels = new ArrayList<>();     // 연결된 채널 리스트

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new StringEncoder(StandardCharsets.UTF_8)); // 문자열 인코더
                        }
                    });

            // CLIENT_COUNT만큼 클라이언트를 생성하고 서버에 연결
            for (int i = 0; i < CLIENT_COUNT; i++) {
                ChannelFuture future = bootstrap.connect(HOST, PORT).sync();
                if (future.isSuccess()) {
                    System.out.println("Client " + (i + 1) + " connected.");
                    channels.add(future.channel()); // 연결된 채널 저장
                }
            }

            // 내부 쓰레드로 1초마다 모든 클라이언트가 서버로 데이터를 전송
            Runnable sendDataTask = () -> {
                for (Channel channel : channels) {
                    if (channel.isActive()) {
                        channel.writeAndFlush("Hello from Client at " + System.currentTimeMillis());
                    }
                }
            };

            // 주기적 실행 (1초마다 데이터 전송)
            while (true) {
                sendDataTask.run();
                TimeUnit.SECONDS.sleep(1); // 1초 대기
            }
        } finally {
            // 모든 클라이언트 채널을 닫기
            for (Channel channel : channels) {
                channel.close().sync();
            }
            group.shutdownGracefully();
        }
    }
}
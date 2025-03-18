package com.test.netty.client;

import com.demo.enums.Opcode;
import com.demo.packet.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Random;

public class GpsTestClient {
    private final String host;
    private final int port;

    public GpsTestClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    // GPS 데이터 전송 로직
    public void sendGpsData() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel channel) {
                            channel.pipeline().addLast(new ChannelOutboundHandlerAdapter());
                        }
                    });

            // Channel 연결
            Channel channel = bootstrap.connect(host, port).sync().channel();

            // GPS 데이터 생성 및 전송
            Random random = new Random();
            while (true) {
                GPSInfo gpsInfo = new GPSInfo();
                gpsInfo.setDeviceId(String.valueOf(random.nextInt(999) + 1));
                gpsInfo.setDate(System.currentTimeMillis());
                gpsInfo.setLatitude(37.7749 + random.nextDouble()); // 위도
                gpsInfo.setLongitude(-122.4194 + random.nextDouble()); // 경도
                gpsInfo.setSpeed(random.nextDouble() * 100); // 속도 (km/h)

                // 패킷 생성
                PacketFrame packetFrame = createGpsPacket(gpsInfo);

                // 패킷 전송
                channel.writeAndFlush(Unpooled.copiedBuffer(packetFrame.createPacket(ByteOrder.LITTLE_ENDIAN)));
                Thread.sleep(1000); // 1초 간격으로 데이터 전송
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    // GPS 패킷 생성
    private PacketFrame createGpsPacket(GPSInfo gpsInfo) {
        Random random = new Random();
        byte[] body = gpsInfoToBytes(gpsInfo); // Body 데이터 생성

        // Header 생성
        HeaderFrame headerFrame = new HeaderFrame(
                (byte)HeaderFrame.STX,                      // STX (0x02)
                (byte)Opcode.GPS_INFO.getCode(),         // OpCode
                random.nextInt(999) + 1,                 // 랜덤 ID
                body.length                           // Body 데이터 길이
        );

        // Tail 생성: Checksum 계산
        int checkSum = calculateCheckSum(headerFrame.createPacket(ByteOrder.LITTLE_ENDIAN), body);
        TailFrame tailFrame = new TailFrame(checkSum, 0x03);

        System.out.println("Header Bytes: " + Arrays.toString(headerFrame.createPacket(ByteOrder.LITTLE_ENDIAN)));
        System.out.println("Body Bytes: " + Arrays.toString(body));
        System.out.println("Computed Checksum: " + calculateCheckSum(headerFrame.createPacket(ByteOrder.LITTLE_ENDIAN), body)); // 확인

        // 완성된 패킷 반환
        return new PacketFrame(headerFrame, body, tailFrame);
    }

    // GPSInfo 객체를 바이트 배열로 변환
    private byte[] gpsInfoToBytes(GPSInfo gpsInfo) {
        String json = String.format(
                "{\"deviceId\":\"%s\",\"date\":%d,\"latitude\":%f,\"longitude\":%f,\"speed\":%f}",
                gpsInfo.getDeviceId(), gpsInfo.getDate(), gpsInfo.getLatitude(), gpsInfo.getLongitude(), gpsInfo.getSpeed());
        return json.getBytes(); // JSON 문자열을 바이트 배열로 변환
    }

    // Checksum 계산 (Header + Body 데이터 기반)
    private int calculateCheckSum(byte[] headerBytes, byte[] bodyBytes) {
        byte checkSum = 0;
        for (byte b : headerBytes) {
            checkSum ^= b; // XOR 연산
        }
        for (byte b : bodyBytes) {
            checkSum ^= b; // XOR 연산
        }
        return Byte.toUnsignedInt(checkSum); // 최종 값 반환
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            new Thread(() -> new GpsTestClient("127.0.0.1", 8080).sendGpsData()).start();
        }
    }
}
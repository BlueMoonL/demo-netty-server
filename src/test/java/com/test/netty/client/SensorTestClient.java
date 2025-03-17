package com.test.netty.client;

import com.demo.enums.Opcode;
import com.demo.packet.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Random;

public class SensorTestClient {
    private final String host;
    private final int port;

    public SensorTestClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    // 센서 데이터 전송 로직
    public void sendSensorData() {
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

            // 센서 데이터 생성 및 전송
            Random random = new Random();
            while (true) {
                IotSensorInfo sensorInfo = createRandomSensorInfo(random);

                // 패킷 생성
                PacketFrame packetFrame = createSensorPacket(sensorInfo);

                byte[] fullPacket = packetFrame.createPacket(ByteOrder.LITTLE_ENDIAN); // 전체 패킷 (Header + Body + Tail)
                System.out.println("Full Packet Sent: " + Arrays.toString(fullPacket));


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

    // 랜덤 센서 데이터를 생성
    private IotSensorInfo createRandomSensorInfo(Random random) {
        ByteBuffer buffer = ByteBuffer.allocate(16).order(ByteOrder.LITTLE_ENDIAN); // 센서 데이터는 16바이트 고정
        buffer.putInt(random.nextInt(1000));         // deviceId

        LocalDateTime now = LocalDateTime.now();
        String formattedDate = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")); // YYYYMMDDHH24MISS
        long dateAsLong = Long.parseLong(formattedDate);
        buffer.putLong(dateAsLong);


        buffer.putShort((short) random.nextInt(50)); // temperature
        buffer.putShort((short) random.nextInt(100));// humidity
        return new IotSensorInfo(buffer.array(), ByteOrder.LITTLE_ENDIAN);
    }

    // 센서 패킷 생성
    private PacketFrame createSensorPacket(IotSensorInfo sensorInfo) {
        Random random = new Random();
        byte[] body = sensorInfoToBytes(sensorInfo); // Body 데이터 생성

        // Header 생성
        HeaderFrame headerFrame = new HeaderFrame(
                (byte)HeaderFrame.STX,                      // STX (0x02)
                (byte)Opcode.SENSOR_INFO.getCode(),         // OpCode
                random.nextInt(1000),                 // 랜덤 ID
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


    // IotSensorInfo 객체를 바이트 배열로 변환
    private byte[] sensorInfoToBytes(IotSensorInfo sensorInfo) {
        ByteBuffer buffer = ByteBuffer.allocate(16).order(ByteOrder.LITTLE_ENDIAN); // 센서 데이터는 16바이트 (고정)
        buffer.putInt(sensorInfo.getDeviceId());     // deviceId (4바이트)
        buffer.putLong(sensorInfo.getDate());        // date (8바이트)
        buffer.putShort(sensorInfo.getTemperature());// temperature (2바이트)
        buffer.putShort(sensorInfo.getHumidity());   // humidity (2바이트)
        return buffer.array();
    }

    // Checksum 계산 (Header + Body 데이터 기반)
    private byte calculateCheckSum(byte[] headerBytes, byte[] bodyBytes) {
        byte checkSum = 0;
        for (byte b : headerBytes) {
            checkSum ^= b; // XOR 연산
        }
        for (byte b : bodyBytes) {
            checkSum ^= b; // XOR 연산
        }
        return checkSum; // 최종 값 반환
    }

    public static void main(String[] args) {
        for (int i = 0; i < 1; i++) {
            new Thread(() -> new SensorTestClient("127.0.0.1", 8080).sendSensorData()).start();
        }
    }
}
package com.demo.netty.protocol;

import com.demo.packet.HeaderFrame;
import com.demo.packet.PacketFrame;
import com.demo.packet.TailFrame;
import com.demo.properties.NettyProperties;
import com.demo.util.TypeHelper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class ProtocolDecoder extends ByteToMessageDecoder {

    private static final int MAX_PACKET_SIZE = 2048;

    private final NettyProperties nettyProperties;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

//        if (byteBuf.readableBytes() < HeaderFrame.FRAME_SIZE) {
//            log.debug("byteBuf.readableBytes() less thne Header Frame Size : {} / {}", byteBuf.readableBytes(), HeaderFrame.FRAME_SIZE);
//            return;
//        }
//
//        int stx = byteBuf.readByte();
//        if (stx != HeaderFrame.STX) {
//            log.error("Wrong Packet : {}", stx);
//            byteBuf.markReaderIndex();
//            return;
//        }
//
//        // 헤더
//        byte[] headerByteArray =  new byte[HeaderFrame.FRAME_SIZE];
//        byteBuf.readBytes(headerByteArray);
//        HeaderFrame headerFrame = new HeaderFrame(stx, headerByteArray, nettyProperties.getByteOrder());
//
//        if (headerFrame.getDataLength() > MAX_PACKET_SIZE) {
//            log.error("Wrong Packet - Header data size is to long : {} / {}", headerFrame.getDataLength(), MAX_PACKET_SIZE);
//            byteBuf.clear();
//            return;
//        }
//
//        if (byteBuf.readableBytes() < headerFrame.getDataLength() + TailFrame.FRAME_SIZE) {
//            byteBuf.resetReaderIndex();
//            return;
//        }
//
//        // 바디
//        byte[] bodyBytes = new byte[headerFrame.getDataLength()];
//        byteBuf.readBytes(bodyBytes);
//
//
//        //테일
//        byte[] tailByteArray = new byte[TailFrame.FRAME_SIZE];
//        byteBuf.readBytes(tailByteArray);
//        TailFrame tailFrame = new TailFrame(tailByteArray, nettyProperties.getByteOrder());
//
//        byteBuf.markReaderIndex();
//
//        int checkSum = checkFrame(headerFrame.createPacket(nettyProperties.getByteOrder()), bodyBytes);
//        if (checkSum != tailFrame.getCheckSum()) {
//            log.error("Wrong Packet - CheckSum Error : {} / {}", checkSum, tailFrame.getCheckSum());
//            return;
//        }
//
//        PacketFrame packetFrame = new PacketFrame(headerFrame, bodyBytes, tailFrame);
//
//        list.add(packetFrame);
//
//        log.info("Receive Packet : {}", packetFrame);
        // 디버깅: 현재 ByteBuf의 상태 출력
        // 수신된 원본 데이터 출력
        byte[] rawBytes = new byte[byteBuf.readableBytes()];
        byteBuf.getBytes(byteBuf.readerIndex(), rawBytes); // ByteBuf의 현재 상태 출력
        log.info("Raw Bytes Received: {}", Arrays.toString(rawBytes));


        log.debug("ByteBuf Readable Bytes: {}", byteBuf.readableBytes());

        if (byteBuf.readableBytes() < HeaderFrame.FRAME_SIZE ) {
            log.debug("Not enough data for Header: {} bytes available", byteBuf.readableBytes());
            return;
        }

        // STX 읽기
        int stx = byteBuf.readByte();
        if (stx != HeaderFrame.STX) {
            log.error("STX mismatch or corrupted data. Expected {}, Received {}", HeaderFrame.STX, stx);
            byteBuf.markReaderIndex(); // STX를 못 읽었으므로 다시 읽기 시작 지점으로 돌아감
            return;
        }

        // 헤더 읽기
        byte[] headerByteArray = new byte[HeaderFrame.FRAME_SIZE - 1];
        byteBuf.readBytes(headerByteArray);
        HeaderFrame headerFrame = new HeaderFrame(stx, headerByteArray, nettyProperties.getByteOrder());
        log.debug("Decoded Header: {}", Arrays.toString(headerByteArray));

        // Data Length 체크
        if (headerFrame.getDataLength() > MAX_PACKET_SIZE) {
            log.error("Body size too large: {}, Max Allowed: {}", headerFrame.getDataLength(), MAX_PACKET_SIZE);
            byteBuf.clear(); // 잘못된 패킷이므로 버퍼를 초기화
            return;
        }

        // Body 읽기 전 크기 확인
        if (byteBuf.readableBytes() < headerFrame.getDataLength()) {
            log.debug("Not enough data for Body: {} bytes available", byteBuf.readableBytes());
            byteBuf.resetReaderIndex(); // 패킷이 모두 들어올 때까지 기다림
            return;
        }

        // 바디 읽기
        byte[] bodyBytes = new byte[headerFrame.getDataLength()];
        byteBuf.readBytes(bodyBytes);
        log.debug("Decoded Body: {}", Arrays.toString(bodyBytes));

        // Tail Frame 읽기
        if (byteBuf.readableBytes() < TailFrame.FRAME_SIZE) {
            log.debug("Not enough data for Tail: {} bytes available", byteBuf.readableBytes());
            byteBuf.resetReaderIndex(); // 테일 데이터가 없으면 다시 시도
            return;
        }
        byte[] tailByteArray = new byte[TailFrame.FRAME_SIZE];
        byteBuf.readBytes(tailByteArray);
        TailFrame tailFrame = new TailFrame(tailByteArray, nettyProperties.getByteOrder());
        log.debug("Decoded Tail: {}", Arrays.toString(tailByteArray));

        // 체크섬 계산 비교
        byte calculatedCheckSum = checkFrame(headerFrame.createPacket(nettyProperties.getByteOrder()), bodyBytes);
        if (TypeHelper.unsignedByteToInt(calculatedCheckSum) != tailFrame.getCheckSum()) {
            log.error("Checksum mismatch: Calculated {}, Received {}", calculatedCheckSum, tailFrame.getCheckSum());
            return;
        }

        // 패킷 생성
        PacketFrame packetFrame = new PacketFrame(headerFrame, bodyBytes, tailFrame);
        list.add(packetFrame);
        log.info("Packet successfully decoded: {}", packetFrame);


}

    private byte checkFrame(byte[] headerBytes, byte[] bodyBytes) {
        byte checkSum = 0;
        // Header 부분 XOR 처리
        for (byte b : headerBytes) {
            checkSum ^= b;
        }
        // Body 부분 XOR 처리
        for (byte b : bodyBytes) {
            checkSum ^= b;
        }
        return checkSum;
    }

}

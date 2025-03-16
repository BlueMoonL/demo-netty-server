package com.demo.worker;

import com.demo.enums.Opcode;
import com.demo.packet.HeaderFrame;
import com.demo.packet.PacketFrame;
import com.demo.service.DataProcessService;
import io.netty.channel.Channel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.mockito.Mockito.*;

class PacketProcessorRunnerTest {

    private Channel mockChannel;
    private PacketFrame mockPacketFrame;
    private HeaderFrame mockHeaderFrame;
    private DataProcessService mockDataProcessService;

    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        // Mock 객체 생성
        mockChannel = mock(Channel.class);
        mockPacketFrame = mock(PacketFrame.class);
        mockHeaderFrame = mock(HeaderFrame.class);
        mockDataProcessService = mock(DataProcessService.class);

        // 표준 출력 캡처
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        // PacketFrame에 HeaderFrame 연결
        when(mockPacketFrame.getHeaderFrame()).thenReturn(mockHeaderFrame);
    }

    @Test
    void testRun_whenIdIsZero_shouldNotProcess() {
        // HeaderFrame ID 설정 (ID == 0)
        when(mockHeaderFrame.getId()).thenReturn(0);

        // 실행
        PacketProcessorRunner runner = new PacketProcessorRunner(mockChannel, mockPacketFrame, mockDataProcessService);
        runner.run();

        // 결과 확인
        verify(mockHeaderFrame, times(1)).getId();
        String output = outputStream.toString();
        assert !output.contains("Run Processing");
    }

    @Test
    void testRun_withValidSensorInfoOpcode() {
        // HeaderFrame ID 및 Opcode 설정
        when(mockHeaderFrame.getId()).thenReturn(100);
        when(mockHeaderFrame.getOpCode()).thenReturn(Opcode.SENSOR_INFO.getCode());

        PacketProcessorRunner runner = new PacketProcessorRunner(mockChannel, mockPacketFrame, mockDataProcessService);
        runner.run();

        // IotSensorInfoProcessor가 생성되었는지 확인
        String output = outputStream.toString();
        assert output.contains("Run Processing");
        verify(mockHeaderFrame, times(1)).getOpCode();
    }

    @Test
    void testRun_withValidGpsInfoOpcode() {
        // HeaderFrame ID 및 Opcode 설정
        when(mockHeaderFrame.getId()).thenReturn(300);
        when(mockHeaderFrame.getOpCode()).thenReturn(Opcode.GPS_INFO.getCode());

        PacketProcessorRunner runner = new PacketProcessorRunner(mockChannel, mockPacketFrame, mockDataProcessService);
        runner.run();

        // GpsInfoProcessor가 생성되었는지 확인
        String output = outputStream.toString();
        assert output.contains("Run Processing");
        verify(mockHeaderFrame, times(1)).getOpCode();
    }

    @Test
    void testRun_withUnknownOpcode() {
        // 유효하지 않은 Opcode 설정
        when(mockHeaderFrame.getId()).thenReturn(123);
        when(mockHeaderFrame.getOpCode()).thenReturn(0x99); // 존재하지 않는 Opcode

        PacketProcessorRunner runner = new PacketProcessorRunner(mockChannel, mockPacketFrame, mockDataProcessService);
        runner.run();

        String output = outputStream.toString();

        // "Send NAK INVALID_OPCODE" 메시지가 출력되는지 assert 확인
        assert output.contains("Send NAK INVALID_OPCODE");
    }

}

package com.demo.worker;

import com.demo.enums.Opcode;
import com.demo.packet.PacketFrame;
import com.demo.processor.DummyProcessor;
import com.demo.processor.GpsInfoProcessor;
import com.demo.processor.IotSensorInfoProcessor;
import com.demo.processor.Processable;
import com.demo.service.DataProcessService;
import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class PacketProcessorRunner implements Runnable {

    private final Channel channel;
    private final PacketFrame packetFrame;
    private final DataProcessService dataProcessService;

    @Override
    public void run() {

        Processable dataProcessor = null;

        int id = packetFrame.getHeaderFrame().getId();

        if (id == 0) {
            // ID가 0인 경우 처리하지 않음
            log.info("Skipping processing due to ID=0");
            return;
        }

        Opcode opCode = null;
        try {
            // Opcode 변환 시도
            opCode = Opcode.fromCode(packetFrame.getHeaderFrame().getOpCode());
        } catch (IllegalArgumentException ex) {
            // 알 수 없는 Opcode 처리
            log.error("Unknown OpCode: {} - Creating DummyProcessor", packetFrame.getHeaderFrame().getOpCode());
            dataProcessor = new DummyProcessor(channel, packetFrame);
        }

        if (dataProcessor == null) {
            // 유효한 OpCode 처리
            switch (opCode) {
                case SENSOR_INFO: {
                    dataProcessor = new IotSensorInfoProcessor(channel, packetFrame, dataProcessService);
                    break;
                }

                case GPS_INFO: {
                    dataProcessor = new GpsInfoProcessor(channel, packetFrame, dataProcessService);
                    break;
                }
            }
        }

        if (dataProcessor != null) {
            log.info("Run Processing[id : {}] {}", id, dataProcessor.getClass());
            dataProcessor.Processing();
        } else {
            // 알 수 없는 Opcode일 경우 NAK 로그 출력

            log.error("Send NAK INVALID_OPCODE {} : ID #{}", packetFrame.getHeaderFrame().getOpCode(), id);
            dataProcessor = new DummyProcessor(channel, packetFrame);
            dataProcessor.Processing();
        }
    }
}
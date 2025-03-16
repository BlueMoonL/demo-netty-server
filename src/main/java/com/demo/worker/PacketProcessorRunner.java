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
            // Nack 보내기
            log.error("Send NAK INVALID_ID {}", id);
            return;
        }

        Opcode opCode;
        try {
            // getOpCode()가 int 값을 반환하므로 Enum으로 변환
            opCode = Opcode.fromCode(packetFrame.getHeaderFrame().getOpCode());
        } catch (IllegalArgumentException ex) {
            log.error("Unknown OpCode: " + packetFrame.getHeaderFrame().getOpCode());
            return;
        }


        switch (opCode) {
            case SENSOR_INFO : {
                dataProcessor = new IotSensorInfoProcessor(channel, packetFrame, dataProcessService);
                break;
            }

            case GPS_INFO : {
                dataProcessor = new GpsInfoProcessor(channel, packetFrame, dataProcessService);
                break;
            }

            default: {
                dataProcessor = new DummyProcessor(channel, packetFrame);
            }
        }

        if (dataProcessor != null) {
            log.info("Run Processing[id : {}] {}", id, dataProcessor.getClass());
            dataProcessor.Processing();
        } else {
            // 나크 보내기
            log.error("Send NAK INVALID_OPCODE {} : ID #{}", packetFrame.getHeaderFrame().getOpCode(), packetFrame.getHeaderFrame().getId());
        }
    }
}

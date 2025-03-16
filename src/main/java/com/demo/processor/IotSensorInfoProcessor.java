package com.demo.processor;


import com.demo.packet.PacketFrame;
import com.demo.service.DataProcessService;
import com.demo.service.IotSensorInfoService;
import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class IotSensorInfoProcessor implements Processable {

    private final Channel channel;
    private final PacketFrame packetFrame;
    private final DataProcessService dataProcessService;

    @Override
    public void Processing() {

        log.info("IotSensorInfoProcessor Processing (#{})", packetFrame.getHeaderFrame().getId());

        if(dataProcessService.processIotSensorEvent(channel, packetFrame)) {
            log.info("IotSensorInfoProcessor Processing Success (#{})", packetFrame.getHeaderFrame().getId());
        } else {
            log.error("IotSensorInfoProcessor Processing Fail (#{})", packetFrame.getHeaderFrame().getId());
        }
    }
}

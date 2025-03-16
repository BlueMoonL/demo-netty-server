package com.demo.processor;

import com.demo.packet.PacketFrame;
import com.demo.service.DataProcessService;
import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class GpsInfoProcessor implements Processable {

    private final Channel channel;
    private final PacketFrame packetFrame;
    private final DataProcessService dataProcessService;

    @Override
    public void Processing() {

        log.info("GpsInfoProcessor Processing (#{})", packetFrame.getHeaderFrame().getId());

        if(dataProcessService.processGpsEvent(channel, packetFrame)) {
            log.info("GpsInfoProcessor Processing Success (#{})", packetFrame.getHeaderFrame().getId());
        } else {
            log.error("GpsInfoProcessor Processing Fail (#{})", packetFrame.getHeaderFrame().getId());
        }
    }
}

package com.demo.processor;

import com.demo.packet.HeaderFrame;
import com.demo.packet.PacketFrame;
import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class DummyProcessor implements Processable {

    private final Channel channel;
    private final PacketFrame packetFrame;

    @Override
    public void Processing() {

        HeaderFrame headerFrame = packetFrame.getHeaderFrame();

        log.info("Processing - Dummy Information({}) #{} : {}", channel.remoteAddress(), headerFrame.getId(), headerFrame.getOpCode());
        System.out.println("Send NAK INVALID_OPCODE");
    }
}

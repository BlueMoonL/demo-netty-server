package com.demo.service;

import com.demo.packet.PacketFrame;
import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataProcessService {

    private final IotSensorInfoService iotSensorInfoService;
    private final GpsInfoService gpsInfoService;

    public boolean processIotSensorEvent(Channel channel, PacketFrame packetFrame) {

        if (iotSensorInfoService.insertIotSensorInfo(packetFrame)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean processGpsEvent(Channel channel, PacketFrame packetFrame) {

        if (gpsInfoService.insertGpsInfo(packetFrame)) {
            return true;
        } else {
            return false;
        }
    }
}

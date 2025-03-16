package com.demo.service;

import com.demo.mapper.SensorInfoMapper;
import com.demo.packet.IotSensorInfo;
import com.demo.packet.PacketFrame;
import com.demo.properties.NettyProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class IotSensorInfoService {

    private final NettyProperties nettyProperties;
    private final SensorInfoMapper sensorInfoMapper;

    public boolean insertIotSensorInfo(PacketFrame packetFrame) {

        IotSensorInfo iotSensorInfo = new IotSensorInfo(packetFrame.getBody(), nettyProperties.getByteOrder());

        try {
            sensorInfoMapper.insertSensorInfo(iotSensorInfo);
            return true;
        } catch (Exception e) {
            log.error("DB Insert Error - {}", e.getMessage());
            return false;
        }

    }
}

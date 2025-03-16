package com.demo.service;

import com.demo.mapper.GpsInfoMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.demo.packet.GPSInfo;
import com.demo.packet.PacketFrame;
import com.demo.properties.NettyProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class GpsInfoService {

    private final NettyProperties nettyProperties;
    private final GpsInfoMapper gpsInfoMapper;

    public boolean insertGpsInfo(PacketFrame packetFrame) {

        try {
            GPSInfo gpsInfo = gpsInfoParsing(packetFrame.getBody(), nettyProperties.getByteOrder());

            if (gpsInfo != null) {
                gpsInfoMapper.insertGpsInfo(gpsInfo);
                return true;
            } else {
                log.error("GPS Data Parsing Error");
                return false;
            }
        } catch (Exception e) {
            log.error("DB Insert Error - {}", e.getMessage());
            return false;
        }
    }

    public GPSInfo gpsInfoParsing(byte[] data, ByteOrder byteOrder) {

        try {
            // ByteBuffer로 변환
            ByteBuffer byteBuffer = ByteBuffer.wrap(data);

            // Little Endian 적용 (필요한 경우)
            if (byteOrder != null) {
                byteBuffer.order(byteOrder);
            }

            // JSON 데이터 추출 (ByteBuffer의 모든 데이터를 문자열로 변환)
            String jsonString = new String(byteBuffer.array(), StandardCharsets.UTF_8);

            // JSON → GpsData 변환
            ObjectMapper objectMapper = new ObjectMapper();
            GPSInfo gpsData = objectMapper.readValue(jsonString.trim(), GPSInfo.class);

            return gpsData;
        } catch (Exception e) {
            log.error("GPS Data Parsing Error - {}", e.getMessage());
            return null;
        }
    }
}

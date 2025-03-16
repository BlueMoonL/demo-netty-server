package com.demo.packet;

import lombok.Data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@Data
public class GPSInfo {

    private String deviceId;   // 기기 ID
    private long date;         // 시간
    private double latitude;   // 위도
    private double longitude;  // 경도
    private double speed;      // 속도 (km/h)
}

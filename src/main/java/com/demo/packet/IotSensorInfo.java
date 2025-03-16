package com.demo.packet;

import lombok.Data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@Data
public class IotSensorInfo {

    private int deviceId;       // 4바이트 (Integer)
    private long date;      // 8바이트 (Long)
    private short temperature;   // 2바이트 (Short)
    private short humidity;      // 2바이트 (Short)

    public IotSensorInfo(byte[] data, ByteOrder byteOrder) {

        ByteBuffer byteBuffer = ByteBuffer.wrap(data);
        if (byteOrder != null) { byteBuffer.order(byteOrder); }

        this.deviceId = byteBuffer.getInt();
        this.date = byteBuffer.getLong();
        this.temperature = byteBuffer.getShort();
        this.humidity = byteBuffer.getShort();
    }
}

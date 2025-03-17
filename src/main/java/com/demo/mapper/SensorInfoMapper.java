package com.demo.mapper;

import com.demo.packet.IotSensorInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SensorInfoMapper {

    void insertSensorInfo(IotSensorInfo IotSensorInfo);
}

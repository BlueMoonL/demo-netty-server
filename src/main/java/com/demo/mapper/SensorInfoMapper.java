package com.demo.mapper;

import com.demo.packet.IotSensorInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SensorInfoMapper {

    void insertSensorInfo(IotSensorInfo IotSensorInfo);
}

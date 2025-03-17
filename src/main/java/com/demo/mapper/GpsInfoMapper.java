package com.demo.mapper;

import com.demo.packet.GPSInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GpsInfoMapper {

    void insertGpsInfo(GPSInfo gpsInfo);
}

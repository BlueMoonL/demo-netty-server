package com.demo.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.ByteOrder;

@Data
@ConfigurationProperties(prefix = "netty")
public class NettyProperties {

    private int port;
    private int readerIdleTimeSeconds;
    private int writerIdleTimeSeconds;
    private int allIdleTimeSeconds;
    private Boolean byteOrder;
    private Boolean autoRead;
    private Boolean tcpNoDelay;
    private int soRcvBuf;
    private int soSndBuf;
    private int connectTimeOut;
    private Boolean keepAlive;

    public ByteOrder getByteOrder() {
        return byteOrder ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
    }
}

# **Demo Netty Server**
### **프로젝트 소개**
`Demo Netty Server`는 **Netty** 프레임워크를 기반으로 설계된 TCP 서버입니다.
이 서버는 Netty의 비동기 이벤트 기반 네트워크 처리를 활용합니다.
주요 구성은 데이터 패킷의 인코딩/디코딩, 요청 분배 및 병렬 처리 기반의 작업 프로세스를 포함하며, Spring Boot를 활용하였습니다.

## **주요 기능**
- **Netty 기반 TCP 서버**:
    - 효율적이고 비동기 방식의 네트워크 데이터 처리.

- **패킷 처리**:
    - 사용자 정의 디코더 및 인코더를 통해 IOT 센서 및 GPS 데이터 패킷 구조 지원.

- **병렬 처리**:
    - 데이터 처리 작업을 병렬화하였음.

- **Spring 기반 DI**:
    - Spring Boot와 함께 빈 관리 및 설정 자동화를 사용.

- **유연한 설정**:
    - Netty 관련 설정 및 스레드 풀 크기, KeepAlive 등 주요 값들을 `application.yml`설정을 통해 수정 가능.

## **사용 기술**
- **프레임워크**:
    - Spring Boot (2.7.18)
    - Netty (4.1.109.Final)
    - MyBatis

- **데이터베이스**:
    - MySQL (Connector: 8.0.33)

- **빌드 도구**:
    - Maven (Java 8 기반)

- **라이브러리**:
    - Lombok (1.18.28)
    - Jackson (2.15.3)
    - JUnit (5.9.3) 및 Mockito (5.3.1)를 활용한 테스트 코드 작성

## **주요 클래스 설명**
### 1. **핸들러**
- `ServerConnectionHandler`:
    - 클라이언트 연결 및 종료 이벤트 로깅.
    - Idle 상태(시간 초과)에 따른 연결 종료 처리.

- `ServerDistributeHandler`:
    - 들어오는 데이터 처리 요청을 스레드풀로 분배.

### 2. **패킷 디코더/인코더**
- `ProtocolDecoder`:
    - TCP 데이터 패킷을 `PacketFrame` 객체로 디코딩.
    - Header/Body/Tail 구조.

- `ProtocolEncoder`:
    - `PacketFrame` 객체를 TCP 패킷으로 인코딩하여 클라이언트로 전송.

### 3. **데이터 처리**
- `DataProcessService`:
    - IOT 센서 이벤트 및 GPS 이벤트 데이터 처리 로직.
    - 각각의 데이터는 내부 서비스(`IotSensorInfoService`, `GpsInfoService`)를 통해 데이터베이스로 저장.

### 4. **Netty 설정**
- `NettyConfig`:
    - Netty의 주요 설정(포트, TCP 옵션 등)을 초기화.
    - 정의된 핸들러 체인을 네트워크 채널에 바인딩.
 
## **패킷 구조**
이 프로젝트는 예시로 만든 IOT 센서 데이터와 GPS 데이터를 처리하며, 각각 아래와 같은 패킷 구조를 따릅니다:
### **IOT 센서 데이터 패킷**
- 바이너리 데이터로 값이 전송되며, 각 필드는 고정된 크기로 제공됩니다:
    - **`deviceId`** (4 바이트, 정수형): 기기 고유 ID
    - **`date`** (8 바이트, Long): 센서 데이터가 생성된 시간
    - **`temperature`** (2 바이트, Short): 기기의 온도 값
    - **`humidity`** (2 바이트, Short): 기기의 습도 값

- **파싱 방식**:
    - 패킷 바이트 데이터를 `ByteBuffer` 객체로 변환하여 각 필드로 읽어들임.

- **예제 코드** (`IotSensorInfo.java`):
``` java
private int deviceId;     // 4바이트
private long date;        // 8바이트
private short temperature;  // 2바이트
private short humidity;     // 2바이트
```
### **GPS 데이터 패킷**
- **JSON** 형식으로 변환된 데이터를 포함.
- 위도, 경도, 속도 등을 포함하는 전형적인 GPS 데이터 정보를 수신합니다:
    - **`deviceId`**: 기기 ID
    - **`date`**: 데이터 생성 시간
    - **`latitude`**: 위도
    - **`longitude`**: 경도
    - **`speed`**: 주행 속도

- **파싱 방식**:
    - 수신된 패킷 데이터를 문자열로 변환한 뒤, JSON 파서를 사용하여 Java 객체(`GPSInfo.java`)로 변환.

- **예제 코드** (`GPSInfoService`):
``` java
GPSInfo gpsInfo = objectMapper.readValue(jsonString.trim(), GPSInfo.class);
```


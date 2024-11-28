package org.yuxun.x.nexusx.Service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.yuxun.x.nexusx.Enmu.DeviceStatus;
import org.yuxun.x.nexusx.Entity.Devices;
import org.yuxun.x.nexusx.Mapper.DevicesMapper;

import java.time.LocalDateTime;

@Service
public class HeartbeatService {

    private final DevicesMapper devicesMapper;

    @Autowired
    public HeartbeatService(DevicesMapper devicesMapper) {
        this.devicesMapper = devicesMapper;
    }

    // 更新设备的状态，通常是在心跳包收到时调用
    public void updateDeviceStatus(String deviceId, String status) {
        Devices device = devicesMapper.getDeviceInfo(deviceId);
        if (device != null) {
            device.setDeviceStatus(DeviceStatus.valueOf(status)); // 更新状态
            devicesMapper.updateById(device);
        }
    }

    // 定期检查设备状态（例如，每分钟检查一次）
    @Scheduled(fixedRate = 60000) // 每分钟执行一次
    public void checkDeviceHeartbeat() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(2); // 2分钟内未收到心跳包的设备为离线
        devicesMapper.updateDeviceStatusByHeartbeat(cutoffTime); // 假设你在 Mapper 中写了此方法
    }
}

package org.yuxun.x.nexusx.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.yuxun.x.nexusx.DTO.DeviceInfoDTO;
import org.yuxun.x.nexusx.Enmu.DeviceStatus;
import org.yuxun.x.nexusx.Entity.Devices;
import org.yuxun.x.nexusx.Mapper.DevicesMapper;
import org.yuxun.x.nexusx.Service.DevicesService;
import org.yuxun.x.nexusx.Utils.MagicPacketUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

@Service
@Log4j2
public class DevicesServiceImpl extends ServiceImpl<DevicesMapper,Devices> implements DevicesService {

    private final DevicesMapper devicesMapper;

    @Autowired
    public DevicesServiceImpl(DevicesMapper devicesMapper) {
        this.devicesMapper = devicesMapper;
    }

    /**
     * 录入设备相应信息
     */
    @Override
    public int registerDeviceInfo(DeviceInfoDTO device){
        Devices devices = DeviceInfoDTO.convertToEntity(device);
        QueryWrapper<Devices> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("device_id", devices.getDeviceId());
        Devices reDevice = devicesMapper.selectOne(queryWrapper);
        if (reDevice != null) {
            throw new IllegalArgumentException("设备信息已存在！");
        }
        return devicesMapper.insert(devices);
    }

    /**
     * 获取对应设备信息
     * @author yuxun
     */
    @Override
    public Devices getDeviceInfo(String deviceId){
        QueryWrapper<Devices> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deviceId", deviceId);
        return devicesMapper.selectOne(queryWrapper);
    }

    /**
     * 获取设备信息列表
     */
    @Override
    public List<Devices> getDeviceInfoList(String userId) {
        QueryWrapper<Devices> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        return devicesMapper.selectList(queryWrapper);
    }



    /**
     * 获取设备状态
     */
    @Override
    public String getDeviceStatus(String deviceId) {
        Long id = Long.parseLong(deviceId); // 假设 deviceId 为 String 类型，但数据库中的 device_id 是 BIGINT 类型
        QueryWrapper<Devices> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("device_id", id);
        Devices device = devicesMapper.selectOne(queryWrapper);

        if (device != null && device.getDeviceStatus() != null) {
            // 获取数据库中存储的设备状态，并转换为 DeviceStatus 枚举
            DeviceStatus deviceStatus = device.getDeviceStatus();
            return deviceStatus.getDescription(); // 返回设备状态的中文描述
        }

        return "未知状态"; // 如果设备不存在或状态为 null，返回 "未知状态"
    }

    /**
     * 判断设备是否已存在
     */
    @Override
    public boolean deviceExists(String deviceId) {
        QueryWrapper<Devices> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("device_id", deviceId);
        return devicesMapper.selectCount(queryWrapper) > 0;
    }

    /**
     * 登录时间间隔
     */
    @Override
    public long calculateLoginInterval(String deviceId) {
        Devices device = this.getDeviceInfo(deviceId);
        if (device == null || device.getLastOnlineTime() == null) {
            return -1; // 设备不存在或从未登录
        }
        LocalDateTime lastLoginTime = device.getLastOnlineTime();
        return ChronoUnit.DAYS.between(lastLoginTime, LocalDateTime.now()); // 计算天数差
    }


    /**
     * 设备解绑
     */
    @Override
    public void unbindDevice(Long userId, String deviceId) {
        Devices device = this.getDeviceInfo(deviceId);
        if (device == null || !Objects.equals(device.getUserId(), userId)) {
            throw new RuntimeException("设备不存在或用户无权解绑此设备！");
        }
        device.setUserId(null); // 清除用户绑定
        device.setDeviceStatus(DeviceStatus.fromValue(0)); // 设置设备为离线
        devicesMapper.updateById(device); // 更新设备信息
    }

    /**
     * 清除无主设备信息
     */
    @Override
    public void cleanUnboundDevices(LocalDateTime cutoffTime) {
        QueryWrapper<Devices> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNull("user_id") // 查询没有绑定用户的设备
                .lt("updated_at", cutoffTime); // 根据更新时间判断是否过期
        devicesMapper.delete(queryWrapper); // 删除设备
    }


    /**
     * 检查绑定信息
     */
    @Override
    public Long checkDeviceBinding(String deviceId) {
        Devices device = this.getDeviceInfo(deviceId);
        return (device != null) ? device.getUserId() : null; // 如果设备绑定了用户，则返回 user_id
    }

    /**
     * 唤醒设备
     */
    @Override
    public boolean wakeUpDevice(String deviceId) {
        // 假设从数据库中获取设备信息
        Devices device = getDeviceInfo(deviceId);
        if (device == null) {
            log.error("设备不存在，ID：{}", deviceId);
            return false;
        }
        try {
            // 使用工具类发送唤醒信号
            String macAddress = device.getDeviceMacAddress();
            String broadcastIP = device.getDeviceBroadcastIP(); // 假设存储在设备信息中
            MagicPacketUtils.sendMagicPacket(macAddress, broadcastIP);
            log.info("唤醒信号已发送到设备，ID：{}", deviceId);
            return true;
        } catch (Exception e) {
            log.error("唤醒设备失败，ID：{}，错误：{}", deviceId,e.getMessage());
            return false;
        }
    }

    // 处理设备的心跳包
    public void handleHeartbeat(Long deviceId) {
        Devices device = devicesMapper.getDeviceInfo(String.valueOf(deviceId));

        if (device != null) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime lastHeartbeatTime = device.getLastHeartbeatTime();

            // 判断设备是否超时，如果超过2分钟没收到心跳包，则标记为离线
            if (lastHeartbeatTime != null && Duration.between(lastHeartbeatTime, now).toMinutes() > 2) {
                // 设备心跳超时，设置为离线
                device.setDeviceStatus(DeviceStatus.fromValue(DeviceStatus.OFFLINE.getValue()));
            } else if (Integer.parseInt(String.valueOf(device.getDeviceStatus())) == DeviceStatus.SLEEP.getValue()) {
                // 如果设备处于休眠状态，尝试唤醒设备
                wakeUpDevice(deviceId);
            } else {
                // 如果设备处于在线状态，更新心跳时间
                device.setLastHeartbeatTime(now);
            }

            // 更新设备状态和心跳时间
            device.setLastHeartbeatTime(now);
            devicesMapper.updateById(device);
        }
    }

    // 唤醒设备，将设备状态设置为在线
    private void wakeUpDevice(Long deviceId) {
        Devices device = devicesMapper.getDeviceInfo(String.valueOf(deviceId));
        if (device != null) {
            device.setDeviceStatus(DeviceStatus.fromValue(DeviceStatus.ONLINE.getValue()));
            devicesMapper.updateById(device);
        }
    }

    // 定期检查设备的心跳和状态，超时则设置为离线
    @Scheduled(fixedRate = 60000) // 每分钟检查一次
    public void checkDeviceStatus() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(2); // 如果超过2分钟没收到心跳包，设备为离线
        List<Devices> devices = devicesMapper.getDevicesByLastHeartbeatTimeBefore(cutoffTime);

        for (Devices device : devices) {
            if (Integer.parseInt(String.valueOf(device.getDeviceStatus())) != DeviceStatus.OFFLINE.getValue()) {
                // 如果设备状态不是离线，更新为离线
                device.setDeviceStatus(DeviceStatus.fromValue(DeviceStatus.OFFLINE.getValue()));
                devicesMapper.updateById(device);
            }
        }
    }

}

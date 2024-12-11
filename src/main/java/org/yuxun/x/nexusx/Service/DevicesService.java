package org.yuxun.x.nexusx.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.yuxun.x.nexusx.DTO.DeviceInfoDTO;
import org.yuxun.x.nexusx.Entity.Devices;

import java.time.LocalDateTime;
import java.util.List;


public interface DevicesService extends IService<Devices> {

    /**
     * 将设备信息录入系统
     * @param device
     * @return 执行情况
     */
    int registerDeviceInfo(DeviceInfoDTO device);

    /**
     * 获取对应用户的设备信息（UUID，名称，状态等）
     * @param deviceId 设备唯一标识
     * @return 设备信息
     */
    Devices getDeviceInfo(String deviceId);

    /**
     * 获取对应用户的所有设备信息（UUID，名称，状态等）
     * @param userId 设备唯一标识
     * @return 设备信息
     */
    List<Devices> getDeviceInfoList(String userId);

    /**
     * 获取设备的实时状态
     * @param deviceId 设备唯一标识
     * @return 设备状态
     */
    String getDeviceStatus(String deviceId);

    /**
     * 计算设备的登录时间间隔
     * @param deviceId 设备唯一标识
     * @return 登录时间间隔（单位：天）
     */
    long calculateLoginInterval(String deviceId);

    /**
     * 解除设备与用户的绑定关系
     * @param userId 用户ID
     * @param deviceId 设备ID
     */
    void unbindDevice(Long userId, String deviceId);

    /**
     * 定期清理无主设备
     * @param cutoffTime 清理的截止时间
     */
    void cleanUnboundDevices(LocalDateTime cutoffTime);

    /**
     * 检查设备是否已经绑定
     * @param deviceId 设备ID
     * @return 绑定的用户ID，若未绑定则为NULL
     */
    Long checkDeviceBinding(String deviceId);

    boolean wakeUpDevice(String deviceId);

    boolean deviceExists(String deviceId);
}





package org.yuxun.x.nexusx.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Param;
import org.yuxun.x.nexusx.Entity.Devices;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface DevicesMapper extends BaseMapper<Devices> {

    /**
     * 获取设备基础信息
     * @param deviceId 设备唯一标识
     * @return 设备信息
     */
    @Select("SELECT deviceId, deviceName, status FROM devices WHERE deviceId = #{deviceId}")
    Devices getDeviceInfo(@Param("deviceId") String deviceId);

    /**
     * 获取设备状态
     * @param deviceId 设备唯一标识
     * @return 设备状态值
     */
    @Select("SELECT status FROM devices WHERE deviceId = #{deviceId}")
    Integer getDeviceStatus(@Param("deviceId") String deviceId);

    /**
     * 获取设备的最后登录时间
     * @param deviceId 设备唯一标识
     * @return 设备的最后登录时间
     */
    @Select("SELECT lastOnlineTime FROM devices WHERE deviceId = #{deviceId}")
    LocalDateTime getLastLoginTime(@Param("deviceId") String deviceId);

    /**
     * 删除无主设备（未绑定设备）
     * @param cutoffTime 截止时间
     * @return 删除的设备数量
     */
    @Delete("DELETE FROM devices WHERE userId = ${userId} AND devices.createdAt < #{cutoffTime}")
    int deleteUnboundDevices(@Param("cutoffTime") LocalDateTime cutoffTime, @Param("userId") String userId);

    /**
     * 检查设备是否已经绑定
     * @param deviceId 设备ID
     * @return 绑定状态，返回绑定的用户ID，如果没有绑定则为 NULL
     */
    @Select("SELECT userId FROM devices WHERE deviceId = #{deviceId}")
    Long checkDeviceBinding(@Param("deviceId") String deviceId);

    /**
     * 更新设备绑定信息
     * @param deviceId 设备ID
     * @param userId 用户ID
     * @return 更新结果
     */
    @Update("UPDATE devices SET userId = #{userId} WHERE deviceId = #{deviceId}")
    int bindDeviceToUser(@Param("deviceId") String deviceId, @Param("userId") Long userId);

    /**
     * 更新设备状态
     * @param deviceId 设备ID
     * @param status 状态值
     * @return 更新结果
     */
    @Update("UPDATE devices SET status = #{status} WHERE deviceId = #{deviceId}")
    int updateDeviceStatus(@Param("deviceId") String deviceId, @Param("status") int status);

    /**
     * 更新设备状态，心跳超时的设备更新为离线
     */
    @Update("UPDATE devices SET status = 'offline' WHERE lastHeartbeatTime < #{cutoffTime} AND status = 'online'")
    void updateDeviceStatusByHeartbeat(@Param("cutoffTime") LocalDateTime cutoffTime);

    // 获取心跳超时的设备
    @Select("SELECT deviceId,userId,deviceName,device_broadcast_IP,device_mac_address,status,deviceType,lastOnlineTime,lastHeartbeatTime,lastStatusUpdateTime,createdAt,updatedAt FROM devices WHERE lastHeartbeatTime = ${cutoffTime}")
    List<Devices> getDevicesByLastHeartbeatTimeBefore(@Param("cutoffTime") LocalDateTime cutoffTime);
}

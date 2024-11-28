package org.yuxun.x.nexusx.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.yuxun.x.nexusx.Enmu.DeviceStatus;
import org.yuxun.x.nexusx.Entity.Devices;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@Schema(description = "设备信息请求传输对象")
public class DeviceInfoDTO {
    @Schema(description = "设备UUID", example = "101")
    private String deviceId;

    @Schema(description = "用户ID", example = "111")
    private String userId;

    @Schema(description = "设备类型", example = "PC/Mobile")
    private String deviceType;

    @Schema(description = "设备名称", example = "DESKTOP-OI9D65P")
    private String name;

    @Schema(description = "Mac地址", example = "00:14:22:01:23:45")
    private String deviceMacAddress;

    @Schema(description = "广播 IP 地址", example = "192.168.1.255")
    private String deviceBroadcastIP;

    @Schema(description = "设备状态", example = "离线，在线，休眠，注销")
    private DeviceStatus status;

    @Schema(description = "状态更新时间", example = "2002-11-11 12:22:03")
    private LocalDateTime lastStatusUpdateTime;

    @Schema(description = "上次在线时间", example = "2002-11-11 12:22:03")
    private LocalDateTime lastOnlineTime;

    @Schema(description = "上次心跳时间", example = "2002-11-11 12:22:03")
    private LocalDateTime lastHeartbeatTime;

    @Schema(description = "创建时间", example = "2002-11-11 12:22:03")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", example = "2002-11-11 12:22:03")
    private LocalDateTime updateTime;

    public static Devices convertToEntity(DeviceInfoDTO dto) {
        Devices entity = new Devices();
        entity.setDeviceId(dto.getDeviceId());
        entity.setUserId(Long.valueOf(dto.getUserId()));
        entity.setDeviceType(dto.getDeviceType());
        entity.setDeviceName(dto.getName());
        entity.setDeviceMacAddress(dto.getDeviceMacAddress());
        entity.setDeviceBroadcastIP(dto.getDeviceBroadcastIP());
        entity.setDeviceStatus(dto.getStatus());
        entity.setLastStatusUpdateTime(dto.getLastStatusUpdateTime());
        entity.setLastOnlineTime(dto.getLastOnlineTime());
        entity.setLastHeartbeatTime(dto.getLastHeartbeatTime());
        entity.setCreateTime(dto.getCreateTime());
        entity.setUpdateTime(dto.getUpdateTime());
        return entity;
    }
}

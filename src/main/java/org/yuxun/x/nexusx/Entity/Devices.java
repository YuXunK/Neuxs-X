package org.yuxun.x.nexusx.Entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.yuxun.x.nexusx.Enmu.DeviceStatus;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@TableName("devices")
public class Devices {
    private String deviceId;
    private Long userId;
    private String deviceName;
    private String deviceType;
    private String deviceMacAddress;
    private String deviceBroadcastIP;
    private DeviceStatus deviceStatus;
    private LocalDateTime lastOnlineTime;
    private LocalDateTime lastHeartbeatTime;
    private LocalDateTime lastStatusUpdateTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

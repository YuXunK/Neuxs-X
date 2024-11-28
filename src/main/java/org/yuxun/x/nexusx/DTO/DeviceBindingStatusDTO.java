package org.yuxun.x.nexusx.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.yuxun.x.nexusx.Enmu.DeviceStatus;

@Data
@Getter
@Setter
@Schema(description = "设备绑定信息传输对象")
public class DeviceBindingStatusDTO {
    @Schema(description = "设备UUID", example = "101")
    private String deviceId;
    @Schema(description = "用户ID", example = "01")
    private Long userId;
    @Schema(description = "用户名", example = "奶奶滴")
    private String userName;
    @Schema(description = "设备状态", example = "离线，在线，休眠，注销--好逑难写")
    private DeviceStatus status;
}

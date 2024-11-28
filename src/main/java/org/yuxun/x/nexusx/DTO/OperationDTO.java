package org.yuxun.x.nexusx.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "任务信息传输对象")
public class OperationDTO {
    @Schema(description = "操作ID", example = "101")
    private Long log_id;

    @Schema(description = "操作用户ID", example = "1222")
    private Long user_id;

    @Schema(description = "操作设备ID", example = "12223333")
    private Long device_id;

    @Schema(description = "任务描述", example = "编写Nexus-X项目的API文档")
    private String operation;

    @Schema(description = "操作类型", example = "CREATE-CANCEL-UPDATE-DELETE")
    private String operation_type;

    @Schema(description = "操作时的ip地址", example = "127.0.0.1")
    private String ip_address;

    @Schema(description = "任务状态", example = "进行中")
    private Byte status;

    @Schema(description = "任务创建时间", example = "2024-11-13T10:00:00")
    private LocalDateTime createdTime;

}

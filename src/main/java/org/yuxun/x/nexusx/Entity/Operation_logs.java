package org.yuxun.x.nexusx.Entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@TableName("operation_logs")
public class Operation_logs {
    private Long logId;
    private Long userId;
    private Long deviceId;
    private String operation;
    private String ipAddress;
    private Byte operationStatus;
    private LocalDateTime createTime;
}

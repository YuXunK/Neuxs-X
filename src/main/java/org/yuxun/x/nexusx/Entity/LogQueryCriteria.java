package org.yuxun.x.nexusx.Entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class LogQueryCriteria {

    private Long userId;           // 用户ID
    private Long deviceId;         // 设备ID
    private String operation;      // 操作描述（模糊匹配）
    private String ipAddress;      // 操作IP地址
    private Integer status;        // 操作状态：0-失败，1-成功
    private LocalDateTime startTime; // 查询起始时间
    private LocalDateTime endTime;   // 查询结束时间
}


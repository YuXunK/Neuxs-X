package org.yuxun.x.nexusx.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.yuxun.x.nexusx.DTO.OperationDTO;
import org.yuxun.x.nexusx.Entity.LogQueryCriteria;
import org.yuxun.x.nexusx.Entity.Operation_logs;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface OperationService extends IService<Operation_logs>{

    // 1. 设备连接相关
    void establishConnection(OperationDTO operationDTO);
    void disconnectConnection(OperationDTO operationDTO, boolean isManual);

    // 2. 操作日志记录
    void recordLog(OperationDTO operationDTO);

    // 3. 同步功能
    void startScreenStreaming(OperationDTO operationDTO);
    void sendUserCommand(OperationDTO operationDTO, String command);
    void handleReconnect(Long deviceId);

    // 4. 日志管理
    List<Operation_logs> queryLogs(LogQueryCriteria criteria);
    Map<String, Object> analyzeLogs(LocalDateTime start, LocalDateTime end);
    void clearOldLogs(LocalDateTime cutoffTime);

    // 5. 状态维护
    void checkDeviceStatus();
}


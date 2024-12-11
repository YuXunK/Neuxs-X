package org.yuxun.x.nexusx.Service;

import org.yuxun.x.nexusx.DTO.OperationDTO;
import org.yuxun.x.nexusx.Entity.Operation_logs;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface AllLogService {
    void recordOperationLog(OperationDTO operationDTO);
    List<Operation_logs> getOperationLogs(String deviceId, String userId, int pageNum, int pageSize);
    void cleanOldLogs();
    List<Operation_logs> getOperationLogsByUserId(String userId);
    Map<String, Object> analyzeLogs(String userId,LocalDateTime start, LocalDateTime end);
    int clearOldLogs(Long userId, LocalDateTime cutoffDate);
}

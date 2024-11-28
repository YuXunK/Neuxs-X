package org.yuxun.x.nexusx.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yuxun.x.nexusx.Entity.LogQueryCriteria;
import org.yuxun.x.nexusx.Entity.Operation_logs;
import org.yuxun.x.nexusx.Service.OperationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/operations")
public class OperationController {

    private final OperationService operationService;

    @Autowired
    public OperationController(OperationService operationService) {
        this.operationService = operationService;
    }

    /**
     * 查询操作日志
     * @param criteria 查询条件
     * @return 操作日志列表
     */
    @PostMapping("/query-logs")
    public ResponseEntity<List<Operation_logs>> queryLogs(@RequestBody LogQueryCriteria criteria) {
        List<Operation_logs> logs = operationService.queryLogs(criteria);
        return ResponseEntity.ok(logs);
    }

    /**
     * 分析操作日志
     * @param start 起始时间
     * @param end 结束时间
     * @return 日志分析结果
     */
    @PostMapping("/analyze-logs")
    public ResponseEntity<Map<String, Object>> analyzeLogs(@RequestParam LocalDateTime start,
                                                           @RequestParam LocalDateTime end) {
        Map<String, Object> analysisResult = operationService.analyzeLogs(start, end);
        return ResponseEntity.ok(analysisResult);
    }

    /**
     * 清理旧的操作日志
     * @param cutoffTime 截止时间
     * @return 操作结果
     */
    @PostMapping("/clear-old-logs")
    public ResponseEntity<String> clearOldLogs(@RequestParam LocalDateTime cutoffTime) {
        try {
            operationService.clearOldLogs(cutoffTime);
            return ResponseEntity.ok("旧日志已清理");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("清理旧日志失败");
        }
    }
}

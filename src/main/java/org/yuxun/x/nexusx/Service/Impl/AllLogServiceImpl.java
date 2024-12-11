package org.yuxun.x.nexusx.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.yuxun.x.nexusx.DTO.OperationDTO;
import org.yuxun.x.nexusx.Entity.Operation_logs;
import org.yuxun.x.nexusx.Mapper.OperationLogsMapper;
import org.yuxun.x.nexusx.Mapper.OperationMapper;
import org.yuxun.x.nexusx.Service.AllLogService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class AllLogServiceImpl extends ServiceImpl<OperationMapper, Operation_logs> implements AllLogService {

    private final OperationLogsMapper operationLogsMapper;

    @Autowired
    public AllLogServiceImpl(OperationLogsMapper operationLogsMapper) {
        this.operationLogsMapper = operationLogsMapper;
    }
    /**
     * 记录操作日志
     */
    @Override
    public void recordOperationLog(OperationDTO operationDTO) {
        Operation_logs log = new Operation_logs();
        log.setUserId(operationDTO.getUser_id());
        log.setDeviceId(operationDTO.getDevice_id());
        log.setOperation(operationDTO.getOperation());
        log.setIpAddress(operationDTO.getIp_address());
        log.setOperationStatus(operationDTO.getStatus());
        log.setCreateTime(LocalDateTime.now());
        this.save(log);
    }

    /**
     * 分页查询操作日志--对应设备
     */
    @Override
    public List<Operation_logs> getOperationLogs(String deviceId, String userId, int pageNum, int pageSize) {
        Page<Operation_logs> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Operation_logs> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("device_id", deviceId);
        queryWrapper.eq("user_id", userId);
        return operationLogsMapper.selectPage(page, queryWrapper).getRecords();
    }

    /**
     * 定期清理操作日志
     */
    @Override
    @Scheduled(cron = "0 0 0 * * ?")  // 每天零点执行清理任务
    public void cleanOldLogs() {
        // 清理超过一个月的日志
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        operationLogsMapper.delete(new QueryWrapper<Operation_logs>().lt("created_at", oneMonthAgo));
    }

    /**
     * @author yuxun
     * 查询对应用户全部日志信息
     */
    @Override
    public List<Operation_logs> getOperationLogsByUserId(String userId) {
        QueryWrapper<Operation_logs> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return operationLogsMapper.selectList(queryWrapper);
    }

    /**
     * @author yuxun
     * 前端获取时间区间，再去数据库根据时间区间去查询对应操作数据，用于统计日活
     */
    public Map<String, Object> analyzeLogs(String userId,LocalDateTime start, LocalDateTime end) {
        // 初始化结果 Map
        Map<String, Object> analysisResult = new HashMap<>();

        // 获取每一天的操作次数
        List<Operation_logs> logs = operationLogsMapper.selectList(
                new QueryWrapper<Operation_logs>()
                        .eq("user_id", userId)
                        .ge("created_at", start)
                        .le("created_at", end)
        );

        // 统计每日的操作次数
        Map<LocalDate, Long> dailyOperationCount = logs.stream()
                .collect(Collectors.groupingBy(
                        log -> log.getCreateTime().toLocalDate(),
                        Collectors.counting()
                ));

        // 补充没有日志的日期，保证图表数据完整
        LocalDate current = start.toLocalDate();
        while (!current.isAfter(end.toLocalDate())) {
            dailyOperationCount.putIfAbsent(current, 0L);
            current = current.plusDays(1);
        }

        // 排序结果
        Map<LocalDate, Long> sortedResult = new TreeMap<>(dailyOperationCount);

        // 转换为前端需要的格式
        analysisResult.put("dates", sortedResult.keySet());
        analysisResult.put("counts", sortedResult.values());

        return analysisResult;
    }

    /**
     * @author yuxun
     * 定期清理日志数量
     */
    @Override
    public int clearOldLogs(Long userId, LocalDateTime cutoffDate) {
        // 使用 QueryWrapper 构建条件清理日志
        QueryWrapper<Operation_logs> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .lt("created_at", cutoffDate);

        // 调用 Mapper 执行删除操作
        return operationLogsMapper.delete(queryWrapper);
    }

}


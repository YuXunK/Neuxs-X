package org.yuxun.x.nexusx.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.yuxun.x.nexusx.DTO.OperationDTO;
import org.yuxun.x.nexusx.Entity.LogQueryCriteria;
import org.yuxun.x.nexusx.Entity.Operation_logs;
import org.yuxun.x.nexusx.Mapper.OperationMapper;
import org.yuxun.x.nexusx.Service.OperationService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OperationServiceImpl extends ServiceImpl<OperationMapper, Operation_logs> implements OperationService {

    @Override
    public void establishConnection(OperationDTO operationDTO) {
        // 使用 DTO 记录日志
        recordLog(operationDTO);
        System.out.println("设备连接已建立: User ID = " + operationDTO.getUser_id() + ", Device ID = " + operationDTO.getDevice_id());
    }

    @Override
    public void disconnectConnection(OperationDTO operationDTO, boolean isManual) {
        String operation = isManual ? "主动断开连接" : "网络中断";
        operationDTO.setOperation(operation);
        recordLog(operationDTO);
        System.out.println("设备连接已断开: Device ID = " + operationDTO.getDevice_id());
    }

    @Override
    public void recordLog(OperationDTO operationDTO) {
        Operation_logs log = new Operation_logs();
        log.setUserId(operationDTO.getUser_id());
        log.setDeviceId(operationDTO.getDevice_id());
        log.setOperation(operationDTO.getOperation());
        log.setIpAddress(operationDTO.getIp_address());
        log.setOperationStatus(operationDTO.getStatus());
        log.setCreateTime(LocalDateTime.now());
        this.save(log);
    }

    @Override
    public void startScreenStreaming(OperationDTO operationDTO) {
        operationDTO.setOperation("开始屏幕流传输");
        recordLog(operationDTO);
        System.out.println("开始屏幕流传输: User ID = " + operationDTO.getUser_id() + ", Device ID = " + operationDTO.getDevice_id());
    }


    @Override
    public void sendUserCommand(OperationDTO operationDTO, String command) {
        operationDTO.setOperation("发送用户命令: " + command);
        recordLog(operationDTO);
        System.out.println("发送命令: Command = " + command + ", Device ID = " + operationDTO.getDevice_id());
    }

    @Override
    public void handleReconnect(Long deviceId) {
        // 这里我们不需要 OperationDTO，直接处理设备 ID
        OperationDTO operationDTO = new OperationDTO();
        operationDTO.setDevice_id(deviceId);
        operationDTO.setOperation("重新连接设备");
        operationDTO.setIp_address("127.0.0.1");
        operationDTO.setStatus((byte) 1);
        recordLog(operationDTO);
        System.out.println("设备重新连接: Device ID = " + deviceId);
    }

    @Override
    public List<Operation_logs> queryLogs(LogQueryCriteria criteria) {
        return baseMapper.queryLogs(criteria.getUserId(),
                criteria.getDeviceId(),
                criteria.getStartTime(),
                criteria.getEndTime());
    }

    @Override
    public Map<String, Object> analyzeLogs(LocalDateTime start, LocalDateTime end) {
        Map<String, Object> analysisResult = new HashMap<>();

        // 1. 按状态统计成功和失败的数量
        Map<String, Long> statusCounts = baseMapper.countByStatus(start, end);
        analysisResult.put("statusCounts", statusCounts);

        // 2. 按操作名称分组统计操作数量
        List<Map<String, Object>> operationCounts = baseMapper.groupByOperation(start, end);
        analysisResult.put("operationCounts", operationCounts);

        return analysisResult;
    }


    @Override
    public void clearOldLogs(LocalDateTime cutoffTime) {
        int rowsDeleted = baseMapper.clearOldLogs(cutoffTime);
        System.out.println("清理日志成功，删除了 " + rowsDeleted + " 条记录");
    }

    @Override
    public void checkDeviceStatus() {
        System.out.println("检查设备状态...");
        // 模拟心跳包逻辑
    }
}


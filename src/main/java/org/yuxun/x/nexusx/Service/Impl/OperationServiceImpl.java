package org.yuxun.x.nexusx.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.yuxun.x.nexusx.DTO.OperationDTO;
import org.yuxun.x.nexusx.Entity.Operation_logs;
import org.yuxun.x.nexusx.Mapper.OperationMapper;
import org.yuxun.x.nexusx.Service.OperationService;

import java.time.LocalDateTime;

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
    public void checkDeviceStatus() {
        System.out.println("检查设备状态...");
        // 模拟心跳包逻辑
    }
}


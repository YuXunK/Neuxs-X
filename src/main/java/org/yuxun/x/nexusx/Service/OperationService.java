package org.yuxun.x.nexusx.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.yuxun.x.nexusx.DTO.OperationDTO;
import org.yuxun.x.nexusx.Entity.Operation_logs;

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

    // 5. 状态维护
    void checkDeviceStatus();
}


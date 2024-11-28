package org.yuxun.x.nexusx.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yuxun.x.nexusx.DTO.OperationDTO;
import org.yuxun.x.nexusx.Service.OperationService;


@RestController
@RequestMapping("/api/sync")
public class SyncController {

    private final OperationService operationService;

    @Autowired
    public SyncController(OperationService operationService) {
        this.operationService = operationService;
    }

    /**
     * 建立设备连接
     * @param operationDTO 操作数据
     * @return 操作结果
     */
    @PostMapping("/establish-connection")
    public ResponseEntity<String> establishConnection(@RequestBody OperationDTO operationDTO) {
        try {
            operationService.establishConnection(operationDTO);
            return ResponseEntity.ok("设备连接已建立");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("设备连接失败");
        }
    }

    /**
     * 断开设备连接
     * @param operationDTO 操作数据
     * @param isManual 是否是主动断开
     * @return 操作结果
     */
    @PostMapping("/disconnect-connection")
    public ResponseEntity<String> disconnectConnection(@RequestBody OperationDTO operationDTO,
                                                       @RequestParam boolean isManual) {
        try {
            operationService.disconnectConnection(operationDTO, isManual);
            return ResponseEntity.ok("设备连接已断开");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("设备连接断开失败");
        }
    }

    /**
     * 开始屏幕流传输
     * @param operationDTO 操作数据
     * @return 操作结果
     */
    @PostMapping("/start-screen-streaming")
    public ResponseEntity<String> startScreenStreaming(@RequestBody OperationDTO operationDTO) {
        try {
            operationService.startScreenStreaming(operationDTO);
            return ResponseEntity.ok("屏幕流传输已启动");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("屏幕流传输启动失败");
        }
    }

    /**
     * 发送用户命令
     * @param operationDTO 操作数据
     * @param command 用户命令
     * @return 操作结果
     */
    @PostMapping("/send-user-command")
    public ResponseEntity<String> sendUserCommand(@RequestBody OperationDTO operationDTO,
                                                  @RequestParam String command) {
        try {
            operationService.sendUserCommand(operationDTO, command);
            return ResponseEntity.ok("命令已发送");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("命令发送失败");
        }
    }

    /**
     * 处理设备重新连接
     * @param deviceId 设备ID
     * @return 操作结果
     */
    @PostMapping("/reconnect")
    public ResponseEntity<String> reconnectDevice(@RequestParam Long deviceId) {
        try {
            operationService.handleReconnect(deviceId);
            return ResponseEntity.ok("设备重新连接");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("设备重新连接失败");
        }
    }

    /**
     * 检查设备状态
     * @return 操作结果
     */
    @PostMapping("/check-device-status")
    public ResponseEntity<String> checkDeviceStatus() {
        try {
            operationService.checkDeviceStatus();
            return ResponseEntity.ok("设备状态检查完成");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("设备状态检查失败");
        }
    }
}

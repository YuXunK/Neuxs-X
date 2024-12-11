package org.yuxun.x.nexusx.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yuxun.x.nexusx.DTO.DeviceInfoDTO;
import org.yuxun.x.nexusx.Enmu.DeviceStatus;
import org.yuxun.x.nexusx.Service.DevicesService;
import org.yuxun.x.nexusx.Service.UserService;
import org.yuxun.x.nexusx.Utils.AESUtil;

@RestController
@RequestMapping("/api/devices")
public class DevicesController {

    private final DevicesService devicesService;
    private final UserService userService;
    @Autowired
    public DevicesController(DevicesService devicesService, UserService userService)
    {
        this.devicesService = devicesService;
        this.userService = userService;
    }

    @PostMapping("/{userId}/register")
    public ResponseEntity<String> registerDevice(@RequestBody DeviceInfoDTO device) {
        try {
            String encryptedDeviceId = AESUtil.encrypt(device.getDeviceId());
            boolean exists = devicesService.deviceExists(encryptedDeviceId);
            if (exists) {
                return ResponseEntity.badRequest().body("device already exists");
            }

            DeviceInfoDTO deviceInfoDTO = new DeviceInfoDTO();
            deviceInfoDTO.setDeviceId(encryptedDeviceId);
            deviceInfoDTO.setUserId(device.getUserId());
            deviceInfoDTO.setDeviceType(device.getDeviceType());
            deviceInfoDTO.setName(device.getName());
            deviceInfoDTO.setDeviceBroadcastIP(device.getDeviceBroadcastIP());
            deviceInfoDTO.setDeviceMacAddress(device.getDeviceMacAddress());
            deviceInfoDTO.setStatus(DeviceStatus.fromValue(1));
            deviceInfoDTO.setLastOnlineTime(device.getLastOnlineTime());
            deviceInfoDTO.setLastHeartbeatTime(device.getLastHeartbeatTime());
            deviceInfoDTO.setLastStatusUpdateTime(device.getLastStatusUpdateTime());
            deviceInfoDTO.setCreateTime(device.getCreateTime());
            deviceInfoDTO.setUpdateTime(device.getUpdateTime());
            devicesService.registerDeviceInfo(deviceInfoDTO);

            //设备信息上传后与当前账号进行绑定
            userService.userConnectedToDevice(Long.valueOf(device.getUserId()),encryptedDeviceId);

            return ResponseEntity.ok("device registered successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    /**
     * 获取设备当前状态信息
     */
    @GetMapping("/{deviceId}/status")
    public ResponseEntity<String> getDeviceStatus(@PathVariable("deviceId") String deviceId) {
        DeviceStatus status = DeviceStatus.valueOf(devicesService.getDeviceStatus(deviceId));
        return ResponseEntity.ok(status.name());
    }

    /**
     * 向待机或睡眠PC设备发送Magic包来唤醒设备
     */
    @PostMapping("/{deviceId}/wake")
    public ResponseEntity<String> wakeDevice(@PathVariable("deviceId") String deviceId) {
        boolean success = devicesService.wakeUpDevice(deviceId);
        if (success) {
            return ResponseEntity.ok("唤醒Magic包发送成功");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("唤醒失败，设备不存在或是设备网络异常");
        }
    }

    /**
     * 设备进行解绑操作
     */
    @DeleteMapping("/{deviceId}/unbind")
    public ResponseEntity<Void> unbindDevice(@RequestParam Long userId, @PathVariable Long deviceId) {
        devicesService.unbindDevice(userId, String.valueOf(deviceId));
        return ResponseEntity.noContent().build();
    }
}

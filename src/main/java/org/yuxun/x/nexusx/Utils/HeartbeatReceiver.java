package org.yuxun.x.nexusx.Utils;

import org.yuxun.x.nexusx.Service.Impl.HeartbeatService;

public class HeartbeatReceiver {
    private HeartbeatService heartbeatService;
    public HeartbeatReceiver(String deviceId) {
        heartbeatService.updateDeviceStatus(deviceId,"online");
    }
}

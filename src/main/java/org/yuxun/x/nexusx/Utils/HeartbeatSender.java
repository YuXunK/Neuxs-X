package org.yuxun.x.nexusx.Utils;

import java.time.LocalDateTime;

public class HeartbeatSender implements Runnable {
    private static final int HEARTBEAT_INTERVAL = 30000; // 每30秒发送一次心跳包
    private String deviceId;
    private String serverUrl; // 服务器URL

    public HeartbeatSender(String deviceId, String serverUrl) {
        this.deviceId = deviceId;
        this.serverUrl = serverUrl;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // 构造心跳包消息
                String heartbeatMessage = buildHeartbeatMessage(deviceId);

                // 发送心跳包到服务器
                sendHeartbeatToServer(heartbeatMessage);

                // 等待下次心跳包发送
                Thread.sleep(HEARTBEAT_INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private String buildHeartbeatMessage(String deviceId) {
        // 构造心跳包内容
        return "{ \"deviceId\": \"" + deviceId + "\", \"timestamp\": \"" + LocalDateTime.now() + "\", \"type\": \"heartbeat\" }";
    }

    private void sendHeartbeatToServer(String message) {
        // 模拟发送 HTTP 请求（实际应用中可以用 WebSocket 或 REST API）
        System.out.println("Sending heartbeat: " + message);
        // 使用 HTTP 请求发送消息到服务器
    }
}

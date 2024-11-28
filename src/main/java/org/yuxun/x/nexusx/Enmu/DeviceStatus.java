package org.yuxun.x.nexusx.Enmu;

import lombok.Getter;

@Getter
public enum DeviceStatus {
    OFFLINE(0, "离线"),
    ONLINE(1, "在线"),
    SLEEP(2, "休眠");

    private final int value;
    @Getter
    private final String description; // 添加描述字段

    DeviceStatus(int value, String description) {
        this.value = value;
        this.description = description;
    }

    // 根据数据库中的数值转换为枚举
    public static DeviceStatus fromValue(int value) {
        for (DeviceStatus status : values()) {
            if (status.value == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid DeviceStatus value: " + value);
    }

}


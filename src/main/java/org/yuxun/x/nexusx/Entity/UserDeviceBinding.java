package org.yuxun.x.nexusx.Entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class UserDeviceBinding {
    private byte id;
    private long userId;
    private byte deviceId;
    private LocalDateTime bindTime;
}

package org.yuxun.x.nexusx.Entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@TableName("permissions")
public class Permission {
    private Long permission_id;
    private String permission_name;
    private String permissionDesc;
    private LocalDateTime permissionCreateTime;
}

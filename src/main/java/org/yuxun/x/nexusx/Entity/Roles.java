package org.yuxun.x.nexusx.Entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@TableName("roles")
public class Roles {
    private Long role_id;
    private String role_name;
    private String roleDesc;
    private LocalDateTime createTime;
}

package org.yuxun.x.nexusx.Entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@TableName("role_permissions")
public class Role_Permissions {
        private int role_id;
        private int permission_id;
}

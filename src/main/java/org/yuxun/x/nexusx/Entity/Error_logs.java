package org.yuxun.x.nexusx.Entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@TableName("error_logs")
public class Error_logs {
    private Long log_id;
    private String error_type;
    private String error_message;
    private String error_trace;
    private LocalDateTime created_timestamp;
}

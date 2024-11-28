package org.yuxun.x.nexusx.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "标准API响应对象")
public class ApiResponseDTO<T> {

    @Schema(description = "响应状态码", example = "200")
    private int statusCode;

    @Schema(description = "响应消息", example = "请求成功")
    private String message;

    @Schema(description = "响应数据")
    private T data;

}


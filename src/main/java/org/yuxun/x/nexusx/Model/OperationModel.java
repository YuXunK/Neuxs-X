package org.yuxun.x.nexusx.Model;

import lombok.Getter;
import lombok.Setter;
import org.yuxun.x.nexusx.DTO.OperationDTO;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OperationModel {
    private Long log_id;
    private Long user_id;
    private Long device_id;
    private String operation;
    private String operation_type;
    private String ip_address;
    private Byte operation_status;
    private LocalDateTime create_time;

    public OperationModel(Long log_id,Long user_id,Long device_id,String operation,String operation_type,Byte operation_status,LocalDateTime create_time) {
        this.log_id = log_id;
        this.user_id = user_id;
        this.device_id = device_id;
        this.operation = operation;
        this.operation_type = operation_type;
        this.operation_status = operation_status;
        this.create_time = create_time;
    }

    public OperationDTO toDTO() {
        OperationDTO operationDTO = new OperationDTO();
        operationDTO.setLog_id(this.log_id);
        operationDTO.setUser_id(this.user_id);
        operationDTO.setDevice_id(this.device_id);
        operationDTO.setOperation(this.operation);
        operationDTO.setOperation_type(this.operation_type);
        operationDTO.setCreatedTime(this.create_time);
        return operationDTO;
    }

    public boolean isValidOperation() {
        return List.of("CREATE","CANCEL","UPDATE","DELETE").contains(this.operation);
    }

    @Override
    public String toString() {
        return "OperationModel{" +
                "log_id=" + log_id +
                ", user_id=" + user_id +
                ", device_id=" + device_id +
                ", operation='" + operation + '\'' +
                ", ip_address='" + ip_address + '\'' +
                ", operation_status=" + operation_status +
                ", create_time=" + create_time +
                '}';
    }

}

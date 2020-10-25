package cn.ilubov.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class FlowStartVO {

    @ApiModelProperty("流程实例ID")
    private String procInstId;
}

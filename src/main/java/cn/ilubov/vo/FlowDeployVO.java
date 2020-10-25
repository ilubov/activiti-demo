package cn.ilubov.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class FlowDeployVO {

    @ApiModelProperty("流程部署ID")
    private String deployId;

    @ApiModelProperty("流程名称")
    private String deployName;
}
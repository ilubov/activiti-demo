package cn.ilubov.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 流程部署
 *
 * @author ilubov
 * @date 2020/10/25
 */
@Data
@ApiModel
public class FlowDeployDTO {

    @ApiModelProperty("流程部署ID")
    private String deploymentId;

    @ApiModelProperty("流程文件路径")
    private String bpmnFilePath;

    @ApiModelProperty("相关联的")
    private boolean cascade;
}
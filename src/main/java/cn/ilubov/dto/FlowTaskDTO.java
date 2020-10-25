package cn.ilubov.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 流程任务
 *
 * @author ilubov
 * @date 2020/10/25
 */
@Data
@ApiModel
public class FlowTaskDTO {

    @ApiModelProperty("流程KEY")
    private List<String> procKeys;

    @ApiModelProperty("流程实例Id")
    private String procInstId;

    @ApiModelProperty("任务定义key")
    private String taskDefKey;

    @ApiModelProperty("任务执行Id")
    private String executionId;

    @ApiModelProperty("变量名称")
    private String varName;

    @ApiModelProperty("用户Id")
    private Long userId;

    @ApiModelProperty("开始记录数")
    private int offSet;

    @ApiModelProperty("每页记录数")
    private int pageSize;
}

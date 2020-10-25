package cn.ilubov.dto;

import com.google.common.collect.Maps;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * 流程完成
 *
 * @author ilubov
 * @date 2020/10/25
 */
@Data
@ApiModel
public class TaskCompleteDTO {

    @ApiModelProperty("任务ID")
    private String taskId;

    @ApiModelProperty("选择的操作")
    private String seqFlowId;

    @ApiModelProperty("意见")
    private String comment;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("流程变量")
    private Map<String, Object> variable = Maps.newHashMap();

    @ApiModelProperty("审批人点击的按钮")
    private String btnName;

    @ApiModelProperty("工单ID")
    private String workId;

    @ApiModelProperty("流程KEY")
    private String procDefKey;
}

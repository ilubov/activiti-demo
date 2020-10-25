package cn.ilubov.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel
public class FlowCommentVO {

    @ApiModelProperty("工作流实例ID")
    private String procInstId;

    @ApiModelProperty("任务ID")
    private String taskId;

    @ApiModelProperty("任务定义Key")
    private String taskDefKey;

    @ApiModelProperty("任务名称")
    private String taskName;

    @ApiModelProperty("任务到达时间")
    private Date taskStartTime;

    @ApiModelProperty("任务完成时间（审批时间）")
    private Date taskFinishTime;

    @ApiModelProperty("任务滞留时长，格式：xx天 xx小时 xx分钟 xx秒")
    private String stayTime;

    @ApiModelProperty("审批意见")
    private String comment;

    @ApiModelProperty("操作人ID")
    private String operaUserId;
}

package cn.ilubov.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel
public class FlowTaskVO {

    @ApiModelProperty("实例名称")
    private String procInstName;

    @ApiModelProperty("实例ID")
    private String procInstId;

    @ApiModelProperty("任务名称")
    private String taskName;

    @ApiModelProperty("任务ID")
    private String taskId;

    @ApiModelProperty("办理路径")
    private String detailUrl;

    @ApiModelProperty("任务滞留时间 格式：xx天 xx小时 xx分钟 xx秒")
    private String stayTime;

    @ApiModelProperty("任务到达时间")
    private Date startTime;

    @ApiModelProperty("")
    private String taskDefKey;

    @ApiModelProperty("")
    private String executionId;
}

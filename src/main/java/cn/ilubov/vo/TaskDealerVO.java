package cn.ilubov.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel
public class TaskDealerVO {

    @ApiModelProperty("任务ID")
    private String taskId;

    @ApiModelProperty("任务名称")
    private String taskName;

    @ApiModelProperty("用户IDs")
    private List<String> userIds;

    @ApiModelProperty("用户信息")
    private List<String> userInfos;
}

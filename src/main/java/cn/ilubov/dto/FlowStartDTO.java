package cn.ilubov.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * 流程启动
 *
 * @author ilubov
 * @date 2020/10/25
 */
@Data
@ApiModel
public class FlowStartDTO {

    @ApiModelProperty("流程标识（必需）")
    private String procKey;

    @ApiModelProperty("流程业务标题（必需，待办的标题）")
    private String businessTitle;

    @ApiModelProperty("业务流程类型（必需，可以是业务表名或业务实体名）")
    private String businessType;

    @ApiModelProperty("业务表或实体的主键（必需）")
    private String businessId;

    @ApiModelProperty("详情URL（必需，待办的链接地址）")
    private String detailUrl;

    @ApiModelProperty("申请人ID（必需，一般是当前用户userId）")
    private Long applyId;

    @ApiModelProperty("营业厅")
    private Long businHall;

    @ApiModelProperty("机房")
    private Long egroom;

    @ApiModelProperty("流程变量")
    private Map<String, Object> variable;

    @ApiModelProperty("审批人点击的按钮")
    private String btnName;

    @ApiModelProperty("意见")
    private String comment;
}

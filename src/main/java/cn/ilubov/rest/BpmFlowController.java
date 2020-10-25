package cn.ilubov.rest;

import cn.ilubov.dto.FlowStartDTO;
import cn.ilubov.dto.FlowTaskDTO;
import cn.ilubov.dto.TaskCompleteDTO;
import cn.ilubov.comps.R;
import cn.ilubov.vo.FlowCommentVO;
import cn.ilubov.vo.FlowStartVO;
import cn.ilubov.vo.FlowTaskVO;
import cn.ilubov.vo.TaskDealerVO;
import cn.ilubov.service.BpmBusinService;
import cn.ilubov.service.BpmTaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 流程操作类，流程的启动、执行、查询、终止等
 *
 * @author ilubov
 * @date 2020/10/25
 */
@Api(tags = {"流程操作类，流程的启动、执行、查询、终止等"})
@RestController
@RequestMapping("/bpm")
public class BpmFlowController {

    @Autowired
    BpmBusinService bpmBusinService;

    @Autowired
    BpmTaskService bpmTaskService;

    /**
     * 获取代办任务
     *
     * @param req        参数
     * @param procKeys   流程定义ID，数组【必填】
     * @param userId     用户ID【必填】
     * @param procInstId 流程实例ID
     * @param taskDefKey 任务定义Key
     * @return
     */
    @ApiOperation("获取代办任务")
    @RequestMapping("/todoTasks")
    public R<List<FlowTaskVO>> todoTasks(@RequestBody FlowTaskDTO req) {
        List<FlowTaskVO> tasks = bpmTaskService.todoTasks(req);
        return R.<List<FlowTaskVO>>ok().data(tasks);
    }

    /**
     * 根据用户ID查询代办任务
     *
     * @param req      参数
     * @param userId   用户ID【必填】
     * @param procKeys 流程定义Key
     * @return
     */
    @ApiOperation("根据用户ID查询代办任务")
    @RequestMapping("/taskByUser")
    public R<List<FlowTaskVO>> taskByUser(@RequestBody FlowTaskDTO req) {
        List<FlowTaskVO> tasks = bpmTaskService.taskByUser(req.getUserId(), req.getProcKeys());
        return R.<List<FlowTaskVO>>ok().data(tasks);
    }

    /**
     * 根据流程实例ID查询当前任务
     *
     * @param req        参数
     * @param UserId     用户Id           【必填】
     * @param ProcInstId 流程实例ID    【必填】
     * @return
     */
    @ApiOperation("根据流程实例ID查询当前任务")
    @RequestMapping("/getCurrentTask")
    public R<FlowTaskVO> getCurrentTask(@RequestBody FlowTaskDTO req) {
        FlowTaskVO task = bpmTaskService.getCurrentTask(req.getProcInstId(), req.getUserId());
        return R.<FlowTaskVO>ok().data(task);
    }

    /**
     * 流程启动
     *
     * @param req           参数
     * @param applyId       申请人【必填】
     * @param procKey       流程定义ID【必填】
     * @param businessId    业务ID【必填】
     * @param businessTitle 业务title【必填】
     * @param variable      流程参数
     * @param detailUrl
     * @return
     */
    @ApiOperation("流程启动")
    @RequestMapping("/startFlow")
    public R<FlowStartVO> startFlow(@RequestBody FlowStartDTO req) {
        try {
            FlowStartVO startResp = bpmBusinService.flowStart(req);
            return R.<FlowStartVO>ok().data(startResp).desc("流程启动成功！");
        } catch (Exception ex) {
            return R.<FlowStartVO>error().desc("流程启动失败:" + ex.getMessage());
        }
    }

    /**
     * 完成任务
     *
     * @param req
     * @param taskId    任务ID【必填】
     * @param userId    用户ID【必填】
     * @param variable  流程变量
     * @param seqFlowId 路由选择变量
     * @return
     */
    @ApiOperation("完成任务")
    @RequestMapping("/taskComplete")
    public R<List<TaskDealerVO>> taskComplete(@RequestBody TaskCompleteDTO req) {
        try {
            List<TaskDealerVO> dealers = bpmBusinService.taskComplete(req);
            return R.<List<TaskDealerVO>>ok().data(dealers).desc("任务完成成功！");
        } catch (Exception ex) {
            return R.<List<TaskDealerVO>>error().desc("任务完成失败：" + ex.getMessage());
        }
    }

    /**
     * 删除流程实例
     *
     * @param req
     * @param procInstId 流程实例ID【必填】
     * @return
     */
    @ApiOperation("删除流程实例")
    @RequestMapping("/delProcInst")
    public R<Object> delProcInst(@RequestBody FlowTaskDTO req) {
        try {
            bpmBusinService.delProcInst(req.getProcInstId(), req.getUserId());
            return R.ok().desc("删除流程实例成功！");
        } catch (Exception ex) {
            return R.ok().desc("删除流程实例失败：" + ex.getMessage());
        }
    }

    /**
     * 自动完成当前任务
     *
     * @param req
     * @param procInstId 流程实例ID【必填】
     * @return
     */
    @ApiOperation("自动完成当前任务")
    @RequestMapping("/autoCompleteTask")
    public R<Object> autoCompleteTask(@RequestBody FlowTaskDTO req) {
        try {
            bpmBusinService.autoCompleteTask(req.getProcInstId());
            return R.ok().desc("自动完成任务成功！");
        } catch (Exception ex) {
            return R.ok().desc("自动完成任务失败：" + ex.getMessage());
        }
    }

    /**
     * 查询历史任务
     *
     * @param req        参数 分页查询
     * @param userId     用户ID【必填】
     * @param procKeys   流程定义Key【必填】
     * @param procInstId 流程实例ID
     * @param taskDefKey 任务定义Key
     * @param offSet     开始记录数【必填】
     * @param pageSize   每页记录数【必填】
     * @return
     */
    @ApiOperation("查询历史任务")
    @RequestMapping("/historyTasks")
    public R<List<FlowTaskVO>> historyTasks(@RequestBody FlowTaskDTO req) {
        List<FlowTaskVO> tasks = bpmTaskService.historyTasks(req);
        return R.<List<FlowTaskVO>>ok().data(tasks);
    }

    /**
     * 查询历史审批意见
     *
     * @param req        参数
     * @param procInstId 流程实例ID【必填】
     * @return
     */
    @ApiOperation("查询历史审批意见")
    @RequestMapping("/historyComments")
    public R<List<FlowCommentVO>> historyComments(@RequestBody FlowTaskDTO req) {
        List<FlowCommentVO> comments = bpmTaskService.historyComments(req);
        return R.<List<FlowCommentVO>>ok().data(comments);
    }

    /**
     * 查询任务下一步处理人
     *
     * @param req        参数
     * @param ProcInstId 流程实例ID【必填】
     * @return
     */
    @ApiOperation("查询任务下一步处理人")
    @RequestMapping("/taskNextDealer")
    public R<List<TaskDealerVO>> taskNextDealer(@RequestBody FlowTaskDTO req) {
        List<TaskDealerVO> dealers = bpmTaskService.taskNextDealer(req.getProcInstId());
        return R.<List<TaskDealerVO>>ok().data(dealers);
    }

    /**
     * 根据流程实例获取任务
     *
     * @param req        参数
     * @param procInstId 流程实例ID【参数】
     * @return
     */
    @ApiOperation("根据流程实例获取任务")
    @RequestMapping("/tasksByProcInstId")
    public R<List<FlowTaskVO>> tasksByProcInstId(@RequestBody FlowTaskDTO req) {
        List<FlowTaskVO> tasks = bpmTaskService.tasksByProcInstId(req.getProcInstId());
        return R.<List<FlowTaskVO>>ok().data(tasks);
    }

    /**
     * 获取流程变量
     *
     * @param req         参数
     * @param executionId 任务执行ID【必填】
     * @param varName     变量名称【必填】
     * @return
     */
    @ApiOperation("获取流程变量")
    @RequestMapping("/getFlowVariable")
    public R<Object> getFlowVariable(@RequestBody FlowTaskDTO req) {
        Object value = bpmTaskService.getFlowVariable(req.getExecutionId(), req.getVarName());
        return R.ok().data(value);
    }

    /**
     * 获取流程候选人或代理人，返回ID，多个用逗号分隔
     *
     * @param req        参数
     * @param procInstId 流程实例ID【必填】
     * @return
     */
    @ApiOperation("获取流程候选人或代理人，返回ID，多个用逗号分隔")
    @RequestMapping("/getCandidate")
    public R<String> getCandidate(@RequestBody FlowTaskDTO req) {
        String userIds = bpmTaskService.getCandidate(req.getProcInstId());
        return R.<String>ok().data(userIds);
    }
}

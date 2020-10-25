package cn.ilubov.service;

import cn.ilubov.dto.FlowStartDTO;
import cn.ilubov.dto.TaskCompleteDTO;
import cn.ilubov.vo.FlowStartVO;
import cn.ilubov.vo.TaskDealerVO;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工作流任务操作相关方法
 *
 * @author ilubov
 * @date 2020/10/25
 */
@Slf4j
@Service
public class BpmBusinService {

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    TaskService taskService;

    @Autowired
    BpmTaskService bpmTaskService;

    @Autowired
    RepositoryService repositoryService;

    /**
     * 启动流程，创建流程实例
     *
     * @param startReq      请求参数
     * @param applyId       申请人【必填】
     * @param procKey       流程定义ID【必填】
     * @param businessId    业务ID【必填】
     * @param businessTitle 业务title【必填】
     * @param variable      流程参数
     * @param detailUrl
     * @return
     */
    @Transactional
    public FlowStartVO flowStart(FlowStartDTO startReq) {
        FlowStartVO startResp = new FlowStartVO();
        Map<String, Object> variables = new HashMap<>();
        variables.put(FlowDefaultVars.VAR_APPLY_USER, startReq.getApplyId());
        if (startReq.getVariable() != null) {
            variables.putAll(startReq.getVariable());
        }
        ProcessInstance pi = runtimeService.startProcessInstanceByKey(startReq.getProcKey(), startReq.getBusinessId(), variables);
        runtimeService.setProcessInstanceName(pi.getProcessInstanceId(),
                startReq.getBusinessTitle());
        startResp.setProcInstId(pi.getProcessInstanceId());
        return startResp;
    }

    /**
     * 执行任务
     *
     * @param taskReq
     * @param taskId    任务ID【必填】
     * @param userId    用户ID【必填】
     * @param variable  流程变量
     * @param seqFlowId 路由选择变量
     * @return 下一步处理人
     */
    @Transactional
    public List<TaskDealerVO> taskComplete(TaskCompleteDTO taskReq) {
        if (taskReq == null) {
            throw new RuntimeException(FlowDefaultVars.ILLEGAL_MESSAGE + "：请求参数错误！");
        }
        if (StringUtils.isEmpty(taskReq.getTaskId())) {
            throw new RuntimeException("没有指定审批任务！");
        }
        Task task = taskService.createTaskQuery().taskId(taskReq.getTaskId()).singleResult();
        if (task == null) {
            throw new RuntimeException("指定的审批任务不存在！");
        }
        String userId = String.valueOf(taskReq.getUserId());
        if (task.getAssignee() == null) {
            // 当前任务未被领取，先领取任务
            taskService.claim(task.getId(), userId);
        } else if (!task.getAssignee().equals(userId)) {
            throw new RuntimeException("您无权处理当前审批任务！");
        }
        Map<String, Object> variables;
        if (taskReq.getVariable() == null) {
            variables = new HashMap<>();
        } else {
            variables = taskReq.getVariable();
        }
        if (StringUtils.isNotEmpty(taskReq.getSeqFlowId())) {
            variables.put(FlowDefaultVars.VAR_SELECTED_SEQFLOWID, taskReq.getSeqFlowId());
        }
        taskService.complete(task.getId(), variables);
        // 查下一步办理人，如为空则抛出异常
        List<TaskDealerVO> nextTasksAndUsers = bpmTaskService
                .taskNextDealer(task.getProcessInstanceId());
        if (nextTasksAndUsers != null && !nextTasksAndUsers.isEmpty()) {
            for (TaskDealerVO nextTasksAndUsersRspBo : nextTasksAndUsers) {
                List<String> nextUserIds = nextTasksAndUsersRspBo.getUserIds();
                if (nextUserIds == null || nextUserIds.isEmpty()) {
                    throw new RuntimeException("【" + nextTasksAndUsersRspBo.getTaskName() + "】没有找到对应的审批人，请联系系统管理员！");
                }
            }
        }
        return nextTasksAndUsers;
    }

    /**
     * 批量执行任务
     *
     * @param taskReqs
     */
    @Transactional
    public void multiTaskComplete(List<TaskCompleteDTO> taskReqs) {
        if (taskReqs != null && taskReqs.size() > 0) {
            for (TaskCompleteDTO completeReq : taskReqs) {
                taskComplete(completeReq);
            }
        }
    }

    /**
     * 根据流程实例删除流程
     *
     * @param procInstId 流程实例ID【必填】
     * @param userId
     */
    @Transactional
    public void delProcInst(String procInstId, Long userId) {
        if (StringUtils.isEmpty(procInstId)) {
            throw new RuntimeException("流程实例不能为空！");
        }
        runtimeService.deleteProcessInstance(procInstId, "删除流程:user=" + userId);
    }

    /**
     * 自动完成当前任务的下一任务
     *
     * @param procInstId 流程实例ID【必填】
     */
    public void autoCompleteTask(String procInstId) {
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(procInstId).list();
        if (tasks != null && tasks.size() > 0) {
            TaskCompleteDTO taskCompleteDTO;
            for (Task task : tasks) {
                List<IdentityLink> identityLinks = taskService.getIdentityLinksForTask(task.getId());
                Long userId = identityLinks.get(0).getUserId() != null
                        ? Long.valueOf(identityLinks.get(0).getUserId()) : null;
                taskCompleteDTO = new TaskCompleteDTO();
                taskCompleteDTO.setUserId(userId);
                taskCompleteDTO.setTaskId(task.getId());
                taskComplete(taskCompleteDTO);
            }
        }
    }
}

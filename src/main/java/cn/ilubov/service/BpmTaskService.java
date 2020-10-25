package cn.ilubov.service;

import cn.hutool.core.collection.CollUtil;
import cn.ilubov.dto.FlowTaskDTO;
import cn.ilubov.entity.Participant;
import cn.ilubov.mapper.ParticipantMapper;
import cn.ilubov.vo.FlowCommentVO;
import cn.ilubov.vo.FlowTaskVO;
import cn.ilubov.vo.TaskDealerVO;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 工作流任务查询相关方法
 *
 * @author ilubov
 * @date 2020/10/25
 */
@Slf4j
@Service
public class BpmTaskService {

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    ParticipantMapper participantMapper;

    /**
     * 查询代办任务
     *
     * @param taskReq    任务请求参数
     * @param procKeys   流程定义ID，数组【必填】
     * @param userId     用户ID【必填】
     * @param procInstId 流程实例ID
     * @param taskDefKey 任务定义Key
     * @return List
     */
    public List<FlowTaskVO> todoTasks(FlowTaskDTO taskReq) {
        List<FlowTaskVO> resps = null;
        TaskQuery taskQuery = taskService.createTaskQuery().taskCandidateOrAssigned(String.valueOf(taskReq.getUserId()));
        if (taskReq.getProcKeys() != null && !taskReq.getProcKeys().isEmpty()) {
            taskQuery.processDefinitionKeyIn(taskReq.getProcKeys());
        }
        if (StringUtils.isEmpty(taskReq.getProcInstId())) {
            taskQuery.processInstanceId(taskReq.getProcInstId());
        }
        if (StringUtils.isEmpty(taskReq.getTaskDefKey())) {
            taskQuery.taskDefinitionKey(taskReq.getTaskDefKey());
        }
        List<Task> todoTasks = taskQuery.orderByTaskCreateTime().desc().list();
        if (todoTasks != null && todoTasks.size() > 0) {
            resps = new ArrayList<>(todoTasks.size());
            long currentTimeMillis = System.currentTimeMillis();
            for (Task task : todoTasks) {
                FlowTaskVO todoTaskRspBo = new FlowTaskVO();
                todoTaskRspBo.setTaskId(task.getId());
                todoTaskRspBo.setTaskName(task.getName());
                todoTaskRspBo.setProcInstId(task.getProcessInstanceId());
                todoTaskRspBo.setStartTime(task.getCreateTime());
                long duration = (currentTimeMillis - task.getCreateTime().getTime()) / 1000;
                long day = duration / (24 * 3600);
                long hour = duration % (24 * 3600) / 3600;
                long minute = duration % 3600 / 60;
                long second = duration % 60;
                String stayTime = second + "秒";
                if (day != 0L) {
                    stayTime = day + "天";
                } else if (hour != 0L) {
                    stayTime = hour + "小时";
                } else if (minute != 0L) {
                    stayTime = minute + "分钟";
                }
                todoTaskRspBo.setStayTime(stayTime);
                resps.add(todoTaskRspBo);
            }
        }

        return resps;
    }

    /**
     * 根据用户ID获取任务列表
     *
     * @param userId   用户ID
     * @param procKeys 流程定义Key
     * @return List
     */
    public List<FlowTaskVO> taskByUser(long userId, List<String> procKeys) {
        List<FlowTaskVO> resp = null;
        TaskQuery taskQuery = taskService.createTaskQuery();
        if (procKeys != null && !procKeys.isEmpty()) {
            taskQuery.processDefinitionKeyIn(procKeys);
        }
        taskQuery.taskCandidateOrAssigned(String.valueOf(userId)).orderByTaskCreateTime().desc();
        List<Task> todoTasks = taskQuery.list();
        if (todoTasks != null && todoTasks.size() > 0) {
            resp = new ArrayList<>();
            FlowTaskVO flowTask = null;
            for (Task task : todoTasks) {
                flowTask = new FlowTaskVO();
                flowTask.setTaskId(task.getId());
                flowTask.setTaskName(task.getName());
                flowTask.setProcInstId(task.getProcessInstanceId());
                flowTask.setStartTime(task.getCreateTime());
                resp.add(flowTask);
            }
        }
        return resp;
    }

    /**
     * 查询历史任务
     *
     * @param taskReq    userId 用户ID【必填】
     * @param procKeys   流程定义Key【必填】
     * @param procInstId 流程实例ID
     * @param taskDefKey 任务定义Key
     * @param offSet     开始记录数【必填】
     * @param pageSize   每页记录数【必填】
     * @return List
     */
    public List<FlowTaskVO> historyTasks(FlowTaskDTO taskReq) {
        List<FlowTaskVO> todoTasksRsp = Lists.newArrayList();
        String user = String.valueOf(taskReq.getUserId());

        HistoricTaskInstanceQuery taskQuery = historyService.createHistoricTaskInstanceQuery().taskAssignee(user).finished();
        if (taskReq.getProcKeys() != null && !taskReq.getProcKeys().isEmpty()) {
            taskQuery.processDefinitionKeyIn(taskReq.getProcKeys());
        }
        if (taskReq.getProcInstId() != null) {
            taskQuery.processInstanceId(taskReq.getProcInstId());
        }
        if (taskReq.getTaskDefKey() != null) {
            taskQuery.taskDefinitionKey(taskReq.getTaskDefKey());
        }
        long count = taskQuery.count();
        List<HistoricTaskInstance> doneTasks = taskQuery.listPage(taskReq.getOffSet(), taskReq.getPageSize());
        for (HistoricTaskInstance task : doneTasks) {
            FlowTaskVO todoTaskRspBo = new FlowTaskVO();
            todoTaskRspBo.setTaskId(task.getId());
            todoTaskRspBo.setTaskName(task.getName());
            todoTaskRspBo.setProcInstId(task.getProcessInstanceId());
            todoTaskRspBo.setStartTime(task.getCreateTime());
            long duration = (task.getEndTime().getTime() - task.getCreateTime().getTime()) / 1000;
            long day = duration / (24 * 3600);
            long hour = duration % (24 * 3600) / 3600;
            long minute = duration % 3600 / 60;
            long second = duration % 60;
            String stayTime = second + "秒";
            if (day != 0L) {
                stayTime = day + "天";
            } else if (hour != 0L) {
                stayTime = hour + "小时";
            } else if (minute != 0L) {
                stayTime = minute + "分钟";
            }
            todoTaskRspBo.setStayTime(stayTime);
            todoTasksRsp.add(todoTaskRspBo);
        }
        return todoTasksRsp;
    }

    /**
     * 历史审批意见
     *
     * @param req
     * @param procInstId 流程实例ID【必填】
     * @return
     */
    public List<FlowCommentVO> historyComments(FlowTaskDTO req) {
        List<HistoricTaskInstance> taskHistorys = historyService.createHistoricTaskInstanceQuery().processInstanceId(req.getProcInstId()).list();
        List<FlowCommentVO> taskCommentsRsp = new ArrayList<FlowCommentVO>();
        for (HistoricTaskInstance hisTask : taskHistorys) {
            String historyTaskId = hisTask.getId();
            FlowCommentVO taskCommentRspBo = new FlowCommentVO();
            taskCommentRspBo.setProcInstId(hisTask.getProcessInstanceId());
            taskCommentRspBo.setTaskId(historyTaskId);
            taskCommentRspBo.setTaskName(hisTask.getName());
            taskCommentRspBo.setTaskDefKey(hisTask.getTaskDefinitionKey());
            taskCommentRspBo.setOperaUserId(hisTask.getAssignee());
            taskCommentRspBo.setTaskStartTime(hisTask.getStartTime());
            hisTask.getDurationInMillis();
            Long duration = hisTask.getDurationInMillis();
            duration = duration == null ? 0 : (duration / 1000);
            long day = duration / (24 * 3600);
            long hour = duration % (24 * 3600) / 3600;
            long minute = duration % 3600 / 60;
            long second = duration % 60;
            String stayTime = second + "秒";
            if (day != 0L) {
                stayTime = day + "天";
            } else if (hour != 0L) {
                stayTime = hour + "小时";
            } else if (minute != 0L) {
                stayTime = minute + "分钟";
            }
            taskCommentRspBo.setStayTime(stayTime);
            taskCommentsRsp.add(taskCommentRspBo);
        }
        return taskCommentsRsp;
    }

    /**
     * 获取当前任务
     *
     * @param procInstId 流程实例ID【必填】
     * @param uid        用户Id【必填】
     * @return
     */
    public FlowTaskVO getCurrentTask(String procInstId, long uid) {
        FlowTaskVO flowTaskVO = null;
        String userId = String.valueOf(uid);
        List<Task> currentTasks = null;
        currentTasks = taskService.createTaskQuery().processInstanceId(procInstId).taskCandidateOrAssigned(userId).list();
        if (currentTasks != null && !currentTasks.isEmpty()) {
            Task currentTask = currentTasks.get(0);
            flowTaskVO = new FlowTaskVO();
            flowTaskVO.setTaskId(currentTask.getId());
            flowTaskVO.setTaskDefKey(currentTask.getTaskDefinitionKey());
            flowTaskVO.setTaskName(currentTask.getName());
            flowTaskVO.setProcInstId(currentTask.getProcessInstanceId());
            flowTaskVO.setExecutionId(currentTask.getExecutionId());
        }

        return flowTaskVO;
    }

    /**
     * 任务下一步处理人
     *
     * @param procInstId 流程实例ID  【必填】
     * @return
     */
    public List<TaskDealerVO> taskNextDealer(String procInstId) {
        if (StringUtils.isEmpty(procInstId)) {
            throw new RuntimeException(FlowDefaultVars.ILLEGAL_MESSAGE);
        }
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(procInstId).singleResult();
        if (processInstance == null || processInstance.isEnded()) {
            return null;
        }
        List<TaskDealerVO> nextTasks = null;
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(procInstId).list();
        if (tasks != null && !tasks.isEmpty()) {
            nextTasks = new ArrayList<TaskDealerVO>();
            for (Task task : tasks) {
                TaskDealerVO nextTask = new TaskDealerVO();
                nextTask.setTaskId(task.getId());
                nextTask.setTaskName(task.getName());
                String assignee = task.getAssignee();
                List<String> userIds = new ArrayList<String>();
                List<String> userInfos = new ArrayList<>();
                if (assignee != null) {
                    //判断用户为空的情况在查询下一步处理人的时候判断,BpmCandidateUsersServiceImpl.findByPositionInDept
                    userIds.add(assignee);
                    userInfos.add(queryUserInfo(assignee));
                } else {
                    List<IdentityLink> ils = taskService.getIdentityLinksForTask(task.getId());
                    for (IdentityLink il : ils) {
                        userIds.add(il.getUserId());
                        userInfos.add(queryUserInfo(il.getUserId()));
                    }
                }
                nextTask.setUserIds(userIds);
                nextTask.setUserInfos(userInfos);
                nextTasks.add(nextTask);
            }
        }
        return nextTasks;
    }

    /**
     * 获取流程变量
     *
     * @param executionId 任务执行ID【必填】
     * @param varName     变量名称【必填】
     * @return
     */
    public Object getFlowVariable(String executionId, String varName) {
        Object value = runtimeService.getVariable(executionId, varName);
        return value;
    }

    /**
     * 根据流程实例获取任务
     *
     * @param procInstId 流程实例ID【参数】
     * @return
     */
    public List<FlowTaskVO> tasksByProcInstId(String procInstId) {
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(procInstId).list();
        List<FlowTaskVO> flowTasks = Lists.newArrayList();
        if (tasks != null && tasks.size() > 0) {
            FlowTaskVO flowTask;
            for (Task task : tasks) {
                flowTask = new FlowTaskVO();
                flowTask.setExecutionId(task.getExecutionId());
                flowTask.setProcInstId(task.getProcessInstanceId());
                flowTask.setTaskDefKey(task.getTaskDefinitionKey());
                flowTask.setTaskId(task.getId());
                flowTask.setTaskName(task.getName());
                flowTasks.add(flowTask);
            }
        }
        return flowTasks;
    }

    /**
     * 查询候选人,候选人ID，多个用 , 分隔
     *
     * @param procInstId 流程实例ID【必填】
     * @return
     */
    public String getCandidate(String procInstId) {
        String result = null;
        List<Task> t = taskService.createTaskQuery().processInstanceId(procInstId).list();
        if (t == null || t.isEmpty()) {
            return null;
        }
        List<IdentityLink> identityLinks = new ArrayList<>();
        for (Task task : t) {
            List<IdentityLink> forTask = taskService.getIdentityLinksForTask(task.getId());
            if (CollUtil.isEmpty(forTask)) {
                return null;
            }
            identityLinks.addAll(forTask);
        }
        List<Long> userIds = new ArrayList<>();
        for (IdentityLink il : identityLinks) {
            if (il == null) continue;
            userIds.add(Long.valueOf(il.getUserId()));
        }
        StringBuilder sb = new StringBuilder();

        for (Long uid : userIds) {
            sb.append(uid).append(",");
        }
        if (sb.toString().endsWith(",")) {
            result = sb.substring(0, sb.length() - 1).toString();
        }
        return result;
    }

    /**
     * 查询人员信息
     *
     * @param userId
     * @return
     */
    public String queryUserInfo(String userId) {
        Participant user = participantMapper.userInfoById(Long.parseLong(userId));
        if (user != null) {
            return user.getUserName();
        }
        return null;
    }
}

package cn.ilubov.rest;

import cn.hutool.core.util.StrUtil;
import cn.ilubov.dto.FlowStartDTO;
import cn.ilubov.dto.FlowTaskDTO;
import cn.ilubov.dto.TaskCompleteDTO;
import cn.ilubov.service.BpmBusinService;
import cn.ilubov.service.BpmDefineService;
import cn.ilubov.service.BpmTaskService;
import cn.ilubov.service.FlowDefaultVars;
import cn.ilubov.vo.FlowDefineVO;
import cn.ilubov.vo.FlowStartVO;
import cn.ilubov.vo.FlowTaskVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * 简易页面
 *
 * @author ilubov
 * @date 2020/10/25
 */
@Api(tags = {"简易页面"})
@Controller
public class FlowCtrl {

    @Autowired
    BpmDefineService bpmDefineService;

    @Autowired
    BpmBusinService bpmBusinService;

    @Autowired
    BpmTaskService bpmTaskService;

    @RequestMapping({"", "/"})
    public String index(HttpServletRequest request) {
        request.setAttribute("title", "工作流测试！");
        return "index";
    }

    /**
     * 流程测试--查询已发布的流程
     *
     * @param request
     * @return
     */
    @ApiOperation("流程测试--查询已发布的流程")
    @GetMapping("/flow")
    public String flow(HttpServletRequest request) {
        request.setAttribute("title", "流程管理！");
        List<FlowDefineVO> flows = bpmDefineService.queryDefinitions();
        request.setAttribute("flows", flows);
        return "flow";
    }

    /**
     * 发布流程
     *
     * @param request
     * @return
     */
    @ApiOperation("发布流程")
    @PostMapping("/flow")
    public String flows(HttpServletRequest request, @RequestParam("bpmnFile") MultipartFile bpmnFile) {
        try {
            int ret = bpmDefineService.processDeployment(bpmnFile.getOriginalFilename(), bpmnFile.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flow(request);
    }

    /**
     * 删除流程
     *
     * @param request
     * @return
     */
    @ApiOperation("删除流程")
    @PostMapping("/delFlow")
    @ResponseBody
    public String delFlow(HttpServletRequest request) {
        String ret;
        String deploymentId = request.getParameter("deploymentId");
        if (StrUtil.isBlank(deploymentId)) {
            ret = "请选择一个流程！";
            return ret;
        }
        try {
            ret = bpmDefineService.deleteProcess(deploymentId, true);
        } catch (Exception e) {
            ret = "删除流程异常！" + e.getMessage();
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * 查看流程图
     *
     * @param request
     * @return
     */
    @ApiOperation("查看流程图")
    @GetMapping("/viewFlow")
    public String viewFlow(HttpServletRequest request, HttpServletResponse response) {
        String deploymentId = request.getParameter("deploymentId");
        String imageName = request.getParameter("diagramResourceName");
        try {
            response.setHeader("Content-type", "image/png;charset=UTF-8");
            bpmDefineService.viewImage(deploymentId, imageName, response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 创建流程实例
     *
     * @param request
     * @return
     */
    @ApiOperation("创建流程实例")
    @PostMapping("/newInstance")
    @ResponseBody
    public String newInstance(HttpServletRequest request) {
        String ret;
        String key = request.getParameter("key");
        if (StrUtil.isBlank(key)) {
            ret = "请选择一个流程！";
            return ret;
        }
        try {
            FlowStartDTO startReq = new FlowStartDTO();
            startReq.setApplyId(1000L);
            startReq.setProcKey(key);
            startReq.setBusinessId(String.valueOf(System.currentTimeMillis()));
            startReq.setBusinessTitle(key + " : "
                    + DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd'T'HH:mm:ss"));
            Map<String, Object> variable = new HashMap<>();
            variable.put("start", startReq.getBusinessId() + ":" + startReq.getBusinessTitle());
            variable.put(FlowDefaultVars.VAR_ROOM, "1");
            variable.put(FlowDefaultVars.VAR_BUSINESS_HALL, "1");
            variable.put("ifAgree", true);
            startReq.setVariable(variable);
            FlowStartVO startResp = bpmBusinService.flowStart(startReq);
            ret = "流程实例启动成功:" + startResp.getProcInstId();
        } catch (Exception e) {
            ret = "启动流程实例异常！" + e.getMessage();
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * 代办任务
     *
     * @param request    procKeys 流程定义ID，数组【必填】
     * @param userId     用户ID【必填】
     * @param procInstId 流程实例ID
     * @param taskDefKey 任务定义Key
     * @return
     */
    @ApiOperation("代办任务")
    @GetMapping("/todoWork")
    public String todoWork(HttpServletRequest request) {
        request.setAttribute("title", "代办任务！");
        FlowTaskDTO taskReq = new FlowTaskDTO();
        taskReq.setUserId(1000L);
        List<String> procKeys = Collections.singletonList("TEST_FLOW");
        // taskReq.setProcKeys(procKeys);
        List<FlowTaskVO> tasks = bpmTaskService.todoTasks(taskReq);
        request.setAttribute("tasks", tasks);
        return "todoWork";
    }

    /**
     * 完成任务
     *
     * @param request
     * @param taskId    任务ID【必填】
     * @param userId    用户ID【必填】
     * @param variable  流程变量
     * @param seqFlowId 路由选择变量
     * @return
     */
    @ApiOperation("完成任务")
    @PostMapping("/taskComplete")
    @ResponseBody
    public String taskComplete(HttpServletRequest request) {
        String ret;
        String taskId = request.getParameter("taskId");
        if (StrUtil.isBlank(taskId)) {
            ret = "请选择一个任务！";
            return ret;
        }
        try {
            TaskCompleteDTO taskReq = new TaskCompleteDTO();
            taskReq.setUserId(1000L);
            taskReq.setTaskId(taskId);
            Map<String, Object> variable = new HashMap<>();
            variable.put("doTask", taskId + " : " + DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd'T'HH:mm:ss"));
            bpmBusinService.taskComplete(taskReq);
            ret = "完成任务成功:" + taskId;
        } catch (Exception e) {
            ret = "完成任务异常！" + e.getMessage();
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * 删除流程实例
     *
     * @param request
     * @return
     */
    @ApiOperation("删除流程实例")
    @PostMapping("/delProcInst")
    @ResponseBody
    public String delProcInst(HttpServletRequest request) {
        String ret;
        String procInstId = request.getParameter("procInstId");
        if (StrUtil.isBlank(procInstId)) {
            ret = "请选择一个任务！";
            return ret;
        }
        try {
            bpmBusinService.delProcInst(procInstId, 1000L);
            ret = "删除流程实例成功:";
        } catch (Exception e) {
            ret = "删除流程实例异常！" + e.getMessage();
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * 历史任务
     *
     * @param request
     * @return
     */
    @ApiOperation("历史任务")
    @GetMapping("/historyTasks")
    public String historyTasks(HttpServletRequest request) {
        request.setAttribute("title", "历史任务！");
        FlowTaskDTO taskReq = new FlowTaskDTO();
        taskReq.setUserId(1000L);
        List<String> procKeys = Collections.singletonList("TEST_FLOW");
        // taskReq.setProcKeys(procKeys);
        taskReq.setOffSet(0);
        taskReq.setPageSize(100);
        List<FlowTaskVO> tasks = bpmTaskService.historyTasks(taskReq);
        request.setAttribute("tasks", tasks);
        return "historyTasks";
    }
}

package cn.ilubov.service;

import cn.ilubov.vo.FlowDefineVO;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 流程定义
 *
 * @author ilubov
 * @date 2020/10/25
 */
@Slf4j
@Service
public class BpmDefineService {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    /**
     * 查询发布的流程
     *
     * @return
     */
    public List<FlowDefineVO> queryDefinitions() {
        List<FlowDefineVO> flows = null;
        try {
            ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
            List<ProcessDefinition> defines = processDefinitionQuery.orderByProcessDefinitionVersion().desc().list();
            if (defines != null && defines.size() > 0) {
                flows = new ArrayList<>(defines.size());
                FlowDefineVO flow = null;
                for (ProcessDefinition define : defines) {
                    flow = new FlowDefineVO();
                    flow.setDeployId(define.getId());
                    flow.setDeployName(define.getName());
                    flow.setDeploymentId(define.getDeploymentId());
                    flow.setResourceName(define.getResourceName());
                    flow.setDiagramResourceName(define.getDiagramResourceName());
                    flow.setVersion(define.getVersion());
                    flow.setKey(define.getKey());
                    flow.setDescription(define.getDescription());
                    flows.add(flow);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return flows;
    }

    /**
     * 部署流程 文件路径方式
     *
     * @param filepath 流程文件路径，classpath下  processes/Test.bpmn
     * @return 0 成功，1 失败
     */
    public int processDeployment(String filepath) {
        int ret = 1;
        try {
            String bpmnName = filepath.substring(filepath.lastIndexOf("/") + 1);
            InputStream is = this.getClass().getClassLoader().getResourceAsStream(filepath);
            Deployment dpy = repositoryService.createDeployment().name(bpmnName).addInputStream(bpmnName, is).deploy();
            ret = 0;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ret;
    }

    /**
     * 部署流程 文件流方式
     *
     * @param filename
     * @param is
     * @return 0 成功，1 失败
     */
    public int processDeployment(String filename, InputStream is) {
        int ret = 1;
        try {
            Deployment dpy = repositoryService.createDeployment().name(filename).addInputStream(filename, is).deploy();
            ret = 0;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ret;
    }

    /**
     * 删除流程
     *
     * @param deploymentId 流程部署ID
     * @param cascade      是否关联删除
     * @return
     */
    public String deleteProcess(String deploymentId, boolean cascade) {
        String msg = "";
        try {
            repositoryService.deleteDeployment(deploymentId, cascade);
            msg = "流程删除成功";
        } catch (Exception ex) {
            msg = "流程删除失败";
            ex.printStackTrace();
        }
        return msg;
    }

    /**
     * 查看流程图
     *
     * @param deploymentId
     * @param imageName
     * @param out
     * @throws Exception
     */
    public void viewImage(String deploymentId, String imageName, OutputStream out) throws Exception {
        String image = null;
        image = imageName;
        if (imageName == null) {
            List<String> names = repositoryService.getDeploymentResourceNames(deploymentId);
            for (String name : names) {
                if (names.indexOf(".png") >= 0) {
                    image = name;
                }
            }
        }
        InputStream in = getImageInputStream(deploymentId, image);
        for (int b = -1; (b = in.read()) != -1; ) {
            out.write(b);
        }
        out.close();
        in.close();

    }

    public InputStream getImageInputStream(String deploymentId, String imageName) {
        return repositoryService.getResourceAsStream(deploymentId, imageName);
    }
}

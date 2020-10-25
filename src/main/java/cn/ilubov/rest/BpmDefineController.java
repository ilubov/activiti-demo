package cn.ilubov.rest;

import cn.ilubov.dto.FlowDeployDTO;
import cn.ilubov.comps.R;
import cn.ilubov.vo.FlowDefineVO;
import cn.ilubov.service.BpmDefineService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.executor.BatchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 流程管理类：部署流程、查询流程、删除流程
 *
 * @author ilubov
 * @date 2020/10/25
 */
@Api(tags = {"流程管理类：部署流程、查询流程、删除流程"})
@RestController
@RequestMapping("/bpm")
public class BpmDefineController {

    @Autowired
    BpmDefineService bpmDefineService;

    /**
     * 查询流程
     *
     * @return
     */
    @ApiOperation("查询流程")
    @RequestMapping("/flowDefines")
    public R<List<FlowDefineVO>> flowDefines() {
        List<FlowDefineVO> flows = bpmDefineService.queryDefinitions();
        return R.<List<FlowDefineVO>>ok().data(flows);
    }

    /**
     * 发布流程
     *
     * @param req
     * @return
     */
    @ApiOperation("发布流程")
    @RequestMapping("/deployFlow")
    public R<List<BatchResult>> deployFlow(@RequestBody FlowDeployDTO req) {
        int ret = bpmDefineService.processDeployment(req.getBpmnFilePath());
        if (ret == 0) {
            return R.ok();
        }
        return R.error();
    }

    /**
     * 删除流程
     *
     * @param req
     * @return
     */
    @ApiOperation("删除流程")
    @RequestMapping("/deleteFlow")
    public R<Object> delFlow(@RequestBody FlowDeployDTO req) {
        String msg = bpmDefineService.deleteProcess(req.getDeploymentId(), req.isCascade());
        return R.ok().desc(msg);
    }
}

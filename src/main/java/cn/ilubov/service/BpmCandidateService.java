package cn.ilubov.service;

import cn.ilubov.entity.Participant;
import cn.ilubov.mapper.ParticipantMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 查找审批人
 *
 * @author ilubov
 * @date 2020/10/25
 */
@Slf4j
@Service
public class BpmCandidateService {

    @Autowired
    ParticipantMapper participantMapper;

    /**
     * 根据岗位获取审批人
     *
     * @param postCode 岗位
     * @return
     */
    public List<String> getApproversByPost(String postCode) {
        return getApprovers(null, null, null, postCode);
    }

    /**
     * 根据租户获取审批人
     *
     * @param tenant   租户ID
     * @param postCode 岗位
     * @return
     */
    public List<String> getApproversByPost(String tenant, String postCode) {
        return getApprovers(null, null, tenant, postCode);
    }


    /**
     * 根据机房获取审批人
     *
     * @param egroom   机房ID
     * @param postCode 岗位
     * @return
     */
    public List<String> getApproversByEgroom(String egroom, String postCode) {
        return getApprovers(egroom, null, null, postCode);
    }

    /**
     * 根据营业厅获取审批人
     *
     * @param businHall 营业厅ID
     * @param postCode  岗位
     * @return
     */
    public List<String> getApproversByHall(String businHall, String postCode) {
        return getApprovers(null, businHall, null, postCode);
    }

    /**
     * 根据 机房、营业厅、岗位获取审批人
     *
     * @param egroom    机房ID
     * @param businHall 营业厅ID
     * @param tenant    租户ID
     * @param postCode  岗位
     * @return List
     */
    public List<String> getApprovers(String egroom, String businHall, String tenant, String postCode) {
        log.info("egroom=" + egroom + " , businHall=" + businHall + " , postCode=" + postCode);
        List<String> userIds = new ArrayList<>();
        List<Participant> users = participantMapper.getApprovers(egroom, businHall, tenant, postCode);
        if (users != null && users.size() > 0) {
            for (Participant user : users) {
                userIds.add(String.valueOf(user.getUserId()));
            }
        }
        return userIds;
    }
}

package cn.ilubov.mapper;

import cn.ilubov.entity.Participant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 参与者
 *
 * @author ilubov
 * @date 2020/10/25
 */
@Mapper
public interface ParticipantMapper {

    /**
     * 根据 机房、营业厅、岗位获取审批人
     *
     * @param egroom    机房ID
     * @param businHall 营业厅ID
     * @param postCode  岗位
     * @return
     */
    List<Participant> getApprovers(@Param("egroom") String egroom,
                                   @Param("businHall") String businHall,
                                   @Param("tenant") String tenant,
                                   @Param("postCode") String postCode);

    Participant userInfoById(@Param("userId") long userId);
}
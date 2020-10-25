package cn.ilubov.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 参与者
 *
 * @author ilubov
 * @date 2020/10/25
 */
@Data
@TableName("participant")
public class Participant {

    @TableId(value = "cfg_id", type = IdType.AUTO)
    private Long cfgId;

    //营业厅ID
    @TableField("busin_hall")
    private Long businHall;

    //营业厅名称
    @TableField("hall_name")
    private String hallName;

    //机房ID
    @TableField("egroom")
    private Long egroom;

    //租户ID
    @TableField("tenant_id")
    private Long tenantId;

    //机房名称
    @TableField("businHall")
    private String egroomName;

    // 岗位
    @TableField("post_code")
    private String postCode;

    //用户ID
    @TableField("user_id")
    private Long userId;

    //用户名称
    @TableField("user_name")
    private String userName;

    @TableField("status")
    private String status;

    @TableField("create_user")
    private Long createUser;

    @TableField("create_date")
    private Date createDate;
}

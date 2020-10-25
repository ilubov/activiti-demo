package cn.ilubov.vo;

import lombok.Data;

import java.util.Date;

@Data
public class FlowDefineVO {

    private String deployId;

    private String deployName;

    private Date deployTime;

    private String deploymentId;

    private String resourceName;

    private int version;

    private String key;

    private String description;

    private String diagramResourceName;
}

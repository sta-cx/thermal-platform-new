package org.sdkj.thermal.domain.vo;

import lombok.Data;

@Data
public class PrCompanyVo {
    private Long id;
    private String name;
    private String code;
    private String tele;
    private String principal;
    private Integer nature;
    private Integer isEnabled;
}

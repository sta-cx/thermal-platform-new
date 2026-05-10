package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.thermal.domain.PrPrintTemplate;

import java.util.List;

/**
 * 打印模板 Service 接口
 */
public interface IPrPrintTemplateService extends IService<PrPrintTemplate> {

    /**
     * 查询模板列表
     */
    List<PrPrintTemplate> listByOrgAndCompany(String orgId, String name);

    /**
     * 保存或更新模板
     */
    boolean saveOrUpdateTemplate(String orgId, String name, String templateContent);

    /**
     * 根据序列号查询模板
     */
    PrPrintTemplate getBySerialNum(String serialNum);
}

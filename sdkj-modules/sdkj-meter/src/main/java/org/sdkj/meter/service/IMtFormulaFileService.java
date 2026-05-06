package org.sdkj.meter.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.meter.domain.MtFormulaFile;
import org.sdkj.meter.domain.vo.MtFormulaFileVo;

import java.util.List;
import java.util.Map;

/**
 * 公式档案服务接口
 */
public interface IMtFormulaFileService extends IService<MtFormulaFile> {

    TableDataInfo<MtFormulaFileVo> selectPageList(LambdaQueryWrapper<MtFormulaFile> lqw, PageQuery pageQuery);

    int validateName(String name, String id);

    boolean toggleEnabled(Long id, String value);

    List<Map<String, Object>> getFormulaType();

    List<Map<String, Object>> getFormulaElement();

    List<MtFormulaFileVo> getDataByType(String type);
}

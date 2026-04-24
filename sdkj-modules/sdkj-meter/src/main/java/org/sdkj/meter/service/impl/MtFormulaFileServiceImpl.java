package org.sdkj.meter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.meter.domain.MtFormulaFile;
import org.sdkj.meter.domain.vo.MtFormulaFileVo;
import org.sdkj.meter.mapper.MtFormulaFileMapper;
import org.sdkj.meter.service.IMtFormulaFileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 公式档案服务实现
 */
@Service
@RequiredArgsConstructor
public class MtFormulaFileServiceImpl extends ServiceImpl<MtFormulaFileMapper, MtFormulaFile> implements IMtFormulaFileService {

    private final MtFormulaFileMapper baseMapper;

    @Override
    public TableDataInfo<MtFormulaFileVo> selectPageList(LambdaQueryWrapper<MtFormulaFile> lqw, PageQuery pageQuery) {
        Page<MtFormulaFileVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public int validateName(String name, String id) {
        return baseMapper.validateName(name, id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean toggleEnabled(String id, String value) {
        MtFormulaFile entity = new MtFormulaFile();
        entity.setId(id);
        entity.setIsEnabled(value);
        return updateById(entity);
    }

    @Override
    public List<Map<String, Object>> getFormulaType() {
        return baseMapper.getFormulaType();
    }

    @Override
    public List<Map<String, Object>> getFormulaElement() {
        return baseMapper.getFormulaElement();
    }

    @Override
    public List<MtFormulaFileVo> getDataByType(String type) {
        return baseMapper.selectByType(type).stream()
            .map(e -> {
                MtFormulaFileVo vo = new MtFormulaFileVo();
                vo.setId(e.getId());
                vo.setName(e.getName());
                vo.setType(e.getType());
                vo.setCformula(e.getCformula());
                vo.setEformula(e.getEformula());
                vo.setSeq(e.getSeq());
                vo.setIsEnabled(e.getIsEnabled());
                return vo;
            }).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(MtFormulaFile entity) {
        convertFormula(entity);
        return super.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(MtFormulaFile entity) {
        convertFormula(entity);
        return super.updateById(entity);
    }

    /**
     * 将中文公式中的元素名称替换为字典值
     */
    private void convertFormula(MtFormulaFile entity) {
        if (entity.getCformula() == null) return;
        entity.setEformula(entity.getCformula());
        List<Map<String, Object>> elements = baseMapper.getFormulaElement();
        if (elements == null || elements.isEmpty()) return;
        for (Map<String, Object> elem : elements) {
            String label = (String) elem.get("label");
            String value = (String) elem.get("value");
            if (label != null && value != null && entity.getCformula().contains(label)) {
                entity.setEformula(entity.getEformula().replace(label, value));
            }
        }
    }
}

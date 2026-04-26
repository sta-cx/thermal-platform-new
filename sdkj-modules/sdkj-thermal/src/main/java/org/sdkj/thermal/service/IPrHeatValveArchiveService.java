package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrHeatValveArchive;
import org.sdkj.thermal.domain.dto.PrHouseByPayVo;
import org.sdkj.thermal.domain.vo.PrHeatValveArchiveVo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/**
 * 户间阀门配表 Service 接口
 */
public interface IPrHeatValveArchiveService extends IService<PrHeatValveArchive> {

    /**
     * 根据ID查询档案详情
     */
    PrHeatValveArchiveVo selectById(Serializable id);

    /**
     * 分页查询户间阀门配表列表
     * @param companyId 公司ID
     * @param orgId 小区ID
     * @param buildingId 楼宇ID
     * @param unit 单元
     * @param search 搜索关键字（表号/档案名称）
     * @param parentId 父级ID（房屋ID）
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<PrHeatValveArchiveVo> selectPageList(String companyId, String orgId, String buildingId,
                                                        String unit, String search, String parentId,
                                                        PageQuery pageQuery);

    /**
     * 批量设置阀门开关/查询/制动状态
     * @param houseList 房屋列表（含 meterNum + meterArcCode）
     * @param valveStatus 阀门状态: "1"→开(100), "2"→关(0), "3"→开度, "4"→查询, "5"→制动, "51"→特殊
     * @return 是否成功
     */
    boolean batchSetValveStatus(List<PrHouseByPayVo> houseList, String valveStatus);

    /**
     * 批量设置阀门开度
     * @param houseList 房屋列表（含 meterNum + meterArcCode）
     * @param opening 开度值
     * @return 是否成功
     */
    boolean batchSetValveOpening(List<PrHouseByPayVo> houseList, String opening);

    /**
     * 批量设置上报周期
     * @param houseList 房屋列表（含 meterNum + meterArcCode）
     * @param interval 间隔
     * @param unit 单位
     * @param valid 有效时间
     * @return 是否成功
     */
    boolean batchSetValveCycle(List<PrHouseByPayVo> houseList, String interval, String unit, String valid);

    /**
     * 查询全部阀门信息（用于导出）
     * @param companyId 公司ID
     * @param orgId 小区ID
     * @return 阀门配表列表
     */
    List<PrHeatValveArchiveVo> listAll(String companyId, String orgId);

    /**
     * 导入阀门配表
     * @param file Excel 文件
     * @return 导入结果
     */
    R<Void> importValveArchive(MultipartFile file) throws IOException;
}

package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.sdkj.common.core.domain.R;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 仪表编码获取
 * 迁移自旧系统 AccessCodeController
 * 为自助机提供仪表厂商/分类/设备的最新编码
 */
@Validated
@RestController
@RequestMapping("/thermal/agent/access-code")
public class AccessCodeController {

    private static final Map<String, String> METER_TABLE_MAP = Map.of(
        "01", "mt_electric_archive",
        "02", "mt_water_archive",
        "03", "mt_heat_archive",
        "04", "mt_gas_archive",
        "11", "mt_centrator_archive",
        "21", "mt_tc_archive",
        "31", "mt_tc_valve"
    );

    private final JdbcTemplate jdbc;

    public AccessCodeController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @SaCheckPermission("thermal:agent:accessCode:query")
    @SaCheckLogin
    @GetMapping("/vendorCode")
    public R<String> accessMtVendorCode() {
        Integer max = jdbc.queryForObject(
            "SELECT MAX(CAST(IFNULL(code,'0') AS UNSIGNED)) FROM mt_meter_vendor", Integer.class);
        String result = String.format("%02d", (max != null ? max : 0) + 1);
        return R.ok("操作成功", result);
    }

    @SaCheckPermission("thermal:agent:accessCode:query")
    @SaCheckLogin
    @GetMapping("/sortCode")
    public R<String> accessMtSortCode(@RequestParam String vendorId,
                                    @RequestParam String meterType,
                                    @RequestParam String vendorCode) {
        Integer max = jdbc.queryForObject(
            "SELECT MAX(CAST(IFNULL(serial_num,'0') AS UNSIGNED)) FROM mt_meter_sort WHERE vendor_id = ?",
            Integer.class, vendorId);
        String seq = String.format("%02d", (max != null ? max : 0) + 1);
        String result = vendorCode + meterType + seq;
        return R.ok("操作成功", result);
    }

    @SaCheckPermission("thermal:agent:accessCode:query")
    @SaCheckLogin
    @GetMapping("/meterCode")
    public R<String> accessMeterCode(@RequestParam String sortCode,
                                   @RequestParam String sortId,
                                   @RequestParam String meterType) {
        String tableName = METER_TABLE_MAP.get(meterType);
        if (tableName == null) {
            return R.fail("Invalid meter type: " + meterType);
        }
        try {
            Integer max = jdbc.queryForObject(
                "SELECT MAX(CAST(IFNULL(SUBSTRING(meter_num, LENGTH(?) + 1), '0') AS UNSIGNED)) FROM " + tableName
                + " WHERE meter_num LIKE ?",
                Integer.class, sortCode, sortCode + "%");
            String result = sortCode + String.format("%04d", (max != null ? max : 0) + 1);
            return R.ok("操作成功", result);
        } catch (Exception e) {
            String result = sortCode + "0001";
            return R.ok("操作成功", result);
        }
    }

    @SaCheckLogin
    @GetMapping("/list")
    public R<?> list() {
        return R.ok();
    }
}

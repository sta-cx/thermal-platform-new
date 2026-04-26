package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 仪表编码获取
 * 迁移自旧系统 AccessCodeController
 * 为自助机提供仪表厂商/分类/设备的最新编码
 */
@Validated
@RestController
@RequestMapping("/thermal/agent/access-code")
public class AccessCodeController {

    private final JdbcTemplate jdbc;

    public AccessCodeController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @SaCheckPermission("thermal:agent:access-code:query")
    @SaCheckLogin
    @GetMapping("/vendorCode")
    public String accessMtVendorCode() {
        Integer max = jdbc.queryForObject(
            "SELECT MAX(CAST(IFNULL(code,'0') AS UNSIGNED)) FROM mt_meter_vendor", Integer.class);
        return String.format("%02d", (max != null ? max : 0) + 1);
    }

    @SaCheckPermission("thermal:agent:access-code:query")
    @SaCheckLogin
    @GetMapping("/sortCode")
    public String accessMtSortCode(@RequestParam String vendorId,
                                    @RequestParam String meterType,
                                    @RequestParam String vendorCode) {
        Integer max = jdbc.queryForObject(
            "SELECT MAX(CAST(IFNULL(serial_num,'0') AS UNSIGNED)) FROM mt_meter_sort WHERE vendor_id = ?",
            Integer.class, vendorId);
        String seq = String.format("%02d", (max != null ? max : 0) + 1);
        return vendorCode + meterType + seq;
    }

    @SaCheckPermission("thermal:agent:access-code:query")
    @SaCheckLogin
    @GetMapping("/meterCode")
    public String accessMeterCode(@RequestParam String sortCode,
                                   @RequestParam String sortId,
                                   @RequestParam String meterType) {
        String tableName = switch (meterType) {
            case "01" -> "mt_electric_archive";
            case "02" -> "mt_water_archive";
            case "03" -> "mt_heat_archive";
            case "04" -> "mt_gas_archive";
            case "31" -> "mt_tc_valve";
            case "21" -> "mt_tc_archive";
            case "11" -> "mt_centrator_archive";
            default -> null;
        };
        if (tableName == null) {
            return "";
        }
        try {
            Integer max = jdbc.queryForObject(
                "SELECT MAX(CAST(IFNULL(SUBSTRING(meter_num, LENGTH(?) + 1), '0') AS UNSIGNED)) FROM " + tableName
                + " WHERE meter_num LIKE ?",
                Integer.class, sortCode, sortCode + "%");
            return sortCode + String.format("%04d", (max != null ? max : 0) + 1);
        } catch (Exception e) {
            return sortCode + "0001";
        }
    }
}

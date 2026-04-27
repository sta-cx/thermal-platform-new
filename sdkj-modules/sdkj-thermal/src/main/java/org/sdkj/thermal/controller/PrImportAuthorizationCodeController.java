package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Table;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.vo.MdbVo;
import org.sdkj.thermal.service.IPrImportAuthorizationCodeService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/property/auth-code")
public class PrImportAuthorizationCodeController extends BaseController {

    private final IPrImportAuthorizationCodeService service;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @SaCheckLogin
    @Log(title = "授权码导入", businessType = BusinessType.IMPORT)
    @PostMapping("/import")
    public R<Boolean> importData(@RequestParam("file") MultipartFile file,
                                  @RequestParam String longTermCode,
                                  @RequestParam(required = false) String numberSegmentCode) {
        File mdbFile = null;
        try {
            mdbFile = File.createTempFile("auth_code_", ".mdb");
            file.transferTo(mdbFile);

            List<JsonNode> records = new ArrayList<>();
            try (Database db = DatabaseBuilder.open(mdbFile)) {
                for (String tableName : db.getTableNames()) {
                    Table table = db.getTable(tableName);
                    table.forEach(row -> {
                        JsonNode node = objectMapper.valueToTree(row);
                        records.add(node);
                    });
                }
            }

            List<MdbVo> list = new ArrayList<>();
            for (JsonNode node : records) {
                MdbVo vo = objectMapper.convertValue(node, MdbVo.class);
                vo.setPro(longTermCode);
                list.add(vo);
            }

            if (!list.isEmpty()) {
                service.insertAuthorizationCode(list);
                if (StrUtil.isNotBlank(numberSegmentCode)) {
                    service.insertNumberSegmentCode(numberSegmentCode.trim());
                }
            }
            return R.ok(true);
        } catch (IOException e) {
            log.error("导入授权码失败", e);
            return R.fail("导入失败: " + e.getMessage());
        } finally {
            if (mdbFile != null) mdbFile.delete();
        }
    }
}

package org.sdkj.common.mybatis.interceptor;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("dev")
class OrgPermissionInterceptorTest {

    private final TestableOrgPermissionInterceptor interceptor = new TestableOrgPermissionInterceptor();

    @Test
    void selectSqlWithJoinUsesFromTableAliasForOrgPermissionColumn() throws Exception {
        String sql = """
            SELECT a.id, b.room_num
            FROM pr_heat_valve_archive a
            LEFT JOIN pr_house b ON b.id = a.house_id
            WHERE a.id = ?
            """;

        String rewritten = interceptor.rewriteSingle(sql).toLowerCase();

        assertTrue(rewritten.contains("where (a.id = ?)"), rewritten);
        assertTrue(rewritten.contains("pr_data_grant"), rewritten);
        assertTrue(rewritten.contains("a.org_id in"), rewritten);
    }

    @Test
    void selectSqlWithoutAliasUsesFromTableNameForOrgPermissionColumn() throws Exception {
        String sql = "SELECT id, room_num FROM pr_house WHERE id = ?";

        String rewritten = interceptor.rewriteSingle(sql).toLowerCase();

        assertTrue(rewritten.contains("where (id = ?)"), rewritten);
        assertTrue(rewritten.contains("pr_house.org_id in"), rewritten);
    }

    @Test
    void selectSqlWithOrWhereKeepsOrgPermissionAppliedToWholeCondition() throws Exception {
        String sql = "SELECT id FROM pr_house WHERE status = ? OR pay_status = ?";

        String rewritten = interceptor.rewriteSingle(sql).toLowerCase();

        assertTrue(rewritten.contains("(status = ? or pay_status = ?) and"), rewritten);
        assertTrue(rewritten.contains("pr_house.org_id in"), rewritten);
    }

    @Test
    void updateSqlKeepsExistingWhereAndAddsOrgPermissionCondition() throws Exception {
        String sql = "UPDATE pr_house SET name = ? WHERE id = ?";

        String rewritten = interceptor.rewriteMulti(sql).toLowerCase();

        assertTrue(rewritten.contains("where (id = ?)"), rewritten);
        assertTrue(rewritten.contains("pr_data_grant"), rewritten);
        assertTrue(rewritten.contains("pr_house.org_id in"), rewritten);
    }

    @Test
    void updateSqlWithJoinUsesTargetTableAliasForOrgPermissionColumn() throws Exception {
        String sql = """
            UPDATE ht_tasks_perform_ls a
            LEFT JOIN ht_tasks b ON b.id = a.tasks_id
            SET a.alert_type = '9'
            WHERE a.tasks_id = ?
            """;

        String rewritten = interceptor.rewriteMulti(sql).toLowerCase();

        assertTrue(rewritten.contains("a.org_id in"), rewritten);
    }

    @Test
    void deleteSqlKeepsExistingWhereAndAddsOrgPermissionCondition() throws Exception {
        String sql = "DELETE FROM pr_house WHERE id = ?";

        String rewritten = interceptor.rewriteMulti(sql).toLowerCase();

        assertTrue(rewritten.contains("where (id = ?)"), rewritten);
        assertTrue(rewritten.contains("pr_data_grant"), rewritten);
        assertTrue(rewritten.contains("pr_house.org_id in"), rewritten);
    }

    static class TestableOrgPermissionInterceptor extends OrgPermissionInterceptor {
        String rewriteSingle(String sql) {
            return parserSingle(sql, new OrgPermissionContext(7L, "org_id"));
        }

        String rewriteMulti(String sql) {
            return parserMulti(sql, new OrgPermissionContext(7L, "org_id"));
        }
    }
}

package org.sdkj.common.mybatis.interceptor;

import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.parser.JsqlParserSupport;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.ParenthesedExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SetOperationList;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import cn.dev33.satoken.exception.SaTokenContextException;
import org.sdkj.common.core.exception.ServiceException;
import org.sdkj.common.mybatis.annotation.OrgPermission;
import org.sdkj.common.mybatis.helper.OrgPermissionHelper;
import org.sdkj.common.satoken.utils.LoginHelper;

import java.sql.SQLException;
import java.util.List;

/**
 * 组织级数据权限拦截器。
 * 注意：与 DataPermission 共享 InterceptorIgnoreHelper.willIgnoreDataPermission 标志，
 * DataPermissionHelper.ignore() 也会绕过本拦截器。companyId 不参与过滤，依赖 org_id 在公司间的唯一性。
 */
public class OrgPermissionInterceptor extends JsqlParserSupport implements InnerInterceptor {

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter,
                           RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        if (InterceptorIgnoreHelper.willIgnoreDataPermission(ms.getId())) {
            return;
        }
        OrgPermission permission = OrgPermissionHelper.getPermission();
        if (permission == null) {
            return;
        }
        boolean superAdmin;
        Long userId;
        try {
            superAdmin = LoginHelper.isSuperAdmin() || LoginHelper.isTenantAdmin();
            userId = LoginHelper.getUserId();
        } catch (SaTokenContextException e) {
            // 非 HTTP 请求上下文（启动阶段、定时任务等），跳过过滤
            return;
        }
        if (superAdmin || userId == null) {
            return;
        }

        String column = permission.value();
        if (!column.matches("[a-zA-Z0-9_]+")) {
            throw new ServiceException("非法的权限列名: " + column);
        }

        String condition = String.format(
            "(NOT EXISTS (SELECT 1 FROM pr_data_grant WHERE user_id = %d AND del_flag = '0') " +
            "OR %s IN (SELECT org_id FROM pr_data_grant WHERE user_id = %d AND del_flag = '0'))",
            userId, column, userId
        );

        PluginUtils.MPBoundSql mpBs = PluginUtils.mpBoundSql(boundSql);
        mpBs.sql(parserSingle(mpBs.sql(), condition));
    }

    @Override
    protected void processSelect(Select select, int index, String sql, Object condition) {
        String cond = (String) condition;
        if (select instanceof PlainSelect plainSelect) {
            appendCondition(plainSelect, cond);
        } else if (select instanceof SetOperationList setOpList) {
            List<Select> selects = setOpList.getSelects();
            selects.forEach(s -> {
                if (s instanceof PlainSelect ps) {
                    appendCondition(ps, cond);
                }
            });
        }
    }

    private void appendCondition(PlainSelect plainSelect, String condition) {
        try {
            Expression conditionExpr = CCJSqlParserUtil.parseExpression(condition);
            ParenthesedExpressionList<Expression> parenthesis = new ParenthesedExpressionList<>(conditionExpr);
            Expression where = plainSelect.getWhere();
            if (where != null) {
                plainSelect.setWhere(new AndExpression(where, parenthesis));
            } else {
                plainSelect.setWhere(parenthesis);
            }
        } catch (JSQLParserException e) {
            throw new ServiceException("组织权限条件解析异常 => " + e.getMessage());
        }
    }
}

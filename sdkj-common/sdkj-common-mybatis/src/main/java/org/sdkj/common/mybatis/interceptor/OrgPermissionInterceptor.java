package org.sdkj.common.mybatis.interceptor;

import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.parser.JsqlParserSupport;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NotExpression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExistsExpression;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.ParenthesedExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.select.ParenthesedSelect;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import cn.dev33.satoken.exception.SaTokenContextException;
import org.sdkj.common.core.exception.ServiceException;
import org.sdkj.common.mybatis.annotation.OrgPermission;
import org.sdkj.common.mybatis.helper.OrgPermissionHelper;
import org.sdkj.common.satoken.utils.LoginHelper;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 组织级数据权限拦截器。
 *
 * 与 DataPermission 共享 InterceptorIgnoreHelper.willIgnoreDataPermission 标志,
 * DataPermissionHelper.ignore() 也会绕过本拦截器。companyId 不参与过滤,依赖
 * org_id 在公司间的唯一性。
 *
 * 拼接生成的 SQL 条件等价于:
 *   (NOT EXISTS (SELECT 1 FROM pr_data_grant WHERE user_id = :uid AND del_flag = '0')
 *    OR <column> IN (SELECT org_id FROM pr_data_grant WHERE user_id = :uid AND del_flag = '0'))
 *
 * 实现采用 JSqlParser 编程构造 Expression AST,不走字符串拼接 SQL,与
 * PlusTenantLineHandler 的 Expression 嵌入模式一致。
 */
public class OrgPermissionInterceptor extends JsqlParserSupport implements InnerInterceptor {

    private static final String GRANT_TABLE = "pr_data_grant";
    private static final String COL_USER_ID = "user_id";
    private static final String COL_ORG_ID = "org_id";
    private static final String COL_DEL_FLAG = "del_flag";
    private static final StringValue DEL_FLAG_ACTIVE = new StringValue("0");

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter,
                            RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        OrgPermissionContext context = resolveOrgPermissionContext(ms);
        if (context == null) {
            return;
        }
        PluginUtils.MPBoundSql mpBs = PluginUtils.mpBoundSql(boundSql);
        mpBs.sql(parserSingle(mpBs.sql(), context));
    }

    @Override
    public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
        PluginUtils.MPStatementHandler mpSh = PluginUtils.mpStatementHandler(sh);
        MappedStatement ms = mpSh.mappedStatement();
        SqlCommandType commandType = ms.getSqlCommandType();
        if (commandType != SqlCommandType.UPDATE && commandType != SqlCommandType.DELETE) {
            return;
        }

        OrgPermissionContext context = resolveOrgPermissionContext(ms);
        if (context == null) {
            return;
        }
        PluginUtils.MPBoundSql mpBs = mpSh.mPBoundSql();
        mpBs.sql(parserMulti(mpBs.sql(), context));
    }

    @Override
    protected void processSelect(Select select, int index, String sql, Object condition) {
        OrgPermissionContext context = (OrgPermissionContext) condition;
        if (select instanceof PlainSelect plainSelect) {
            appendCondition(plainSelect, buildOrgPermissionExpression(
                context.userId(), qualifyColumn(plainSelect.getFromItem(), context.column())));
        } else if (select instanceof SetOperationList setOpList) {
            List<Select> selects = setOpList.getSelects();
            selects.forEach(s -> {
                if (s instanceof PlainSelect ps) {
                    appendCondition(ps, buildOrgPermissionExpression(
                        context.userId(), qualifyColumn(ps.getFromItem(), context.column())));
                }
            });
        }
    }

    @Override
    protected void processUpdate(Update update, int index, String sql, Object condition) {
        OrgPermissionContext context = (OrgPermissionContext) condition;
        Expression cond = buildOrgPermissionExpression(context.userId(), qualifyColumn(update.getTable(), context.column()));
        update.setWhere(appendCondition(update.getWhere(), cond));
    }

    @Override
    protected void processDelete(Delete delete, int index, String sql, Object condition) {
        OrgPermissionContext context = (OrgPermissionContext) condition;
        Expression cond = buildOrgPermissionExpression(context.userId(), qualifyColumn(delete.getTable(), context.column()));
        delete.setWhere(appendCondition(delete.getWhere(), cond));
    }

    private void appendCondition(PlainSelect plainSelect, Expression conditionExpr) {
        plainSelect.setWhere(appendCondition(plainSelect.getWhere(), conditionExpr));
    }

    private Expression appendCondition(Expression where, Expression conditionExpr) {
        return where == null ? conditionExpr : new AndExpression(new ParenthesedExpressionList<>(where), conditionExpr);
    }

    private OrgPermissionContext resolveOrgPermissionContext(MappedStatement ms) {
        if (InterceptorIgnoreHelper.willIgnoreDataPermission(ms.getId())) {
            return null;
        }
        OrgPermission permission = OrgPermissionHelper.getPermission();
        if (permission == null) {
            return null;
        }
        boolean superAdmin;
        Long userId;
        try {
            superAdmin = LoginHelper.isSuperAdmin() || LoginHelper.isTenantAdmin();
            userId = LoginHelper.getUserId();
        } catch (SaTokenContextException e) {
            // 非 HTTP 请求上下文（启动阶段、定时任务等），跳过过滤
            return null;
        }
        if (superAdmin || userId == null) {
            return null;
        }

        String column = permission.value();
        if (!column.matches("[a-zA-Z0-9_]+")) {
            throw new ServiceException("非法的权限列名: " + column);
        }
        return new OrgPermissionContext(userId, column);
    }

    private String qualifyColumn(Table table, String column) {
        if (table == null) {
            return column;
        }
        if (table.getAlias() != null && table.getAlias().getName() != null) {
            return table.getAlias().getName() + "." + column;
        }
        return table.getName() + "." + column;
    }

    private String qualifyColumn(FromItem fromItem, String column) {
        if (fromItem instanceof Table table) {
            return qualifyColumn(table, column);
        }
        return column;
    }

    /**
     * 编程构造组织权限 WHERE 条件,等价于:
     *   (NOT EXISTS (SELECT 1 FROM pr_data_grant WHERE user_id = ? AND del_flag = '0')
     *    OR <column> IN (SELECT org_id FROM pr_data_grant WHERE user_id = ? AND del_flag = '0'))
     */
    private Expression buildOrgPermissionExpression(Long userId, String column) {
        // 内层子查询 1: SELECT 1 FROM pr_data_grant WHERE user_id = userId AND del_flag = '0'
        ParenthesedSelect existsSubquery = buildGrantSubquery(userId, new LongValue(1L));
        NotExpression notExists = new NotExpression(new ExistsExpression().withRightExpression(existsSubquery));

        // 内层子查询 2: SELECT org_id FROM pr_data_grant WHERE user_id = userId AND del_flag = '0'
        ParenthesedSelect orgIdSubquery = buildGrantSubquery(userId, new Column(COL_ORG_ID));
        InExpression inExpr = new InExpression();
        inExpr.setLeftExpression(new Column(column));
        inExpr.setRightExpression(orgIdSubquery);

        // (notExists OR inExpr)
        OrExpression orExpr = new OrExpression(notExists, inExpr);
        return new ParenthesedExpressionList<>(orExpr);
    }

    /**
     * 构造 (SELECT <projection> FROM pr_data_grant WHERE user_id = :userId AND del_flag = '0')。
     */
    private ParenthesedSelect buildGrantSubquery(Long userId, Expression projection) {
        PlainSelect inner = new PlainSelect();
        List<SelectItem<?>> items = new ArrayList<>();
        items.add(new SelectItem<>(projection));
        inner.setSelectItems(items);
        inner.setFromItem(new Table(GRANT_TABLE));

        EqualsTo userIdEq = new EqualsTo(new Column(COL_USER_ID), new LongValue(userId));
        EqualsTo delFlagEq = new EqualsTo(new Column(COL_DEL_FLAG), DEL_FLAG_ACTIVE);
        inner.setWhere(new AndExpression(userIdEq, delFlagEq));

        ParenthesedSelect parens = new ParenthesedSelect();
        parens.setSelect(inner);
        return parens;
    }

    record OrgPermissionContext(Long userId, String column) {
    }
}

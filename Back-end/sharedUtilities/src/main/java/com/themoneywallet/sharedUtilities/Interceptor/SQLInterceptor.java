package com.themoneywallet.sharedUtilities.Interceptor;


import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.hibernate.stat.Statistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.*;
import java.sql.*;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SQLInterceptor implements StatementInspector {

    private static final Logger logger = LoggerFactory.getLogger(SQLInterceptor.class);
    private static final Set<String> loggedQueries = ConcurrentHashMap.newKeySet();
    private static final String FILE_PATH = "sql-unique.log";

    private static DataSource dataSource;
    private static SessionFactoryImplementor sessionFactory;

    public static void setDataSource(DataSource ds) {
        dataSource = ds;
    }

    public static void setSessionFactory(SessionFactory sf) {
        if (sf instanceof SessionFactoryImplementor) {
            sessionFactory = (SessionFactoryImplementor) sf;
        }
    }

    @Override
    public String inspect(String sql) {
        if (isQuery(sql) && loggedQueries.add(sql)) {
            writeToFile(sql);
            explainPlan(sql);
            logStats();
            logger.debug("Captured: {}", sql);
        }
        return sql;
    }

    private boolean isQuery(String sql) {
        if (sql == null) return false;
        String q = sql.trim().toLowerCase();
        return q.startsWith("select") || q.startsWith("insert") || q.startsWith("update") || q.startsWith("delete");
    }

    private void writeToFile(String query) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(FILE_PATH, true)))) {
            out.println(query);
        } catch (IOException e) {
            logger.error("Write failed", e);
        }
    }

    private void explainPlan(String sql) {
        if (dataSource == null || !sql.trim().toLowerCase().startsWith("select")) return;
        String explainSQL = "EXPLAIN " + sql;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(explainSQL);
             ResultSet rs = ps.executeQuery()) {

            logger.debug("EXPLAIN for: {}", sql);
            int colCount = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i <= colCount; i++) {
                    sb.append(rs.getMetaData().getColumnLabel(i)).append("=").append(rs.getString(i)).append(" ");
                }
                logger.debug(sb.toString());
            }

        } catch (Exception e) {
            logger.warn("EXPLAIN failed: {}", e.getMessage());
        }
    }

    private void logStats() {
        if (sessionFactory == null) return;
        Statistics stats = sessionFactory.getStatistics();
        logger.debug("Hibernate → Count: {}, Max: {} ms",
                stats.getQueryExecutionCount(),
                stats.getQueryExecutionMaxTime());
    }
}
package com.themoneywallet.config.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Minimal JDBC utility showcasing parameterised query execution. <br/>
 * NOTE: This class belongs to legacy code and should be replaced by a proper
 * Spring Data repository. It is kept only for backward-compatibility demos.
 */
@Deprecated
public class MyFirstJdbc {

    private static final String URL = System.getenv().getOrDefault("LEGACY_DB_URL", "jdbc:mysql://localhost:3306/your_db");
    private static final String USER = System.getenv().getOrDefault("LEGACY_DB_USER", "root");
    private static final String PASSWORD = System.getenv().getOrDefault("LEGACY_DB_PASSWORD", "your_password");

    public void printTestRows() {
        String sql = "SELECT * FROM test";
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                System.out.println(rs.getString(1));
            }

        } catch (SQLException e) {
            // Log and propagate as unchecked for simplicity in legacy code
            throw new RuntimeException("Database operation failed", e);
        }
    }
}

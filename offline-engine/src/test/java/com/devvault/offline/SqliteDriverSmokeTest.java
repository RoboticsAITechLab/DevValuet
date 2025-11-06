package com.devvault.offline;

import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;

public class SqliteDriverSmokeTest {

    @Test
    public void sqliteInMemoryConnects() throws Exception {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite::memory:")) {
            try (Statement st = conn.createStatement()) {
                try (ResultSet rs = st.executeQuery("select sqlite_version()")) {
                    assertThat(rs.next()).isTrue();
                    String ver = rs.getString(1);
                    assertThat(ver).isNotBlank();
                }
            }
        }
    }
}

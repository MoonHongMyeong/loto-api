package gg.loto.common.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import java.sql.*;

@SpringBootTest
public class DatabaseConnectionTest {

    @Nested
    @ActiveProfiles("h2")
    class H2DatabaseConnectionTest {

        @Autowired
        private DataSource dataSource;

        @Test
        @DisplayName("H2 연결 테스트")
        void h2ConnectionTest() {
            try (Connection connection = dataSource.getConnection()) {
                assertThat(connection).isNotNull();
                assertThat(connection.isValid(1)).isTrue();

                DatabaseMetaData metaData = connection.getMetaData();
                System.out.println("=== H2 Connection Info ===");
                System.out.println("Database: " + metaData.getDatabaseProductName());
                System.out.println("Version: " + metaData.getDatabaseProductVersion());
                System.out.println("URL: " + metaData.getURL());
                
            } catch (SQLException e) {
                fail("H2 연결 실패: " + e.getMessage());
            }
        }

        @Test
        @DisplayName("H2 statement 쿼리 실행 테스트")
        void h2QueryTest() {
            try (Connection connection = dataSource.getConnection();
                 Statement statement = connection.createStatement()) {
                
                // when
                ResultSet resultSet = statement.executeQuery("SELECT 1");

                // then
                assertThat(resultSet.next()).isTrue();
                assertThat(resultSet.getInt(1)).isEqualTo(1);
                
                // DB 타입 확인
                String dbType = connection.getMetaData().getDatabaseProductName();
                assertThat(dbType.toLowerCase()).contains("h2");
                
            } catch (SQLException e) {
                fail("H2 쿼리 실행 실패: " + e.getMessage());
            }
        }
    }

    @Nested
    @ActiveProfiles("mysql")
    class MySQLDatabaseConnectionTest {

        @Autowired
        private DataSource dataSource;

        @Test
        @DisplayName("MySQL 연결 테스트")
        void mysqlConnectionTest() {
            try (Connection connection = dataSource.getConnection()) {
                assertThat(connection).isNotNull();
                assertThat(connection.isValid(1)).isTrue();

                DatabaseMetaData metaData = connection.getMetaData();
                System.out.println("=== MySQL Connection Info ===");
                System.out.println("Database: " + metaData.getDatabaseProductName());
                System.out.println("Version: " + metaData.getDatabaseProductVersion());
                System.out.println("URL: " + metaData.getURL());
            } catch (SQLException e) {
                fail("MySQL 연결 실패: " + e.getMessage());
            }
        }

        @Test
        @DisplayName("MySQL statement 쿼리 실행 테스트")
        void mysqlQueryTest() {
            try (Connection connection = dataSource.getConnection();
                 Statement statement = connection.createStatement()) {
                
                // when
                ResultSet resultSet = statement.executeQuery("SELECT 1");

                // then
                assertThat(resultSet.next()).isTrue();
                assertThat(resultSet.getInt(1)).isEqualTo(1);
                
                // DB 타입 확인
                String dbType = connection.getMetaData().getDatabaseProductName();
                assertThat(dbType.toLowerCase()).contains("mysql");
                
            } catch (SQLException e) {
                fail("MySQL 쿼리 실행 실패: " + e.getMessage());
            }
        }
    }
}
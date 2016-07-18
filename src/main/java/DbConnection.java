import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Prajjwal Raj Kandel<prajjwalkandel@lftechnology.com>
 *
 */
public class DbConnection {

    public static Connection getPostgresConnection() throws Exception {
        String driver = "org.postgresql.Driver";
        String url = "jdbc:postgresql://localhost:5433/vyaguta_core_dev";
        String username = "postgres";
        String password = "password";
        Class.forName(driver); // load postgresql driver
        Connection conn = DriverManager.getConnection(url, username, password);
        return conn;
    }

    public static Connection getMySqlConnection() throws Exception {
        String driver = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/lms";
        String username = "root";
        String password = "lagrangian";
        Class.forName(driver); // load MySQL driver
        Connection conn = DriverManager.getConnection(url, username, password);
        return conn;
    }

    public static void main(String[] args) {
        Connection postgresConnection = null;
        Connection mysqlConnection = null;
        try {
            postgresConnection = getPostgresConnection();
            mysqlConnection = getMySqlConnection();
            System.out.println("PostgresConnection=" + postgresConnection);
            System.out.println("mysqlConnection=" + mysqlConnection);

            List<Map<String, Object>> userList = new ArrayList<Map<String, Object>>();
            PreparedStatement psMySql = mysqlConnection.prepareStatement("select username,join_date,dateof_birth,resigned_date from user");
            ResultSet rsMysql = psMySql.executeQuery();
            while (rsMysql.next()) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("userName", rsMysql.getString("username"));
                map.put("joinDate", rsMysql.getDate("join_date"));
                map.put("dob", rsMysql.getDate("dateof_birth"));
                map.put("resignedDate", rsMysql.getDate("resigned_date"));
                userList.add(map);
            }

            PreparedStatement psPostgres = postgresConnection
                    .prepareStatement("update employees set joined_date = ?,dob = ?,end_date = ? where primary_email = ?");

            for (Map<String, Object> map2 : userList) {
                System.out.println(map2.get("dob"));
                psPostgres.setDate(1, (Date) map2.get("joinDate"));
                psPostgres.setDate(2, (Date) map2.get("dob"));
                psPostgres.setDate(3, (Date) map2.get("resignedDate"));
                psPostgres.setString(4, (String) map2.get("userName"));
                System.out.println(psPostgres);
                psPostgres.executeUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        finally {
            try {
                postgresConnection.close();
                mysqlConnection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

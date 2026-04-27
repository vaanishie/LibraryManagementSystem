import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DatabaseConnectivity {

    public static Connection connect() {
        try {
            Properties props = new Properties();
            InputStream input = DatabaseConnectivity.class
                    .getClassLoader()
                    .getResourceAsStream("config.properties");

            if (input == null) {
                System.err.println("config.properties not found on classpath");
                return null;
            }

            props.load(input);

            String url      = props.getProperty("db.url");
            String username = props.getProperty("db.username");
            String password = props.getProperty("db.password");

            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, username, password);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

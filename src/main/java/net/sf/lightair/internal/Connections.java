package net.sf.lightair.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;

public class Connections {

    private static final Logger log = LoggerFactory.getLogger(Connections.class);

    public static final String DATABASE_DRIVER_CLASS_NAME = "database.driverClassName";
    public static final String DATABASE_CONNECTION_URL = "database.connectionUrl";
    public static final String DATABASE_USER_NAME = "database.userName";
    public static final String DATABASE_PASSWORD = "database.password";

    public static Connection open(String profile, Map<String, String> profileProperties) {
        String driverClassName = profileProperties.get(DATABASE_DRIVER_CLASS_NAME);
        String url = profileProperties.get(DATABASE_CONNECTION_URL);
        String userName = profileProperties.get(DATABASE_USER_NAME);
        String password = profileProperties.get(DATABASE_PASSWORD);

        Objects.requireNonNull(driverClassName, "Driver class name is required.");
        Objects.requireNonNull(url, "Database URL is required.");
        Objects.requireNonNull(userName, "Database user name is required.");
        Objects.requireNonNull(password, "Database password is required.");

        log.debug("Opening new connection for profile [{}] at [{}].", profile, url);
        try {
            Class.forName(driverClassName);
            Connection connection = DriverManager.getConnection(url, userName, password);
            log.info("Connected for profile [{}] to [{}].", profile, url);
            return connection;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Cannot find driver class " + driverClassName, e);
        } catch (SQLException e) {
            throw new RuntimeException("Cannot connect to database URL " + url, e);
        }
    }

    public static void close(String profile, Connection connection) {
        if (null != connection) {
            try {
                connection.close();
                log.info("Closed connection for profile [{}].", profile);
            } catch (SQLException e) {
                log.error("Cannot close connection for profile [" + profile + "].", e);
            }
        }
    }
}

package famiglia.sapori.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static volatile DatabaseConnection instance;
    private Connection connection;
    private Properties dbProperties;

    private DatabaseConnection() throws SQLException {
        loadDatabaseProperties();
        try {
            Class.forName(dbProperties.getProperty("db.driver"));
            this.connection = DriverManager.getConnection(
                    dbProperties.getProperty("db.url"),
                    dbProperties.getProperty("db.username"),
                    dbProperties.getProperty("db.password"));
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL non trovato", e);
        } catch (SQLException e) {
            throw new SQLException("Errore nella connessione al database: " + e.getMessage(), e);
        }
    }

    private void loadDatabaseProperties() throws SQLException {
        dbProperties = new Properties();
        // Usa getResourceAsStream con path assoluto per sicurezza
        try (InputStream input = DatabaseConnection.class.getResourceAsStream("/database.properties")) {
            if (input == null) {
                throw new SQLException("File database.properties non trovato nel classpath");
            }
            dbProperties.load(input);
        } catch (IOException e) {
            throw new SQLException("Errore nel caricamento del file database.properties", e);
        }
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            // Riconnetti automaticamente se la connessione è chiusa
            synchronized (DatabaseConnection.class) {
                if (connection == null || connection.isClosed()) {
                    try {
                        Class.forName(dbProperties.getProperty("db.driver"));
                        this.connection = DriverManager.getConnection(
                                dbProperties.getProperty("db.url"),
                                dbProperties.getProperty("db.username"),
                                dbProperties.getProperty("db.password"));
                    } catch (ClassNotFoundException e) {
                        throw new SQLException("Driver non trovato durante la riconnessione", e);
                    }
                }
            }
        }
        return connection;
    }

    public static DatabaseConnection getInstance() throws SQLException {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Errore nella chiusura della connessione: " + e.getMessage());
        }
    }
}

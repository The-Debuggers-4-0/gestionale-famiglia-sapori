package database;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Test per la connessione al database
 */
public class DatabaseConnectionTest {

    /**
     * Testa che la connessione al database MySQL sia attiva e funzionante
     */
    @Test
    public void testGetConnection() throws Exception {
        // Act
        try (Connection connection = DatabaseConnection.getInstance().getConnection()) {
            // Assert
            assertNotNull(connection, "La connessione non dovrebbe essere null");
            assertFalse(connection.isClosed(), "La connessione dovrebbe essere aperta");
        } // Auto-close connection
    }

    /**
     * Testa che getInstance restituisca un'istanza valida (non null)
     */
    @Test
    public void testGetInstanceNotNull() throws SQLException {
        // Act
        DatabaseConnection instance = DatabaseConnection.getInstance();

        // Assert
        assertNotNull(instance, "L'istanza DatabaseConnection non dovrebbe essere null");
    }

    /**
     * Testa che DatabaseConnection implementi correttamente il pattern Singleton
     */
    @Test
    public void testSingletonPattern() throws SQLException {
        // Act
        DatabaseConnection instance1 = DatabaseConnection.getInstance();
        DatabaseConnection instance2 = DatabaseConnection.getInstance();

        // Assert
        assertSame(instance1, instance2, "DatabaseConnection dovrebbe seguire il pattern Singleton");
    }


}
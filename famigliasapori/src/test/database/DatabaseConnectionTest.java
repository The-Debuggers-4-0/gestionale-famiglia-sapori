package famiglia.sapori.database;

import famiglia.sapori.testutil.DatabaseTestBase;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseConnectionTest extends DatabaseTestBase {

    /**
     * Verifica il pattern Singleton di DatabaseConnection e che la connessione sia aperta.
     */
    @Test
    void getInstance_returnsSingleton_andConnectionIsOpen() throws Exception {
        DatabaseConnection db1 = DatabaseConnection.getInstance();
        DatabaseConnection db2 = DatabaseConnection.getInstance();
        assertSame(db1, db2);
        Connection c = db1.getConnection();
        assertNotNull(c);
        assertFalse(c.isClosed());
    }

    /**
     * Controlla la chiusura della connessione e la riconnessione al successivo accesso.
     */
    @Test
    void closeConnection_thenReconnectOnNextGetInstance() throws Exception {
        DatabaseConnection db = DatabaseConnection.getInstance();
        db.closeConnection();
        // Should reconnect internally when accessing connection again
        Connection c2 = DatabaseConnection.getInstance().getConnection();
        assertNotNull(c2);
        assertFalse(c2.isClosed());
    }
}

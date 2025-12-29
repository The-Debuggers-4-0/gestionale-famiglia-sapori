package famiglia.sapori.dao;

import famiglia.sapori.database.DatabaseConnection;
import famiglia.sapori.model.Comanda;
import famiglia.sapori.database.DatabaseTestBase;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per verificare gestione eccezioni SQLException nei DAO.
 * Estende DatabaseTestBase per usare H2 in-memory con schema corretto.
 */
public class DAOExceptionTest extends DatabaseTestBase {

    private static Properties loadDbProperties() throws IOException {
        Properties props = new Properties();
        try (InputStream input = DAOExceptionTest.class.getResourceAsStream("/database.properties")) {
            if (input == null) {
                throw new IOException("database.properties not found on classpath");
            }
            props.load(input);
        }
        return props;
    }

    /**
     * Verifica che operazioni su connessione chiusa causino SQLException.
     */
    @Test
    void operationOnClosedConnection_throwsSQLException() throws SQLException {
        // Crea una connessione indipendente e chiudila (non toccare il singleton
        // condiviso)
        Properties props;
        try {
            props = loadDbProperties();
        } catch (IOException e) {
            throw new SQLException("Impossibile caricare database.properties", e);
        }

        Connection conn = DriverManager.getConnection(
                props.getProperty("db.url"),
                props.getProperty("db.username"),
                props.getProperty("db.password"));
        conn.close();

        // Verifica che la connessione sia chiusa
        assertTrue(conn.isClosed(), "La connessione dovrebbe essere chiusa");

        // Tentativo di operazione su connessione chiusa dovrebbe causare errore
        assertThrows(SQLException.class, conn::createStatement);
    }

    /**
     * Verifica che query con sintassi SQL errata causino SQLException.
     */
    @Test
    void invalidSQLQuery_throwsSQLException() {
        assertThrows(SQLException.class, () -> {
            DatabaseConnection.getInstance().getConnection()
                    .createStatement()
                    .executeQuery("SELECT * FROM NonExistentTable");
        });
    }

    /**
     * Verifica che inserimento con constraint violation (FK) sollevi SQLException.
     * Con i foreign key abilitati, H2 correttamente solleva un'eccezione.
     */
    @Test
    void insertWithInvalidForeignKey_handlesGracefully() {
        ComandaDAO dao = new ComandaDAO();
        // idTavolo 9999 non esiste nel database di test
        Comanda c = new Comanda(0, 9999, "Test", 5.00, "Cucina", "In Attesa", LocalDateTime.now(), "", 1);

        // Con FK abilitati, deve sollevare SQLException per violazione constraint
        assertThrows(SQLException.class, () -> dao.insertComanda(c));
    }

    /**
     * Verifica che update su ID inesistente non causi eccezione ma nessun effetto.
     */
    @Test
    void updateNonExistentId_noException() throws SQLException {
        ComandaDAO dao = new ComandaDAO();
        // Update su ID inesistente non dovrebbe causare eccezione
        assertDoesNotThrow(() -> dao.updateStatoComanda(99999, "Servita"));
    }
}

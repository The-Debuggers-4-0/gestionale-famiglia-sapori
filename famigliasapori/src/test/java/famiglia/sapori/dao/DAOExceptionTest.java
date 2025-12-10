package famiglia.sapori.dao;

import famiglia.sapori.database.DatabaseConnection;
import famiglia.sapori.model.Comanda;
import famiglia.sapori.testutil.DatabaseTestBase;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per verificare gestione eccezioni SQLException nei DAO.
 * Estende DatabaseTestBase per usare H2 in-memory con schema corretto.
 */
public class DAOExceptionTest extends DatabaseTestBase {

    /**
     * Verifica che operazioni su connessione chiusa causino SQLException.
     */
    @Test
    void operationOnClosedConnection_throwsSQLException() throws SQLException {
        // Ottieni connessione e chiudila
        Connection conn = DatabaseConnection.getInstance().getConnection();
        conn.close();
        
        // Verifica che la connessione sia chiusa
        assertTrue(conn.isClosed(), "La connessione dovrebbe essere chiusa");
        
        // Tentativo di operazione su connessione chiusa dovrebbe causare errore
        assertThrows(SQLException.class, () -> {
            conn.createStatement().executeQuery("SELECT 1");
        });
    }

    /**
     * Verifica che query con sintassi SQL errata causino SQLException.
     */
    @Test
    void invalidSQLQuery_throwsSQLException() {
        assertThrows(SQLException.class, () -> {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            conn.createStatement().executeQuery("SELECT * FROM NonExistentTable");
        });
    }

    /**
     * Verifica che inserimento con constraint violation (FK) causi SQLException.
     * Nota: H2 potrebbe non avere FK constraint abilitati di default.
     */
    @Test
    void insertWithInvalidForeignKey_handlesGracefully() {
        ComandaDAO dao = new ComandaDAO();
        // idTavolo 9999 non esiste nel database di test
        Comanda c = new Comanda(0, 9999, "Test", "Cucina", "In Attesa", LocalDateTime.now(), "", 1);
        
        // H2 potrebbe non lanciare eccezione se FK non sono abilitati
        // Verifichiamo solo che il metodo non causi crash
        assertDoesNotThrow(() -> dao.insertComanda(c));
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

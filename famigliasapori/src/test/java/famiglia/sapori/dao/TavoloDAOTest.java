package famiglia.sapori.dao;

import famiglia.sapori.model.Tavolo;
import famiglia.sapori.database.DatabaseTestBase;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

public class TavoloDAOTest extends DatabaseTestBase {

    /**
     * Verifica che la lista tavoli contenga righe e
     * includa almeno uno stato "Occupato".
     */
    @Test
    void getAllTavoli_returnsRows() throws SQLException {
        TavoloDAO dao = new TavoloDAO();
        List<Tavolo> tavoli = dao.getAllTavoli();
        assertTrue(tavoli.size() >= 2);
        assertTrue(tavoli.stream().anyMatch(t -> "Occupato".equals(t.getStato())));
    }

    /**
     * Controlla l'aggiornamento dello stato del tavolo e la persistenza del cambiamento.
     */
    @Test
    void updateStatoTavolo_changesState() throws SQLException {
        TavoloDAO dao = new TavoloDAO();
        Tavolo t = dao.getAllTavoli().get(0);
        String newState = "Occupato".equals(t.getStato()) ? "Libero" : "Occupato";
        dao.updateStatoTavolo(t.getId(), newState);
        Tavolo updated = dao.getAllTavoli().stream().filter(x -> x.getId() == t.getId()).findFirst().orElseThrow();
        assertEquals(newState, updated.getStato());
    }

    /**
     * Verifica la transizione completa Libero → Occupato → Libero.
     */
    @Test
    void updateStatoTavolo_liberoToOccupatoToLibero() throws SQLException {
        TavoloDAO dao = new TavoloDAO();
        List<Tavolo> tavoli = dao.getAllTavoli();
        Tavolo libero = tavoli.stream().filter(t -> "Libero".equals(t.getStato())).findFirst().orElseThrow();
        
        // Libero → Occupato
        dao.updateStatoTavolo(libero.getId(), "Occupato");
        Tavolo afterOccupato = dao.getAllTavoli().stream().filter(t -> t.getId() == libero.getId()).findFirst().orElseThrow();
        assertEquals("Occupato", afterOccupato.getStato());
        
        // Occupato → Libero
        dao.updateStatoTavolo(libero.getId(), "Libero");
        Tavolo afterLibero = dao.getAllTavoli().stream().filter(t -> t.getId() == libero.getId()).findFirst().orElseThrow();
        assertEquals("Libero", afterLibero.getStato());
    }

    /**
     * Verifica che l'aggiornamento ripetuto dello stesso stato sia idempotente.
     */
    @Test
    void updateStatoTavolo_sameState_idempotent() throws SQLException {
        TavoloDAO dao = new TavoloDAO();
        Tavolo t = dao.getAllTavoli().get(0);
        String currentState = t.getStato();
        
        dao.updateStatoTavolo(t.getId(), currentState);
        Tavolo after = dao.getAllTavoli().stream().filter(x -> x.getId() == t.getId()).findFirst().orElseThrow();
        assertEquals(currentState, after.getStato(), "Aggiornare con lo stesso stato dovrebbe essere idempotente");
    }

    @Test
    void insertUpdateDeleteTavolo_roundTrip() throws SQLException {
        TavoloDAO dao = new TavoloDAO();

        int uniqueNumero = 10_000 + ThreadLocalRandom.current().nextInt(1_000);
        Tavolo toInsert = new Tavolo(0, uniqueNumero, "Libero", 5, "note test");
        dao.insertTavolo(toInsert);

        List<Tavolo> afterInsert = dao.getAllTavoli();
        Tavolo inserted = afterInsert.stream()
                .filter(t -> t.getNumero() == uniqueNumero)
                .findFirst()
                .orElseThrow();
        assertEquals(5, inserted.getPosti());
        assertEquals("Libero", inserted.getStato());

        Tavolo toUpdate = new Tavolo(inserted.getId(), uniqueNumero + 1, "Occupato", 2, "note upd");
        dao.updateTavolo(toUpdate);

        Tavolo updated = dao.getAllTavoli().stream().filter(t -> t.getId() == inserted.getId()).findFirst().orElseThrow();
        assertEquals(uniqueNumero + 1, updated.getNumero());
        assertEquals("Occupato", updated.getStato());
        assertEquals(2, updated.getPosti());
        assertEquals("note upd", updated.getNote());

        dao.deleteTavolo(inserted.getId());
        List<Tavolo> afterDelete = dao.getAllTavoli();
        assertTrue(afterDelete.stream().noneMatch(t -> t.getId() == inserted.getId()));
    }
}


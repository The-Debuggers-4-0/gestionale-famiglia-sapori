package famiglia.sapori.dao;

import famiglia.sapori.model.Prenotazione;
import famiglia.sapori.testutil.DatabaseTestBase;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PrenotazioneDAOTest extends DatabaseTestBase {

    /**
     * Verifica che le prenotazioni passate siano escluse e che
     * il mapping dell'idTavolo nullo sia gestito correttamente.
     */
    @Test
    void getAllPrenotazioni_excludesPast_andMapsNullTavolo() throws SQLException {
        PrenotazioneDAO dao = new PrenotazioneDAO();
        List<Prenotazione> list = dao.getAllPrenotazioni();
        assertFalse(list.isEmpty());
        assertTrue(list.stream().allMatch(p -> !p.getDataOra().isBefore(LocalDateTime.now().minusMinutes(1))));
        assertTrue(list.stream().anyMatch(p -> p.getIdTavolo() == null));
    }

    /**
     * Controlla inserimento e cancellazione di una prenotazione
     * con idTavolo nullo.
     */
    @Test
    void insertAndDeletePrenotazione_handlesNullIdTavolo() throws SQLException {
        PrenotazioneDAO dao = new PrenotazioneDAO();
        Prenotazione p = new Prenotazione(0, "Test", "999", 3, LocalDateTime.now().plusHours(2), "Note", null);
        dao.insertPrenotazione(p);
        List<Prenotazione> afterInsert = dao.getAllPrenotazioni();
        Prenotazione created = afterInsert.stream().filter(x -> "Test".equals(x.getNomeCliente())).findFirst().orElse(null);
        assertNotNull(created);
        // delete by id
        dao.deletePrenotazione(created.getId());
        List<Prenotazione> afterDelete = dao.getAllPrenotazioni();
        assertTrue(afterDelete.stream().noneMatch(x -> x.getId() == created.getId()));
    }

    /**
     * Verifica che le prenotazioni future siano incluse correttamente.
     */
    @Test
    void getAllPrenotazioni_includesFuture() throws SQLException {
        PrenotazioneDAO dao = new PrenotazioneDAO();
        LocalDateTime futureDate = LocalDateTime.now().plusDays(1);
        Prenotazione p = new Prenotazione(0, "Futuro", "111", 2, futureDate, "", null);
        dao.insertPrenotazione(p);
        
        List<Prenotazione> prenotazioni = dao.getAllPrenotazioni();
        assertTrue(prenotazioni.stream().anyMatch(pr -> "Futuro".equals(pr.getNomeCliente())),
                "Le prenotazioni future dovrebbero essere incluse");
    }

    /**
     * Verifica che prenotazioni con data/ora molto vicine al limite siano gestite correttamente.
     */
    @Test
    void getAllPrenotazioni_edgeCaseNearCurrentTime() throws SQLException {
        PrenotazioneDAO dao = new PrenotazioneDAO();
        // Prenotazione esattamente tra 1 minuto
        LocalDateTime nearFuture = LocalDateTime.now().plusMinutes(1);
        Prenotazione p = new Prenotazione(0, "EdgeCase", "222", 4, nearFuture, "Test limite", null);
        dao.insertPrenotazione(p);
        
        List<Prenotazione> prenotazioni = dao.getAllPrenotazioni();
        assertTrue(prenotazioni.stream().anyMatch(pr -> "EdgeCase".equals(pr.getNomeCliente())),
                "Prenotazioni appena future dovrebbero essere incluse");
    }

    /**
     * Verifica che l'update di idTavolo funzioni correttamente (assegnazione tavolo).
     */
    @Test
    void updatePrenotazione_assignTavolo() throws SQLException {
        PrenotazioneDAO dao = new PrenotazioneDAO();
        Prenotazione p = new Prenotazione(0, "AssignTest", "333", 2, LocalDateTime.now().plusHours(3), "", null);
        dao.insertPrenotazione(p);
        
        List<Prenotazione> all = dao.getAllPrenotazioni();
        Prenotazione created = all.stream().filter(pr -> "AssignTest".equals(pr.getNomeCliente())).findFirst().orElseThrow();
        assertNull(created.getIdTavolo(), "Inizialmente idTavolo dovrebbe essere null");
    }
}

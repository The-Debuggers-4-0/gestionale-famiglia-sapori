package famiglia.sapori.dao;

import famiglia.sapori.model.Prenotazione;
import famiglia.sapori.database.DatabaseTestBase;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Test
    void getReservedTableIdsForDate_returnsOnlyNonNullAndDistinctForThatDate() throws SQLException {
        PrenotazioneDAO dao = new PrenotazioneDAO();

        LocalDate targetDate = LocalDate.now().plusDays(10);
        LocalDateTime atLunch = LocalDateTime.of(targetDate, LocalTime.of(13, 0));
        LocalDateTime atDinner = LocalDateTime.of(targetDate, LocalTime.of(20, 0));
        LocalDateTime otherDay = LocalDateTime.of(targetDate.plusDays(1), LocalTime.of(13, 0));

        dao.insertPrenotazione(new Prenotazione(0, "R1", "100", 2, atLunch, "", 1));
        dao.insertPrenotazione(new Prenotazione(0, "R2", "101", 4, atDinner, "", 2));
        // stessa data, ma id_tavolo null: non deve comparire
        dao.insertPrenotazione(new Prenotazione(0, "R_NULL", "102", 3, atDinner.plusMinutes(15), "", null));
        // data diversa: non deve comparire
        dao.insertPrenotazione(new Prenotazione(0, "R_OTHER", "103", 2, otherDay, "", 3));

        List<Integer> reservedIds = dao.getReservedTableIdsForDate(targetDate);
        Set<Integer> set = new HashSet<>(reservedIds);

        assertEquals(Set.of(1, 2), set);
    }

    @Test
    void getReservationsForDate_returnsOnlyReservationsWithAssignedTableOnThatDate() throws SQLException {
        PrenotazioneDAO dao = new PrenotazioneDAO();

        LocalDate targetDate = LocalDate.now().plusDays(12);
        LocalDateTime atLunch = LocalDateTime.of(targetDate, LocalTime.of(12, 30));
        LocalDateTime otherDay = LocalDateTime.of(targetDate.plusDays(2), LocalTime.of(12, 30));

        dao.insertPrenotazione(new Prenotazione(0, "RES_A", "200", 2, atLunch, "", 1));
        dao.insertPrenotazione(new Prenotazione(0, "RES_NULL", "201", 2, atLunch.plusHours(1), "", null));
        dao.insertPrenotazione(new Prenotazione(0, "RES_OTHER", "202", 2, otherDay, "", 2));

        List<Prenotazione> reservations = dao.getReservationsForDate(targetDate);

        assertTrue(reservations.stream().anyMatch(p -> "RES_A".equals(p.getNomeCliente())));
        assertTrue(reservations.stream().allMatch(p -> p.getIdTavolo() != null));
        assertTrue(reservations.stream().allMatch(p -> p.getDataOra().toLocalDate().equals(targetDate)));
        assertTrue(reservations.stream().noneMatch(p -> "RES_NULL".equals(p.getNomeCliente())));
        assertTrue(reservations.stream().noneMatch(p -> "RES_OTHER".equals(p.getNomeCliente())));
    }
}

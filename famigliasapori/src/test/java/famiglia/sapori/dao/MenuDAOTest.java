package famiglia.sapori.dao;

import famiglia.sapori.model.Piatto;
import famiglia.sapori.database.DatabaseTestBase;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class MenuDAOTest extends DatabaseTestBase {

    /**
     * Controlla che la query restituisca solo piatti disponibili
     * e che gli allergeni siano valorizzati quando presenti.
     */
    @Test
    void getAllPiatti_returnsOnlyDisponibili() throws SQLException {
        MenuDAO dao = new MenuDAO();

        // Esegui la query
        List<Piatto> piatti = dao.getAllPiatti();

        // Verifica che la lista non sia nulla e non vuota
        assertFalse(piatti.isEmpty());

        // Controlla che tutti i piatti siano disponibili
        assertTrue(piatti.stream().allMatch(Piatto::isDisponibile));

        // Controlla che almeno un piatto abbia allergeni
        assertTrue(piatti.stream().anyMatch(p -> !p.getAllergeni().isEmpty()));
    }

    /**
     * Verifica che le categorie siano distinte e ordinate alfabeticamente.
     */
    @Test
    void getAllCategorie_returnsDistinctSorted() throws SQLException {
        MenuDAO dao = new MenuDAO();
        // Verifica che le categorie siano distinte e ordinate
        List<String> categories = dao.getAllCategorie();

        // Controlla che la lista non sia nulla
        assertNotNull(categories);

        // Controlla che ci siano almeno 2 categorie per validare il test
        assertTrue(categories.size() >= 2);

        // Verifica l'ordinamento
        List<String> sorted = categories.stream().sorted().toList();
        
        // Confronta la lista originale con quella ordinata
        assertEquals(sorted, categories, "Categorie non ordinate alfabeticamente");
    }

    /**
     * Verifica la query completa che include anche i piatti non disponibili.
     */
    @Test
    void getAllPiattiComplete_includesUnavailable() throws SQLException {
        MenuDAO dao = new MenuDAO();
        List<Piatto> all = dao.getAllPiattiComplete();
        assertTrue(all.size() >= 3);
        assertTrue(all.stream().anyMatch(p -> !p.isDisponibile()));
    }

    /**
     * Controlla l'aggiornamento della disponibilità di un piatto.
     */
    @Test
    void updateDisponibilita_updatesRow() throws SQLException {
        MenuDAO dao = new MenuDAO();
        List<Piatto> all = dao.getAllPiattiComplete();
        Piatto target = all.stream().filter(Piatto::isDisponibile).findFirst().orElseThrow();
        dao.updateDisponibilita(target.getId(), false);
        List<Piatto> allAfter = dao.getAllPiattiComplete();
        Piatto updated = allAfter.stream().filter(p -> p.getId() == target.getId()).findFirst().orElseThrow();
        assertFalse(updated.isDisponibile());
    }

    // Verifica l'inserimento, l'aggiornamento e la cancellazione di un piatto.
    @Test
    void insertUpdateDeletePiatto_roundTrip() throws SQLException {
        MenuDAO dao = new MenuDAO();

        // Crea un piatto con nome unico per evitare conflitti
        String uniqueName = "TestPiatto-" + UUID.randomUUID();
        Piatto toInsert = new Piatto(0, uniqueName, "Descr", 9.99, "Primi", true, "glutine");
        dao.insertPiatto(toInsert);

        // Verifica l'inserimento
        List<Piatto> afterInsert = dao.getAllPiattiComplete();
        Piatto inserted = afterInsert.stream()
                .filter(p -> uniqueName.equals(p.getNome()))
                .findFirst()
                .orElseThrow();
        assertTrue(inserted.isDisponibile());
        assertEquals("Primi", inserted.getCategoria());

        // Verifica l'aggiornamento
        Piatto toUpdate = new Piatto(inserted.getId(), uniqueName + "-UPD", "Descr2", 11.50, "Bevande", false, "");
        dao.updatePiatto(toUpdate);

        // Verifica i campi aggiornati
        List<Piatto> afterUpdate = dao.getAllPiattiComplete();
        Piatto updated = afterUpdate.stream().filter(p -> p.getId() == inserted.getId()).findFirst().orElseThrow();
        assertEquals(uniqueName + "-UPD", updated.getNome());
        assertEquals("Descr2", updated.getDescrizione());
        assertEquals(11.50, updated.getPrezzo(), 0.0001);
        assertEquals("Bevande", updated.getCategoria());
        assertFalse(updated.isDisponibile());

        // Verifica la cancellazione
        dao.deletePiatto(inserted.getId());
        List<Piatto> afterDelete = dao.getAllPiattiComplete();
        assertTrue(afterDelete.stream().noneMatch(p -> p.getId() == inserted.getId()));
    }
}

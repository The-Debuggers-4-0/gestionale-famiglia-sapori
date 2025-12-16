package famiglia.sapori.dao;

import famiglia.sapori.model.Comanda;
import famiglia.sapori.database.DatabaseTestBase;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ComandaDAOTest extends DatabaseTestBase {

    /**
     * Verifica l'inserimento di una Comanda e la query per stato/tipo.
     * Usa H2 in-memory inizializzato da DatabaseTestBase per garantire isolamento.
     */
    @Test
    void insertComanda_andQueryByStatoAndTipo() throws SQLException {
        ComandaDAO dao = new ComandaDAO();
        Comanda c = new Comanda(0, 1, "Risotto", 10.00, "Cucina", "In Preparazione", LocalDateTime.now(), "", 1);
        dao.insertComanda(c);
        List<Comanda> cucinaInPrep = dao.getComandeByStatoAndTipo("In Preparazione", "Cucina");
        assertTrue(cucinaInPrep.stream().anyMatch(x -> x.getProdotti().contains("Risotto")));
    }

    /**
     * Controlla l'aggiornamento dello stato della Comanda e la corretta persistenza.
     */
    @Test
    void updateStatoComanda_changesState() throws SQLException {
        ComandaDAO dao = new ComandaDAO();
        List<Comanda> allInPrep = dao.getComandeByStato("In Preparazione");
        assertFalse(allInPrep.isEmpty());
        Comanda target = allInPrep.get(0);
        dao.updateStatoComanda(target.getId(), "Servita");
        List<Comanda> nowServite = dao.getComandeByStato("Servita");
        assertTrue(nowServite.stream().anyMatch(c -> c.getId() == target.getId()));
    }

    /**
     * Verifica la selezione delle comande da pagare e la marcatura come pagate.
     */
    @Test
    void getComandeDaPagare_andSetComandePagate() throws SQLException {
        ComandaDAO dao = new ComandaDAO();
        List<Comanda> due = dao.getComandeDaPagare(1);
        assertFalse(due.isEmpty());
        dao.setComandePagate(1);
        List<Comanda> after = dao.getComandeDaPagare(1);
        assertTrue(after.isEmpty(), "Tutte le comande dovrebbero essere marcate come Pagato");
    }
}


package famiglia.sapori.dao;

import famiglia.sapori.model.Comanda;
import famiglia.sapori.database.DatabaseConnection;
import famiglia.sapori.database.DatabaseTestBase;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ComandaDAOTest extends DatabaseTestBase {

    private static void insertComandaWithTimestamp(
            int idTavolo,
            String prodotti,
            double totale,
            String tipo,
            String stato,
            LocalDateTime dataOra,
            int idCameriere
    ) throws SQLException {
        String sql = "INSERT INTO Comande (id_tavolo, prodotti, totale, tipo, stato, data_ora, note, id_cameriere) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idTavolo);
            stmt.setString(2, prodotti);
            stmt.setDouble(3, totale);
            stmt.setString(4, tipo);
            stmt.setString(5, stato);
            stmt.setTimestamp(6, Timestamp.valueOf(dataOra));
            stmt.setString(7, "");
            stmt.setInt(8, idCameriere);
            stmt.executeUpdate();
        }
    }

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

    @Test
    void hasPaidComandaAfter_filtersByTableAndTimestamp() throws SQLException {
        ComandaDAO dao = new ComandaDAO();

        LocalDateTime t0 = LocalDateTime.of(2025, 1, 1, 12, 0, 0);
        insertComandaWithTimestamp(1, "1x Test Paid", 10.0, "Cucina", "Pagato", t0, 1);
        insertComandaWithTimestamp(1, "1x Test NotPaid", 5.0, "Bar", "Servito", t0.plusMinutes(5), 1);
        insertComandaWithTimestamp(2, "1x Other Table Paid", 3.0, "Bar", "Pagato", t0.plusMinutes(10), 1);

        assertTrue(dao.hasPaidComandaAfter(1, t0.minusSeconds(1)), "Dovrebbe trovare la comanda pagata del tavolo 1");
        assertTrue(dao.hasPaidComandaAfter(1, t0), "La condizione e' >=, quindi dovrebbe includere t0");
        assertFalse(dao.hasPaidComandaAfter(1, t0.plusSeconds(1)), "Non dovrebbe includere la comanda pagata precedente");

        assertFalse(dao.hasPaidComandaAfter(1, t0.plusHours(1)), "Nessuna comanda pagata dopo la soglia");
        assertTrue(dao.hasPaidComandaAfter(2, t0.plusMinutes(9)), "Dovrebbe trovare la comanda pagata del tavolo 2");
    }
}


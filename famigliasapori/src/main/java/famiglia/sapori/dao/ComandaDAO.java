package famiglia.sapori.dao;
 
import famiglia.sapori.database.DatabaseConnection;
import famiglia.sapori.model.Comanda;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
 
public class ComandaDAO {
    
    // Inserisce una nuova comanda nel database
    public void insertComanda(Comanda comanda) throws SQLException {
        String query = "INSERT INTO Comande (id_tavolo, prodotti, totale, tipo, stato, note, id_cameriere) VALUES (?, ?, ?, ?, ?, ?, ?)";
       
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
           
            stmt.setInt(1, comanda.getIdTavolo());
            stmt.setString(2, comanda.getProdotti());
            stmt.setDouble(3, comanda.getTotale());
            stmt.setString(4, comanda.getTipo());
            stmt.setString(5, comanda.getStato());
            stmt.setString(6, comanda.getNote());
            stmt.setInt(7, comanda.getIdCameriere());
           
            stmt.executeUpdate();
        }
    }

    // Recupera tutte le comande con uno specifico stato, ordinate per data e ora
    public List<Comanda> getComandeByStato(String stato) throws SQLException {
        List<Comanda> comande = new ArrayList<>();
        String query = "SELECT * FROM Comande WHERE stato = ? ORDER BY data_ora ASC";
       
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
           
            stmt.setString(1, stato);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    comande.add(new Comanda(
                        rs.getInt("id"),
                        rs.getInt("id_tavolo"),
                        rs.getString("prodotti"),
                        rs.getDouble("totale"),
                        rs.getString("tipo"),
                        rs.getString("stato"),
                        rs.getTimestamp("data_ora").toLocalDateTime(),
                        rs.getString("note"),
                        rs.getInt("id_cameriere")
                    ));
                }
            }
        }
        return comande;
    }

    // Recupera tutte le comande con uno specifico stato e tipo, ordinate per data e ora
    public List<Comanda> getComandeByStatoAndTipo(String stato, String tipo) throws SQLException {
        List<Comanda> comande = new ArrayList<>();
        String query = "SELECT * FROM Comande WHERE stato = ? AND tipo = ? ORDER BY data_ora ASC";
       
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
           
            stmt.setString(1, stato);
            stmt.setString(2, tipo);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    comande.add(new Comanda(
                        rs.getInt("id"),
                        rs.getInt("id_tavolo"),
                        rs.getString("prodotti"),
                        rs.getDouble("totale"),
                        rs.getString("tipo"),
                        rs.getString("stato"),
                        rs.getTimestamp("data_ora").toLocalDateTime(),
                        rs.getString("note"),
                        rs.getInt("id_cameriere")
                    ));
                }
            }
        }
        return comande;
    }

    // Aggiorna lo stato di una comanda specifica
    public void updateStatoComanda(int id, String stato) throws SQLException {
        String query = "UPDATE Comande SET stato = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
           
            stmt.setString(1, stato);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }

    // Recupera le comande di un tavolo che non sono ancora state pagate
    public List<Comanda> getComandeDaPagare(int idTavolo) throws SQLException {
        List<Comanda> comande = new ArrayList<>();
        // Seleziona tutto tranne quelle già 'Pagato'
        String query = "SELECT * FROM Comande WHERE id_tavolo = ? AND stato != 'Pagato'";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idTavolo);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    comande.add(new Comanda(
                            rs.getInt("id"),
                            rs.getInt("id_tavolo"),
                            rs.getString("prodotti"),
                            rs.getDouble("totale"),
                            rs.getString("tipo"),
                            rs.getString("stato"),
                            rs.getTimestamp("data_ora").toLocalDateTime(),
                            rs.getString("note"),
                            rs.getInt("id_cameriere")
                    ));
                }
            }
        }
        return comande;
    }

    // Imposta tutte le comande di un tavolo come "Pagato"
    public void setComandePagate(int idTavolo) throws SQLException {
        String query = "UPDATE Comande SET stato = 'Pagato' WHERE id_tavolo = ? AND stato != 'Pagato'";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idTavolo);
            stmt.executeUpdate();
        }
    }
}
package famiglia.sapori.dao;
 
import famiglia.sapori.database.DatabaseConnection;
import famiglia.sapori.model.Comanda;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
 
public class ComandaDAO {
 
    public void insertComanda(Comanda comanda) throws SQLException {
        String query = "INSERT INTO Comande (id_tavolo, prodotti, tipo, stato, note, id_cameriere) VALUES (?, ?, ?, ?, ?, ?)";
       
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
           
            stmt.setInt(1, comanda.getIdTavolo());
            stmt.setString(2, comanda.getProdotti());
            stmt.setString(3, comanda.getTipo());
            stmt.setString(4, comanda.getStato());
            stmt.setString(5, comanda.getNote());
            stmt.setInt(6, comanda.getIdCameriere());
           
            stmt.executeUpdate();
        }
    }
 
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
 
    public void updateStatoComanda(int id, String stato) throws SQLException {
        String query = "UPDATE Comande SET stato = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
           
            stmt.setString(1, stato);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }
}
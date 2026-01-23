package famiglia.sapori.dao;

import famiglia.sapori.database.DatabaseConnection;
import famiglia.sapori.model.ProdottoMagazzino;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class RicettaDAO {

    public Map<ProdottoMagazzino, Double> getIngredienti(int idPiatto) throws SQLException {
        Map<ProdottoMagazzino, Double> ingredienti = new HashMap<>();
        String query = "SELECT m.*, r.quantita as qta_ricetta FROM Ricetta r " +
                       "JOIN Magazzino m ON r.id_prodotto = m.id " +
                       "WHERE r.id_piatto = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idPiatto);
            try (ResultSet rs = stmt.executeQuery()) {
                while(rs.next()) {
                    ProdottoMagazzino p = new ProdottoMagazzino(
                        rs.getInt("id"),
                        rs.getString("prodotto"),
                        rs.getDouble("quantita"),
                        rs.getString("unita_misura"),
                        rs.getDouble("soglia_minima")
                    );
                    double qtaRicetta = rs.getDouble("qta_ricetta");
                    ingredienti.put(p, qtaRicetta);
                }
            }
        }
        return ingredienti;
    }

    public void addIngrediente(int idPiatto, int idProdotto, double quantita) throws SQLException {
        String query = "INSERT INTO Ricetta (id_piatto, id_prodotto, quantita) VALUES (?, ?, ?) " +
                       "ON DUPLICATE KEY UPDATE quantita = VALUES(quantita)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idPiatto);
            stmt.setInt(2, idProdotto);
            stmt.setDouble(3, quantita);
            stmt.executeUpdate();
        }
    }

    public void removeIngrediente(int idPiatto, int idProdotto) throws SQLException {
        String query = "DELETE FROM Ricetta WHERE id_piatto = ? AND id_prodotto = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idPiatto);
            stmt.setInt(2, idProdotto);
            stmt.executeUpdate();
        }
    }
}

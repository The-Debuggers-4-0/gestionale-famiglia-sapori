package famiglia.sapori.dao;

import famiglia.sapori.database.DatabaseConnection;
import famiglia.sapori.model.Piatto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuDAO {
    
    // Recupera tutti i piatti disponibili nel menu
    public List<Piatto> getAllPiatti() throws SQLException {
        List<Piatto> piatti = new ArrayList<>();
        String query = "SELECT * FROM Menu WHERE disponibile = 1";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                piatti.add(new Piatto(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("descrizione"),
                        rs.getDouble("prezzo"),
                        rs.getString("categoria"),
                        rs.getBoolean("disponibile"),
                        rs.getString("allergeni")
                ));
            }
        }
        return piatti;
    }

    // Recupera tutte le categorie di piatti presenti nel menu
    public List<String> getAllCategorie() throws SQLException {
        List<String> categorie = new ArrayList<>();
        String query = "SELECT DISTINCT categoria FROM Menu ORDER BY categoria";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                categorie.add(rs.getString("categoria"));
            }
        }
        return categorie;
    }

    // Recupera tutti i piatti, anche quelli non disponibili
    public List<Piatto> getAllPiattiComplete() throws SQLException {
        List<Piatto> piatti = new ArrayList<>();
        String query = "SELECT * FROM Menu";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                piatti.add(new Piatto(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("descrizione"),
                        rs.getDouble("prezzo"),
                        rs.getString("categoria"),
                        rs.getBoolean("disponibile"),
                        rs.getString("allergeni")
                ));
            }
        }
        return piatti;
    }

    // Aggiorna la disponibilità di un piatto
    public void updateDisponibilita(int id, boolean disponibile) throws SQLException {
        String query = "UPDATE Menu SET disponibile = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, disponibile ? 1 : 0);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        }
    }
}
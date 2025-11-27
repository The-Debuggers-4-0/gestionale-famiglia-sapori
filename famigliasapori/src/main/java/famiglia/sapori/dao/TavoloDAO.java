package famiglia.sapori.dao;

import famiglia.sapori.database.DatabaseConnection;
import famiglia.sapori.model.Tavolo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TavoloDAO {

    // Recupera tutti i tavoli dal database
    public List<Tavolo> getAllTavoli() throws SQLException {
        List<Tavolo> tavoli = new ArrayList<>();
        String query = "SELECT * FROM Tavoli";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                tavoli.add(new Tavolo(
                        rs.getInt("id"),
                        rs.getInt("numero"),
                        rs.getString("stato"),
                        rs.getInt("posti"),
                        rs.getString("note")
                ));
            }
        }
        return tavoli;
    }

    // Aggiorna lo stato di un tavolo specifico
    public void updateStatoTavolo(int id, String stato) throws SQLException {
        String query = "UPDATE Tavoli SET stato = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, stato);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }
}
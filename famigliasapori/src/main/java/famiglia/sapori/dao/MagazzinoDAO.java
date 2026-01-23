package famiglia.sapori.dao;

import famiglia.sapori.database.DatabaseConnection;
import famiglia.sapori.model.ProdottoMagazzino;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MagazzinoDAO {

    public List<ProdottoMagazzino> getAllProdotti() throws SQLException {
        List<ProdottoMagazzino> prodotti = new ArrayList<>();
        String query = "SELECT * FROM Magazzino";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                prodotti.add(new ProdottoMagazzino(
                        rs.getInt("id"),
                        rs.getString("prodotto"),
                        rs.getDouble("quantita"),
                        rs.getString("unita_misura"),
                        rs.getDouble("soglia_minima")
                ));
            }
        }
        return prodotti;
    }

    public void insertProdotto(ProdottoMagazzino p) throws SQLException {
        String query = "INSERT INTO Magazzino (prodotto, quantita, unita_misura, soglia_minima) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, p.getProdotto());
            pstmt.setDouble(2, p.getQuantita());
            pstmt.setString(3, p.getUnitaMisura());
            pstmt.setDouble(4, p.getSogliaMinima());
            pstmt.executeUpdate();
        }
    }

    public void updateProdotto(ProdottoMagazzino p) throws SQLException {
        String query = "UPDATE Magazzino SET prodotto = ?, quantita = ?, unita_misura = ?, soglia_minima = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, p.getProdotto());
            pstmt.setDouble(2, p.getQuantita());
            pstmt.setString(3, p.getUnitaMisura());
            pstmt.setDouble(4, p.getSogliaMinima());
            pstmt.setInt(5, p.getId());
            pstmt.executeUpdate();
        }
    }

    public void deleteProdotto(int id) throws SQLException {
        String query = "DELETE FROM Magazzino WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    public void scaricaQuantita(int idProdotto, double quantitaDaScaricare) throws SQLException {
        String query = "UPDATE Magazzino SET quantita = quantita - ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setDouble(1, quantitaDaScaricare);
            pstmt.setInt(2, idProdotto);
            pstmt.executeUpdate();
        }
    }

    // Metodo ottimizzato per scaricare più prodotti una sola volta
    public void scaricaQuantitaBatch(Map<Integer, Double> scarichi) throws SQLException {
        String query = "UPDATE Magazzino SET quantita = quantita - ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            // Disabilita auto-commit per gestire tutto in transazione (più veloce)
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            
            try {
                for (Map.Entry<Integer, Double> entry : scarichi.entrySet()) {
                    pstmt.setDouble(1, entry.getValue());
                    pstmt.setInt(2, entry.getKey());
                    pstmt.addBatch();
                }

                pstmt.executeBatch();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(originalAutoCommit);
            }
        }
    }
}

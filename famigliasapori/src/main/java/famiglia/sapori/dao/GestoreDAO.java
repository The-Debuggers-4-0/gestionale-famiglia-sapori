package famiglia.sapori.dao;
 
import famiglia.sapori.database.DatabaseConnection;
import famiglia.sapori.model.Piatto;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
 
public class GestoreDAO {
 
    public Map<String, Integer> getBestSellers() throws SQLException {
        Map<String, Integer> stats = new HashMap<>();
        String query = "SELECT prodotti FROM Comande WHERE stato = 'Pagato'";
 
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
 
            while (rs.next()) {
                String prodottiStr = rs.getString("prodotti");
                if (prodottiStr != null && !prodottiStr.isEmpty()) {
                    // Assumiamo che i prodotti siano separati da virgola o newline
                    // Adatta il parsing in base al formato reale salvato nel DB
                    String[] prodotti = prodottiStr.split("[,\\n]");
                    for (String p : prodotti) {
                        String nomePiatto = p.trim();
                        // Rimuovi eventuali quantità iniziali se presenti (es. "2x Carbonara")
                        if (nomePiatto.matches("\\d+x .*")) {
                            String[] parts = nomePiatto.split("x ", 2);
                            int qty = Integer.parseInt(parts[0]);
                            String nome = parts[1];
                            stats.put(nome, stats.getOrDefault(nome, 0) + qty);
                        } else {
                            stats.put(nomePiatto, stats.getOrDefault(nomePiatto, 0) + 1);
                        }
                    }
                }
            }
        }
        return stats;
    }
 
    public double calculateTotalIncome() throws SQLException {
        double total = 0.0;
        Map<String, Double> prezzi = getPrezziMenu();
        String query = "SELECT prodotti FROM Comande WHERE stato = 'Pagato'";
 
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
 
            while (rs.next()) {
                String prodottiStr = rs.getString("prodotti");
                if (prodottiStr != null && !prodottiStr.isEmpty()) {
                    String[] prodotti = prodottiStr.split("[,\\n]");
                    for (String p : prodotti) {
                        String nomePiatto = p.trim();
                        int qty = 1;
                        if (nomePiatto.matches("\\d+x .*")) {
                            String[] parts = nomePiatto.split("x ", 2);
                            qty = Integer.parseInt(parts[0]);
                            nomePiatto = parts[1];
                        }
                       
                        // Cerca il prezzo (anche parziale se il nome nel DB è leggermente diverso)
                        // Qui assumiamo corrispondenza esatta per semplicità
                        if (prezzi.containsKey(nomePiatto)) {
                            total += prezzi.get(nomePiatto) * qty;
                        }
                    }
                }
            }
        }
        return total;
    }
 
    private Map<String, Double> getPrezziMenu() throws SQLException {
        Map<String, Double> prezzi = new HashMap<>();
        String query = "SELECT nome, prezzo FROM Menu";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                prezzi.put(rs.getString("nome"), rs.getDouble("prezzo"));
            }
        }
        return prezzi;
    }
}

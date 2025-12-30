package famiglia.sapori.dao;

import famiglia.sapori.database.DatabaseConnection;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class GestoreDAO {

    public Map<String, Integer> getBestSellers() throws SQLException {
        Map<String, Integer> stats = new HashMap<>();
        // Piatti più venduti dell'ultima settimana (7 giorni)
        String query = "SELECT prodotti FROM Comande WHERE stato = 'Pagato' AND tipo = 'Cucina' AND data_ora >= ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.of(LocalDate.now().minusDays(7), LocalTime.MIN)));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String prodottiStr = rs.getString("prodotti");
                    if (prodottiStr != null && !prodottiStr.isEmpty()) {
                        // Assumiamo che i prodotti siano separati da virgola o newline
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
        }
        return stats;
    }

    // Calcola incasso settimanale (ultimi 7 giorni) solo piatti
    public double calculateWeeklyIncome() throws SQLException {
        double total = 0.0;
        String query = "SELECT SUM(totale) as incasso FROM Comande WHERE stato = 'Pagato' AND tipo = 'Cucina' AND data_ora >= ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.of(LocalDate.now().minusDays(7), LocalTime.MIN)));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    total = rs.getDouble("incasso");
                }
            }
        }
        return total;
    }

    // Calcola incasso giornaliero (solo oggi) solo piatti
    public double calculateDailyIncome() throws SQLException {
        double total = 0.0;
        String query = "SELECT SUM(totale) as incasso FROM Comande WHERE stato = 'Pagato' AND tipo = 'Cucina' AND data_ora >= ? AND data_ora < ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            LocalDate today = LocalDate.now();
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.of(today, LocalTime.MIN)));
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.of(today.plusDays(1), LocalTime.MIN)));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    total = rs.getDouble("incasso");
                }
            }
        }
        return total;
    }
}

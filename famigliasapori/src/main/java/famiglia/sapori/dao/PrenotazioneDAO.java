package famiglia.sapori.dao;
 
import famiglia.sapori.database.DatabaseConnection;
import famiglia.sapori.model.Prenotazione;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
 
public class PrenotazioneDAO {
 
    public List<Prenotazione> getAllPrenotazioni() throws SQLException {
        List<Prenotazione> lista = new ArrayList<>();
        // Ordina per data/ora imminente
        String query = "SELECT * FROM Prenotazioni WHERE data_ora >= CURDATE() ORDER BY data_ora ASC";
 
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int idTavolo = rs.getInt("id_tavolo");
                Integer idTavoloObj = rs.wasNull() ? null : idTavolo;
               
                lista.add(new Prenotazione(
                    rs.getInt("id"),
                    rs.getString("nome_cliente"),
                    rs.getString("telefono"),
                    rs.getInt("numero_persone"),
                    rs.getTimestamp("data_ora").toLocalDateTime(),
                    rs.getString("note"),
                    idTavoloObj
                ));
            }
        }
        return lista;
    }
 
    public void insertPrenotazione(Prenotazione p) throws SQLException {
        String query = "INSERT INTO Prenotazioni (nome_cliente, telefono, numero_persone, data_ora, note, id_tavolo) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, p.getNomeCliente());
            stmt.setString(2, p.getTelefono());
            stmt.setInt(3, p.getNumeroPersone());
            stmt.setTimestamp(4, Timestamp.valueOf(p.getDataOra()));
            stmt.setString(5, p.getNote());
            if (p.getIdTavolo() != null) {
                stmt.setInt(6, p.getIdTavolo());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            stmt.executeUpdate();
        }
    }
 
    public void deletePrenotazione(int id) throws SQLException {
        String query = "DELETE FROM Prenotazioni WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
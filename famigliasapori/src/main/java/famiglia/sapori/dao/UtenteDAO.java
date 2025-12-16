package famiglia.sapori.dao;
 
import famiglia.sapori.database.DatabaseConnection;
import famiglia.sapori.model.Utente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
 
public class UtenteDAO {
    
    // Effettua il login di un utente verificando username e password
    public Utente login(String username, String password) throws SQLException {
        String query = "SELECT * FROM Utenti WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
           
            stmt.setString(1, username);
            stmt.setString(2, password);
           
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Utente(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("ruolo")
                    );
                }
            }
        }
        return null;
    }

    public java.util.List<Utente> getAllUtenti() throws SQLException {
        java.util.List<Utente> utenti = new java.util.ArrayList<>();
        String query = "SELECT * FROM Utenti";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             java.sql.Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                utenti.add(new Utente(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("ruolo")
                ));
            }
        }
        return utenti;
    }

    public void insertUtente(Utente utente) throws SQLException {
        String query = "INSERT INTO Utenti (nome, username, password, ruolo) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, utente.getNome());
            stmt.setString(2, utente.getUsername());
            stmt.setString(3, utente.getPassword());
            stmt.setString(4, utente.getRuolo());
            stmt.executeUpdate();
        }
    }

    public void updateUtente(Utente utente) throws SQLException {
        String query = "UPDATE Utenti SET nome = ?, username = ?, password = ?, ruolo = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, utente.getNome());
            stmt.setString(2, utente.getUsername());
            stmt.setString(3, utente.getPassword());
            stmt.setString(4, utente.getRuolo());
            stmt.setInt(5, utente.getId());
            stmt.executeUpdate();
        }
    }

    public void deleteUtente(int id) throws SQLException {
        String query = "DELETE FROM Utenti WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
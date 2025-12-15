package famiglia.sapori.testutil;

import famiglia.sapori.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class TestDatabase {
    private TestDatabase() {}
    
    private static final Object LOCK = new Object();
    private static boolean schemaCreated = false;

    public static void setupSchema() throws SQLException {
        synchronized (LOCK) {
            try (Connection conn = DatabaseConnection.getInstance().getConnection();
                 Statement st = conn.createStatement()) {
                // Drop existing tables to ensure a clean slate
                st.execute("DROP TABLE IF EXISTS Menu");
                st.execute("DROP TABLE IF EXISTS Utenti");
                st.execute("DROP TABLE IF EXISTS Tavoli");
                st.execute("DROP TABLE IF EXISTS Prenotazioni");
                st.execute("DROP TABLE IF EXISTS Comande");

            // Create tables
            st.execute("CREATE TABLE Menu (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "nome VARCHAR(255), " +
                    "descrizione VARCHAR(255), " +
                    "prezzo DOUBLE, " +
                    "categoria VARCHAR(100), " +
                    "disponibile INT, " +
                    "allergeni VARCHAR(255))");

            st.execute("CREATE TABLE Utenti (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "nome VARCHAR(255), " +
                    "username VARCHAR(255), " +
                    "password VARCHAR(255), " +
                    "ruolo VARCHAR(100))");

            st.execute("CREATE TABLE Tavoli (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "numero INT, " +
                    "stato VARCHAR(50), " +
                    "posti INT, " +
                    "note VARCHAR(255))");

            st.execute("CREATE TABLE Prenotazioni (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "nome_cliente VARCHAR(255), " +
                    "telefono VARCHAR(50), " +
                    "numero_persone INT, " +
                    "data_ora TIMESTAMP, " +
                    "note VARCHAR(255), " +
                    "id_tavolo INT)");

            st.execute("CREATE TABLE Comande (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "id_tavolo INT, " +
                    "prodotti VARCHAR(1000), " +
                    "tipo VARCHAR(50), " +
                    "stato VARCHAR(50), " +
                    "data_ora TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "note VARCHAR(255), " +
                    "id_cameriere INT)");
            }
        }
    }

    public static void seedData() throws SQLException {
        synchronized (LOCK) {
            Connection conn = null;
            Statement st = null;
            try {
                conn = DatabaseConnection.getInstance().getConnection();
                st = conn.createStatement();
                
                // Seed Menu
                st.execute("INSERT INTO Menu (nome, descrizione, prezzo, categoria, disponibile, allergeni) VALUES " +
                        "('Acqua', 'Naturale', 1.50, 'Bevande', 1, '')," +
                        "('Pizza Margherita', 'Pomodoro e mozzarella', 6.00, 'Pizze', 1, 'lattosio, glutine')," +
                        "('Risotto', 'Zafferano', 8.50, 'Primi', 0, 'glutine')");

                // Seed Utenti
                st.execute("INSERT INTO Utenti (nome, username, password, ruolo) VALUES " +
                        "('Mario Rossi', 'mario', 'pwd123', 'Cameriere')," +
                        "('Admin', 'admin', 'admin', 'Admin')");

                // Seed Tavoli
                st.execute("INSERT INTO Tavoli (numero, stato, posti, note) VALUES " +
                        "(1, 'Libero', 4, '')," +
                        "(2, 'Occupato', 2, 'Finestra')");

                // Seed Prenotazioni: one in future, one in past
                st.execute("INSERT INTO Prenotazioni (nome_cliente, telefono, numero_persone, data_ora, note, id_tavolo) VALUES " +
                        "('Luca', '123', 2, DATEADD('DAY', 1, CURRENT_TIMESTAMP()), 'Compleanno', NULL)," +
                        "('Giulia', '456', 4, DATEADD('DAY', -1, CURRENT_TIMESTAMP()), 'Anniversario', 1)");

                // Seed Comande
                st.execute("INSERT INTO Comande (id_tavolo, prodotti, tipo, stato, note, id_cameriere) VALUES " +
                        "(1, 'Acqua, Pizza', 'Cucina', 'In Preparazione', '', 1)," +
                        "(2, 'Caffe', 'Bar', 'Servita', '', 1)");
            } finally {
                if (st != null) {
                    try { st.close(); } catch (SQLException e) { /* ignore */ }
                }
                // Do NOT close connection - it's managed by ConnectionPool
            }
        }
    }
    
    /**
     * Clears all data from tables without dropping them.
     * More efficient than recreating schema for each test.
     */
    public static void clearData() throws SQLException {
        synchronized (LOCK) {
            Connection conn = null;
            Statement st = null;
            try {
                conn = DatabaseConnection.getInstance().getConnection();
                st = conn.createStatement();
                
                // Delete in reverse order of foreign key dependencies
                st.execute("DELETE FROM Comande");
                st.execute("DELETE FROM Prenotazioni");
                st.execute("DELETE FROM Tavoli");
                st.execute("DELETE FROM Utenti");
                st.execute("DELETE FROM Menu");
            } finally {
                if (st != null) {
                    try { st.close(); } catch (SQLException e) { /* ignore */ }
                }
                // Do NOT close connection - it's managed by ConnectionPool
            }
        }
    }
}

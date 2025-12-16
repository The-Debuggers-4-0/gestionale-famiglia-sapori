package famiglia.sapori.database;

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
                // Order is important: drop tables with foreign keys first
                st.execute("DROP TABLE IF EXISTS Comande");
                st.execute("DROP TABLE IF EXISTS Prenotazioni");
                st.execute("DROP TABLE IF EXISTS Menu");
                st.execute("DROP TABLE IF EXISTS Tavoli");
                st.execute("DROP TABLE IF EXISTS Utenti");

            // Create tables
            st.execute("CREATE TABLE Menu (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "nome VARCHAR(100), " +
                    "descrizione TEXT, " +
                    "prezzo DECIMAL(10,2), " +
                    "categoria VARCHAR(50), " +
                    "disponibile TINYINT(1) DEFAULT 1, " +
                    "allergeni TEXT)");

            st.execute("CREATE TABLE Utenti (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "nome VARCHAR(100), " +
                    "username VARCHAR(50) UNIQUE, " +
                    "password VARCHAR(100), " +
                    "ruolo VARCHAR(50))");

            st.execute("CREATE TABLE Tavoli (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "numero INT UNIQUE, " +
                    "stato VARCHAR(50) DEFAULT 'Libero', " +
                    "posti INT DEFAULT 4, " +
                    "note TEXT)");

            st.execute("CREATE TABLE Prenotazioni (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "nome_cliente VARCHAR(100), " +
                    "telefono VARCHAR(20), " +
                    "numero_persone INT, " +
                    "data_ora DATETIME, " +
                    "note TEXT, " +
                    "id_tavolo INT, " +
                    "FOREIGN KEY (id_tavolo) REFERENCES Tavoli(id) ON DELETE SET NULL ON UPDATE CASCADE)");

            st.execute("CREATE TABLE Comande (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "id_tavolo INT, " +
                    "prodotti TEXT, " +
                    "totale DECIMAL(10,2) DEFAULT 0.00, " +
                    "tipo VARCHAR(50), " +
                    "stato VARCHAR(50) DEFAULT 'In Attesa', " +
                    "data_ora DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "note TEXT, " +
                    "id_cameriere INT, " +
                    "FOREIGN KEY (id_tavolo) REFERENCES Tavoli(id) ON DELETE NO ACTION ON UPDATE NO ACTION, " +
                    "FOREIGN KEY (id_cameriere) REFERENCES Utenti(id) ON DELETE NO ACTION ON UPDATE NO ACTION)");
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
                
                // Temporarily disable foreign key constraints for seeding
                st.execute("SET REFERENTIAL_INTEGRITY FALSE");
                
                // Seed Menu with explicit IDs
                st.execute("INSERT INTO Menu (id, nome, descrizione, prezzo, categoria, disponibile, allergeni) VALUES " +
                        "(1, 'Acqua', 'Naturale', 1.50, 'Bevande', 1, '')," +
                        "(2, 'Pizza Margherita', 'Pomodoro e mozzarella', 6.00, 'Primi', 1, 'lattosio, glutine')," +
                        "(3, 'Carbonara', 'Guanciale, uova, pecorino, pepe', 12.00, 'Primi', 1, 'Uova, Glutine, Lattosio')," +
                        "(4, 'Risotto', 'Risotto allo zafferano', 8.50, 'Primi', 0, 'glutine')," +
                        "(5, 'Caffe', 'Espresso', 1.00, 'Bevande', 1, '')");

                // Seed Utenti with explicit IDs
                st.execute("INSERT INTO Utenti (id, nome, username, password, ruolo) VALUES " +
                        "(1, 'Mario Rossi', 'mario', 'pwd123', 'Cameriere')," +
                        "(2, 'Admin User', 'admin', 'admin', 'Gestore')");

                // Seed Tavoli with explicit IDs
                st.execute("INSERT INTO Tavoli (id, numero, stato, posti, note) VALUES " +
                        "(1, 1, 'Libero', 4, '')," +
                        "(2, 2, 'Occupato', 2, 'Finestra')," +
                        "(3, 3, 'Libero', 6, '')");

                // Seed Prenotazioni: one in future, one in past
                st.execute("INSERT INTO Prenotazioni (nome_cliente, telefono, numero_persone, data_ora, note, id_tavolo) VALUES " +
                        "('Luca Bianchi', '123456789', 2, DATEADD('DAY', 1, CURRENT_TIMESTAMP()), 'Compleanno', NULL)," +
                        "('Giulia Verdi', '987654321', 4, DATEADD('DAY', -1, CURRENT_TIMESTAMP()), 'Anniversario', 1)");

                // Seed Comande
                st.execute("INSERT INTO Comande (id_tavolo, prodotti, totale, tipo, stato, note, id_cameriere) VALUES " +
                        "(1, '1x Acqua Naturale, 1x Pizza Margherita', 7.50, 'Cucina', 'In Preparazione', '', 1)," +
                        "(2, '1x Caffe', 1.00, 'Bar', 'Servito', '', 1)");
                
                // Re-enable foreign key constraints
                st.execute("SET REFERENTIAL_INTEGRITY TRUE");
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

package famiglia.sapori.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import famiglia.sapori.util.PasswordUtil;

// Classe di utilità per la gestione del database di test, con setup, pulizia e popolamento
public final class TestDatabase {
    private TestDatabase() {}
    
    // Lock per la sincronizzazione dei metodi statici
    private static final Object LOCK = new Object();
    private static boolean schemaCreated = false;

    // Crea lo schema del database per i test
    public static void setupSchema() throws SQLException {
        synchronized (LOCK) {
            // Ottieni la connessione al database
            Connection conn = DatabaseConnection.getInstance().getConnection();

            // Se lo schema è già stato creato, verifica che esista ancora e ritorna.
            if (schemaCreated) {
                try (Statement verify = conn.createStatement();
                     ResultSet rs = verify.executeQuery(
                             "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'TAVOLI'")) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        return;
                    }
                } catch (SQLException ignored) {
                    // Continua e ricrea lo schema.
                }
            }

            try (Statement st = conn.createStatement()) {
                // Elimina le tabelle esistenti per garantire un ambiente pulito
                // L'ordine è importante: elimina prima le tabelle con chiavi esterne
                st.execute("DROP TABLE IF EXISTS Comande");
                st.execute("DROP TABLE IF EXISTS Prenotazioni");
                st.execute("DROP TABLE IF EXISTS Ricetta");
                st.execute("DROP TABLE IF EXISTS Menu");
                st.execute("DROP TABLE IF EXISTS Magazzino");
                st.execute("DROP TABLE IF EXISTS Tavoli");
                st.execute("DROP TABLE IF EXISTS Utenti");

            // Crea le tabelle necessarie per i test
            st.execute("CREATE TABLE Magazzino (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "prodotto VARCHAR(255) NOT NULL, " +
                    "quantita DOUBLE NOT NULL DEFAULT 0, " +
                    "unita_misura VARCHAR(50) DEFAULT 'kg', " +
                    "soglia_minima DOUBLE DEFAULT 0)");

            st.execute("CREATE TABLE Menu (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "nome VARCHAR(100), " +
                    "descrizione TEXT, " +
                    "prezzo DECIMAL(10,2), " +
                    "categoria VARCHAR(50), " +
                    "disponibile TINYINT(1) DEFAULT 1, " +
                    "allergeni TEXT)");

            st.execute("CREATE TABLE Ricetta (" +
                    "id_piatto INT NOT NULL, " +
                    "id_prodotto INT NOT NULL, " +
                    "quantita DOUBLE NOT NULL, " +
                    "PRIMARY KEY (id_piatto, id_prodotto), " +
                    "FOREIGN KEY (id_piatto) REFERENCES Menu(id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (id_prodotto) REFERENCES Magazzino(id) ON DELETE CASCADE)");

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

            schemaCreated = true;
        }
    }

    // Inserisce dati di test nel database
    public static void seedData() throws SQLException {
        synchronized (LOCK) {
            Connection conn = null;
            Statement st = null;
            try {
                conn = DatabaseConnection.getInstance().getConnection();
                st = conn.createStatement();

            // Disabilita temporaneamente i vincoli di chiave esterna per la pulizia e l'inserimento dei dati.
            // Questo metodo viene chiamato da più test; mantienilo idempotente.
            st.execute("SET REFERENTIAL_INTEGRITY FALSE");
            try {
                // Pulisci le righe esistenti per evitare duplicati di chiavi primarie negli inserimenti con ID fissi
                // (l'ordine è importante a causa delle chiavi esterne).
                st.execute("DELETE FROM Comande");
                st.execute("DELETE FROM Prenotazioni");
                st.execute("DELETE FROM Tavoli");
                st.execute("DELETE FROM Utenti");
                st.execute("DELETE FROM Ricetta");
                st.execute("DELETE FROM Menu");
                st.execute("DELETE FROM Magazzino");

                // Seed Magazzino
                st.execute("INSERT INTO Magazzino (id, prodotto, quantita, unita_misura, soglia_minima) VALUES " +
                    "(1, 'Farina 00', 50, 'kg', 10)," +
                    "(2, 'Uova', 200, 'pz', 50)," +
                    "(3, 'Pasta di Semola', 30, 'kg', 5)," +
                    "(4, 'Pomodori', 20, 'kg', 5)," +
                    "(5, 'Mozzarella', 10, 'kg', 2)," +
                    "(6, 'Zafferano', 0.1, 'kg', 0.01)");

                // Seed Menu con ID espliciti
                st.execute("INSERT INTO Menu (id, nome, descrizione, prezzo, categoria, disponibile, allergeni) VALUES " +
                    "(1, 'Acqua', 'Naturale', 1.50, 'Bevande', 1, '')," +
                    "(2, 'Pizza Margherita', 'Pomodoro e mozzarella', 6.00, 'Primi', 1, 'lattosio, glutine')," +
                    "(3, 'Carbonara', 'Guanciale, uova, pecorino, pepe', 12.00, 'Primi', 1, 'Uova, Glutine, Lattosio')," +
                    "(4, 'Risotto', 'Risotto allo zafferano', 8.50, 'Primi', 0, 'glutine')," +
                    "(5, 'Caffe', 'Espresso', 1.00, 'Bevande', 1, '')");

                // Seed Ricetta (collegamenti Menu -> Magazzino)
                // Margherita (2) usa Farina (1), Pomodori (4), Mozzarella (5)
                st.execute("INSERT INTO Ricetta (id_piatto, id_prodotto, quantita) VALUES " +
                    "(2, 1, 0.2), (2, 4, 0.1), (2, 5, 0.1)," +
                    "(4, 6, 0.001)"); // Risotto usa Zafferano


                // Seed Utenti con ID espliciti
                String pwd123Hash = PasswordUtil.hashPassword("pwd123");
                String adminHash = PasswordUtil.hashPassword("admin");
                
                st.execute("INSERT INTO Utenti (id, nome, username, password, ruolo) VALUES " +
                    "(1, 'Mario Rossi', 'mario', '" + pwd123Hash + "', 'Cameriere')," +
                    "(2, 'Admin User', 'admin', '" + adminHash + "', 'Gestore')");

                // Seed Tavoli con ID espliciti
                st.execute("INSERT INTO Tavoli (id, numero, stato, posti, note) VALUES " +
                    "(1, 1, 'Libero', 4, '')," +
                    "(2, 2, 'Occupato', 2, 'Finestra')," +
                    "(3, 3, 'Libero', 6, '')");

                // Seed Prenotazioni: una nel futuro, una nel passato
                st.execute("INSERT INTO Prenotazioni (nome_cliente, telefono, numero_persone, data_ora, note, id_tavolo) VALUES " +
                    "('Luca Bianchi', '123456789', 2, DATEADD('DAY', 1, CURRENT_TIMESTAMP()), 'Compleanno', NULL)," +
                    "('Giulia Verdi', '987654321', 4, DATEADD('DAY', -1, CURRENT_TIMESTAMP()), 'Anniversario', 1)");

                // Seed Comande
                st.execute("INSERT INTO Comande (id_tavolo, prodotti, totale, tipo, stato, note, id_cameriere) VALUES " +
                    "(1, '1x Acqua Naturale, 1x Pizza Margherita', 7.50, 'Cucina', 'In Preparazione', '', 1)," +
                    "(2, '1x Caffe', 1.00, 'Bar', 'Servito', '', 1)");
            } finally {
                // Riabilita sempre i vincoli anche se gli inserimenti falliscono
                st.execute("SET REFERENTIAL_INTEGRITY TRUE");
            }
            } finally {
                if (st != null) {
                    try { st.close(); } catch (SQLException e) { /* ignore */ }
                }
                // Do NOT close connection - it's managed by ConnectionPool
            }
        }
    }
    
    /**
     * Pulisce tutti i dati dalle tabelle senza eliminarle.
     * Più efficiente che ricreare lo schema per ogni test.
     */
    public static void clearData() throws SQLException {
        synchronized (LOCK) {
            Connection conn = null;
            Statement st = null;
            try {
                conn = DatabaseConnection.getInstance().getConnection();
                st = conn.createStatement();
                
                // Disabilita temporaneamente i vincoli di chiave esterna per la pulizia
                st.execute("SET REFERENTIAL_INTEGRITY FALSE");
                
                // Elimina in ordine inverso rispetto alle dipendenze delle chiavi esterne
                st.execute("DELETE FROM Comande");
                st.execute("DELETE FROM Prenotazioni");
                st.execute("DELETE FROM Tavoli");
                st.execute("DELETE FROM Utenti");
                st.execute("DELETE FROM Menu");
                
                // Riabilita i vincoli di chiave esterna
                st.execute("SET REFERENTIAL_INTEGRITY TRUE");
            } finally {
                if (st != null) {
                    try { st.close(); } catch (SQLException e) { /* ignore */ }
                }
                // Do NOT close connection - it's managed by ConnectionPool
            }
        }
    }
}

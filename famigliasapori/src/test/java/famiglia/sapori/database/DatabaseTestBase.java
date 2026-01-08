package famiglia.sapori.database;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

// Classe base per i test che richiedono il database, con setup e reset
public abstract class DatabaseTestBase {
    
    // Setup iniziale del database prima di tutti i test
    @BeforeAll
    static void beforeAll() throws Exception {

        // Crea lo schema del database di test
        TestDatabase.setupSchema();

        // Popola il database con dati di test iniziali
        TestDatabase.seedData();
    }

    // Reset del database prima di ogni test
    @BeforeEach
    void beforeEach() throws Exception {
    
        // Pulisce i dati esistenti
        TestDatabase.clearData();

        // Popola nuovamente i dati di test
        TestDatabase.seedData();
    }
}

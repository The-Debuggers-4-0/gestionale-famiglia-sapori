package famiglia.sapori.database;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public abstract class DatabaseTestBase {
    // Setup iniziale del database prima di tutti i test
    @BeforeAll
    static void beforeAll() throws Exception {
        TestDatabase.setupSchema();
        TestDatabase.seedData();
    }

    // Reset del database prima di ogni test
    @BeforeEach
    void beforeEach() throws Exception {
        TestDatabase.clearData();
        TestDatabase.seedData();
    }
}

package famiglia.sapori.database;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public abstract class DatabaseTestBase {
    @BeforeAll
    static void beforeAll() throws Exception {
        TestDatabase.setupSchema();
        TestDatabase.seedData();
    }

    @BeforeEach
    void beforeEach() throws Exception {
        TestDatabase.clearData();
        TestDatabase.seedData();
    }
}

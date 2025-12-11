package famiglia.sapori.testutil;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public abstract class DatabaseTestBase {
    @BeforeAll
    static void beforeAll() throws Exception {
        // Setup schema once for all tests in the class
        TestDatabase.setupSchema();
        TestDatabase.seedData();
    }

    @BeforeEach
    void beforeEach() throws Exception {
        // Clear and re-seed data for isolation between tests
        // More efficient than recreating schema every time
        TestDatabase.clearData();
        TestDatabase.seedData();
    }
}

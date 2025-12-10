package famiglia.sapori.testutil;

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
        // Re-seed to ensure isolation between tests
        TestDatabase.setupSchema();
        TestDatabase.seedData();
    }
}

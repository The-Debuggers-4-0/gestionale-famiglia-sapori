package famiglia.sapori.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per LoginController
 * NOTA: I test dei controller JavaFX sono più complessi e richiedono inizializzazione dell'ambiente JavaFX
 */
public class LoginControllerTest {

    private LoginController loginController;

    @BeforeEach
    public void setUp() {
        loginController = new LoginController();
    }

    /**
     * Testa che il controller LoginController venga creato correttamente
     */
    @Test
    public void testLoginControllerCreation() {
        // Assert
        assertNotNull(loginController, "Il controller dovrebbe essere creato correttamente");
    }

    /**
     * Testa l'inizializzazione del controller (test base senza JavaFX)
     */
    @Test
    public void testInitialize() {
        // I test dei controller richiedono un setup più complesso con JavaFX
        // Per ora testiamo solo che il controller possa essere istanziato
        assertDoesNotThrow(() -> {
            loginController.initialize(null, null);
        });
    }

}

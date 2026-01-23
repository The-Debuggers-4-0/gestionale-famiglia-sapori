package famiglia.sapori.controller;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;

public class LoginControllerTest {

    /**
     * Verifica che l'inizializzazione del LoginController configuri l'UtenteDAO.
     * Anche se usiamo null per i parametri (poiché non utilizzati nel metodo initialize),
     * vogliamo assicurarci che il DAO venga istanziato correttamente.
     */
    @Test
    public void testInitializeSetsDAOWithoutUI() throws Exception {
        // Crea un'istanza del controller
        LoginController controller = new LoginController();

        // Chiama il metodo initialize con null, dato che non vengono utilizzati
        assertDoesNotThrow(() -> controller.initialize(null, null));

        // Usa reflection per accedere al campo privato utenteDAO
        Field f = LoginController.class.getDeclaredField("utenteDAO");

        // Rendi il campo accessibile e verifica che non sia null
        f.setAccessible(true);
        assertNotNull(f.get(controller), "Il campo utenteDAO dovrebbe essere inizializzato dopo initialize()");
    }
}

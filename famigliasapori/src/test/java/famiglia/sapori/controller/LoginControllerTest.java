package famiglia.sapori.controller;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;

public class LoginControllerTest {

    /**
     * Verifica che l'inizializzazione del LoginController configuri l'UtenteDAO
     * anche senza UI/FXML, controllando via reflection il campo privato.
     */
    @Test
    public void testInitializeSetsDAOWithoutUI() throws Exception {
        // Crea un'istanza del controller
        LoginController controller = new LoginController();

        // Chiama il metodo initialize
        assertDoesNotThrow(() -> controller.initialize(null, null));

        // Usa reflection per accedere al campo privato utenteDAO
        Field f = LoginController.class.getDeclaredField("utenteDAO");

        // Rendi il campo accessibile e verifica che non sia null
        f.setAccessible(true);
        assertNotNull(f.get(controller));
    }
}

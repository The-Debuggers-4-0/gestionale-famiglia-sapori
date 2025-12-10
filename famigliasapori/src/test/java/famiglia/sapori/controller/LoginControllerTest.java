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
        LoginController controller = new LoginController();
        assertDoesNotThrow(() -> controller.initialize(null, null));
        Field f = LoginController.class.getDeclaredField("utenteDAO");
        f.setAccessible(true);
        assertNotNull(f.get(controller));
    }
}

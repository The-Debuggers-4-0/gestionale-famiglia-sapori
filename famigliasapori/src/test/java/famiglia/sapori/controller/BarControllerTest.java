package famiglia.sapori.controller;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BarControllerTest {

    @Test
    /**
     * Verifica la logica del controller Bar (non-FX),
     * assicurando che i metodi inizializzino/gestiscano correttamente lo stato.
     */
    public void testIsDrinkCategories() throws Exception {
        // Crea un'istanza del controller Bar
        BarController controller = new BarController();

        // Ottiene il metodo isDrink tramite reflection
        var m = BarController.class.getDeclaredMethod("isDrink", String.class);

        // Rende il metodo accessibile e testa vari casi
        m.setAccessible(true);
        
        // Testa categorie di bevande e non bevande
        assertFalse((boolean) m.invoke(controller, (Object) null));
        assertTrue((boolean) m.invoke(controller, "Bevande"));
        assertTrue((boolean) m.invoke(controller, "VINI Bianchi"));
        assertTrue((boolean) m.invoke(controller, "birre artigianali"));
        assertTrue((boolean) m.invoke(controller, "caffè"));
        assertTrue((boolean) m.invoke(controller, "soft drink"));
        assertFalse((boolean) m.invoke(controller, "Primi"));
    }
}

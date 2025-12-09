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
        BarController controller = new BarController();
        var m = BarController.class.getDeclaredMethod("isDrink", String.class);
        m.setAccessible(true);
        assertFalse((boolean) m.invoke(controller, (Object) null));
        assertTrue((boolean) m.invoke(controller, "Bevande"));
        assertTrue((boolean) m.invoke(controller, "VINI Bianchi"));
        assertTrue((boolean) m.invoke(controller, "birre artigianali"));
        assertTrue((boolean) m.invoke(controller, "caffè"));
        assertTrue((boolean) m.invoke(controller, "soft drink"));
        assertFalse((boolean) m.invoke(controller, "Primi"));
    }
}

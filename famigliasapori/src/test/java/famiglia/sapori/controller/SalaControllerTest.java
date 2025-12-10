package famiglia.sapori.controller;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SalaControllerTest {

    /**
     * Verifica la logica di classificazione drink del SalaController (non-FX)
     * chiamando il metodo privato via reflection con varie categorie.
     */
    @Test
    public void testIsDrinkCategories() throws Exception {
        SalaController controller = new SalaController();
        var m = SalaController.class.getDeclaredMethod("isDrink", String.class);
        m.setAccessible(true);
        assertFalse((boolean) m.invoke(controller, (Object) null));
        assertTrue((boolean) m.invoke(controller, "Bevande"));
        assertTrue((boolean) m.invoke(controller, "VINI Rossi"));
        assertTrue((boolean) m.invoke(controller, "birre"));
        assertTrue((boolean) m.invoke(controller, "caffè"));
        assertTrue((boolean) m.invoke(controller, "drink"));
        assertFalse((boolean) m.invoke(controller, "Secondi"));
    }
}

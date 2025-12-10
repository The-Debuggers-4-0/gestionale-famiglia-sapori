package famiglia.sapori.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PiattoTest {

    /**
     * Verifica equals/hashCode basati esclusivamente sull'id del Piatto.
     */
    @Test
    void equalsHashCode_byIdOnly() {
        Piatto a = new Piatto(1, "A", "desc", 1.0, "cat", true, "");
        Piatto b = new Piatto(1, "B", "other", 9.9, "x", false, "all");
        Piatto c = new Piatto(2, "A", "desc", 1.0, "cat", true, "");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotEquals(a, null);
        assertNotEquals(a, "not a Piatto");
    }

    /**
     * Controlla che toString restituisca il nome del piatto.
     */
    @Test
    void toString_returnsNome() {
        Piatto p = new Piatto(1, "Margherita", "Pomodoro e mozzarella", 6.0, "Pizze", true, "");
        assertEquals("Margherita", p.toString());
    }
}

package famiglia.sapori.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class TavoloTest {

    /**
     * Verifica che solo stati validi ('Libero', 'Occupato') siano accettati.
     */
    @ParameterizedTest
    @ValueSource(strings = {"Libero", "Occupato"})
    void stato_validStates_accepted(String stato) {
        Tavolo t = new Tavolo(1, 5, stato, 4, "");
        assertEquals(stato, t.getStato(), "Lo stato dovrebbe essere valorizzato correttamente");
    }

    /**
     * Verifica che il numero di posti sia sempre positivo.
     */
    @Test
    void posti_positiveValue_valid() {
        Tavolo t = new Tavolo(1, 5, "Libero", 4, "");
        assertTrue(t.getPosti() > 0, "Il numero di posti dovrebbe essere positivo");
    }

    /**
     * Verifica il cambio di stato da Libero a Occupato.
     */
    @Test
    void setStato_liberoToOccupato_changes() {
        Tavolo t = new Tavolo(1, 5, "Libero", 4, "");
        t.setStato("Occupato");
        assertEquals("Occupato", t.getStato(), "Lo stato dovrebbe cambiare a Occupato");
    }

    /**
     * Verifica il cambio di stato da Occupato a Libero.
     */
    @Test
    void setStato_occupatoToLibero_changes() {
        Tavolo t = new Tavolo(1, 5, "Occupato", 4, "Note test");
        t.setStato("Libero");
        assertEquals("Libero", t.getStato(), "Lo stato dovrebbe cambiare a Libero");
    }

    /**
     * Verifica che due tavoli con lo stesso ID siano uguali (equals/hashCode by id).
     */
    @Test
    void equals_byId_sameIdEquals() {
        Tavolo t1 = new Tavolo(1, 5, "Libero", 4, "");
        Tavolo t2 = new Tavolo(1, 10, "Occupato", 2, "Note");
        
        assertEquals(t1, t2, "Tavoli con lo stesso ID dovrebbero essere uguali");
        assertEquals(t1.hashCode(), t2.hashCode(), "HashCode dovrebbe essere uguale per stesso ID");
    }

    /**
     * Verifica che tavoli con ID diversi non siano uguali.
     */
    @Test
    void equals_differentId_notEquals() {
        Tavolo t1 = new Tavolo(1, 5, "Libero", 4, "");
        Tavolo t2 = new Tavolo(2, 5, "Libero", 4, "");
        
        assertNotEquals(t1, t2, "Tavoli con ID diversi non dovrebbero essere uguali");
    }

    /**
     * Verifica che le note possano essere nulle o vuote.
     */
    @Test
    void note_nullOrEmpty_accepted() {
        Tavolo t1 = new Tavolo(1, 5, "Libero", 4, null);
        Tavolo t2 = new Tavolo(2, 6, "Libero", 4, "");
        
        assertNull(t1.getNote(), "Note nulle dovrebbero essere accettate");
        assertEquals("", t2.getNote(), "Note vuote dovrebbero essere accettate");
    }

    /**
     * Verifica che il toString restituisca il numero del tavolo.
     */
    @Test
    void toString_returnsNumero() {
        Tavolo t = new Tavolo(1, 5, "Libero", 4, "");
        String result = t.toString();
        assertTrue(result.contains("5"), "toString dovrebbe contenere il numero del tavolo");
    }
}

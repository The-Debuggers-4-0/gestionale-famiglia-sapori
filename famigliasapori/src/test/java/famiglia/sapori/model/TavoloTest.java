package famiglia.sapori.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test unitari per la classe Tavolo
 */
public class TavoloTest {

    private Tavolo tavolo;

    @BeforeEach
    public void setUp() {
        // Arrange
        tavolo = new Tavolo(1, 5, "Libero", 4, "Tavolo vicino alla finestra");
    }

    /**
     * Testa che il costruttore della classe Tavolo inizializzi correttamente tutti i campi
     */
    @Test
    public void testCostruttoreTavolo() {
        // Assert - Verifica che l'oggetto sia stato creato correttamente
        assertNotNull(tavolo);
        assertEquals(1, tavolo.getId());
        assertEquals(5, tavolo.getNumero());
        assertEquals("Libero", tavolo.getStato());
        assertEquals(4, tavolo.getPosti());
        assertEquals("Tavolo vicino alla finestra", tavolo.getNote());
    }

    /**
     * Testa che il metodo getId restituisca l'ID corretto del tavolo
     */
    @Test
    public void testGetId() {
        assertEquals(1, tavolo.getId());
    }

    /**
     * Testa che il metodo getNumero restituisca il numero del tavolo
     */
    @Test
    public void testGetNumero() {
        assertEquals(5, tavolo.getNumero());
    }

    /**
     * Testa che il metodo getStato restituisca lo stato attuale del tavolo
     */
    @Test
    public void testGetStato() {
        assertEquals("Libero", tavolo.getStato());
    }

    /**
     * Testa che il metodo setStato modifichi correttamente lo stato del tavolo
     */
    @Test
    public void testSetStato() {
        // Act
        tavolo.setStato("Occupato");
        
        // Assert
        assertEquals("Occupato", tavolo.getStato());
    }

    /**
     * Testa che il metodo getPosti restituisca il numero di posti disponibili
     */
    @Test
    public void testGetPosti() {
        assertEquals(4, tavolo.getPosti());
    }

    /**
     * Testa che il metodo getNote restituisca le note associate al tavolo
     */
    @Test
    public void testGetNote() {
        assertEquals("Tavolo vicino alla finestra", tavolo.getNote());
    }

    /**
     * Testa i diversi stati possibili di un tavolo (Libero, Occupato, Riservato)
     */
    @Test
    public void testStatiTavoloDiversi() {
        // Test dei diversi stati possibili
        tavolo.setStato("Libero");
        assertEquals("Libero", tavolo.getStato());
        
        tavolo.setStato("Occupato");
        assertEquals("Occupato", tavolo.getStato());
        
        tavolo.setStato("Riservato");
        assertEquals("Riservato", tavolo.getStato());
    }

    /**
     * Testa il comportamento con note vuote (edge case)
     */
    @Test
    public void testTavoloConNoteVuote() {
        // Test edge case con note vuote
        Tavolo tavoloSenzaNote = new Tavolo(2, 3, "Libero", 2, "");
        
        assertEquals("", tavoloSenzaNote.getNote());
    }

    /**
     * Testa tavoli con diverse capacità di posti a sedere
     */
    @Test
    public void testTavoliConPostiDiversi() {
        // Test per tavoli con diverse capacità
        Tavolo tavoloPiccolo = new Tavolo(3, 1, "Libero", 2, "Tavolo per due");
        Tavolo tavoloGrande = new Tavolo(4, 10, "Libero", 8, "Tavolo per gruppo");
        
        assertEquals(2, tavoloPiccolo.getPosti());
        assertEquals(8, tavoloGrande.getPosti());
    }

    /**
     * Testa che lo stato del tavolo possa essere cambiato multiple volte
     */
    @Test
    public void testCambioStatoMultiplo() {
        // Test per verificare che lo stato possa cambiare più volte
        assertEquals("Libero", tavolo.getStato());
        
        tavolo.setStato("Occupato");
        assertEquals("Occupato", tavolo.getStato());
        
        tavolo.setStato("Libero");
        assertEquals("Libero", tavolo.getStato());
    }
}
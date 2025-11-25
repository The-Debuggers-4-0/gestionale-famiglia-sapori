package famiglia.sapori.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test unitari per la classe Piatto
 */
public class PiattoTest {

    private Piatto piatto;

    @BeforeEach
    public void setUp() {
        // Arrange
        piatto = new Piatto(
            1,
            "Pizza Margherita",
            "Pizza con pomodoro, mozzarella e basilico",
            8.50,
            "Pizza",
            true,
            "Glutine, Lattosio"
        );
    }

    /**
     * Testa che il costruttore della classe Piatto inizializzi correttamente tutti i campi
     */
    @Test
    public void testCostruttorePiatto() {
        // Assert - Verifica che l'oggetto sia stato creato correttamente
        assertNotNull(piatto);
        assertEquals(1, piatto.getId());
        assertEquals("Pizza Margherita", piatto.getNome());
        assertEquals("Pizza con pomodoro, mozzarella e basilico", piatto.getDescrizione());
        assertEquals(8.50, piatto.getPrezzo(), 0.01);
        assertEquals("Pizza", piatto.getCategoria());
        assertTrue(piatto.isDisponibile());
        assertEquals("Glutine, Lattosio", piatto.getAllergeni());
    }

    /**
     * Testa che il metodo getId restituisca l'ID corretto del piatto
     */
    @Test
    public void testGetId() {
        assertEquals(1, piatto.getId());
    }

    /**
     * Testa che il metodo getNome restituisca il nome del piatto
     */
    @Test
    public void testGetNome() {
        assertEquals("Pizza Margherita", piatto.getNome());
    }

    /**
     * Testa che il metodo getDescrizione restituisca la descrizione del piatto
     */
    @Test
    public void testGetDescrizione() {
        assertEquals("Pizza con pomodoro, mozzarella e basilico", piatto.getDescrizione());
    }

    /**
     * Testa che il metodo getPrezzo restituisca il prezzo corretto del piatto
     */
    @Test
    public void testGetPrezzo() {
        assertEquals(8.50, piatto.getPrezzo(), 0.01);
    }

    /**
     * Testa che il metodo getCategoria restituisca la categoria del piatto
     */
    @Test
    public void testGetCategoria() {
        assertEquals("Pizza", piatto.getCategoria());
    }

    /**
     * Testa che il metodo isDisponibile restituisca la disponibilità del piatto
     */
    @Test
    public void testIsDisponibile() {
        assertTrue(piatto.isDisponibile());
    }

    /**
     * Testa che il metodo getAllergeni restituisca gli allergeni del piatto
     */
    @Test
    public void testGetAllergeni() {
        assertEquals("Glutine, Lattosio", piatto.getAllergeni());
    }

    /**
     * Testa che il metodo toString restituisca il nome del piatto
     */
    @Test
    public void testToString() {
        assertEquals("Pizza Margherita", piatto.toString());
    }

    /**
     * Testa un piatto non disponibile (edge case)
     */
    @Test
    public void testPiattoNonDisponibile() {
        // Test per un piatto non disponibile
        Piatto piattoNonDisponibile = new Piatto(
            2, "Pasta al Tartufo", "Pasta con tartufo nero", 15.00,
            "Primi", false, "Glutine"
        );
        
        assertFalse(piattoNonDisponibile.isDisponibile());
    }

    /**
     * Testa un piatto senza allergeni (edge case)
     */
    @Test
    public void testPiattoSenzaAllergeni() {
        // Test per un piatto senza allergeni
        Piatto piattoSenzaAllergeni = new Piatto(
            3, "Insalata Verde", "Insalata mista di stagione", 6.00,
            "Contorni", true, ""
        );
        
        assertEquals("", piattoSenzaAllergeni.getAllergeni());
    }

    /**
     * Testa piatti di categorie diverse (Primi, Secondi, Dolci)
     */
    @Test
    public void testCategorieDiverse() {
        // Test per diverse categorie di piatti
        Piatto primo = new Piatto(4, "Spaghetti", "Pasta italiana", 10.00, "Primi", true, "Glutine");
        Piatto secondo = new Piatto(5, "Bistecca", "Carne alla griglia", 18.00, "Secondi", true, "");
        Piatto dolce = new Piatto(6, "Tiramisù", "Dolce italiano", 5.50, "Dolci", true, "Uova, Lattosio");
        
        assertEquals("Primi", primo.getCategoria());
        assertEquals("Secondi", secondo.getCategoria());
        assertEquals("Dolci", dolce.getCategoria());
    }

    /**
     * Testa piatti con prezzi diversi per verificare la corretta gestione dei valori monetari
     */
    @Test
    public void testPrezziDiversi() {
        // Test per verificare la gestione corretta dei prezzi
        Piatto economico = new Piatto(7, "Acqua", "Acqua naturale", 1.50, "Bevande", true, "");
        Piatto costoso = new Piatto(8, "Aragosta", "Aragosta fresca", 45.00, "Pesce", true, "Crostacei");
        
        assertEquals(1.50, economico.getPrezzo(), 0.01);
        assertEquals(45.00, costoso.getPrezzo(), 0.01);
    }
}
package famiglia.sapori.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

/**
 * Test unitari per la classe Comanda
 */
public class ComandaTest {

    private Comanda comanda;
    private LocalDateTime dataTest;

    @BeforeEach
    public void setUp() {
        // Arrange
        dataTest = LocalDateTime.of(2025, 11, 24, 12, 30, 0);
        comanda = new Comanda(
            1,                      // id
            5,                      // idTavolo
            "Pizza Margherita x2",  // prodotti
            "cibo",                 // tipo
            "in_preparazione",      // stato
            dataTest,               // dataOra
            "Senza cipolla",        // note
            10                      // idCameriere
        );
    }

    /**
     * Testa che il costruttore della classe Comanda inizializzi correttamente tutti i campi
     */
    @Test
    public void testCostruttoreComanda() {
        // Assert - Verifica che l'oggetto sia stato creato correttamente
        assertNotNull(comanda);
        assertEquals(1, comanda.getId());
        assertEquals(5, comanda.getIdTavolo());
        assertEquals("Pizza Margherita x2", comanda.getProdotti());
        assertEquals("cibo", comanda.getTipo());
        assertEquals("in_preparazione", comanda.getStato());
        assertEquals(dataTest, comanda.getDataOra());
        assertEquals("Senza cipolla", comanda.getNote());
        assertEquals(10, comanda.getIdCameriere());
    }

    /**
     * Testa che il metodo getId restituisca l'ID corretto della comanda
     */
    @Test
    public void testGetId() {
        assertEquals(1, comanda.getId());
    }

    /**
     * Testa che il metodo getIdTavolo restituisca l'ID del tavolo associato
     */
    @Test
    public void testGetIdTavolo() {
        assertEquals(5, comanda.getIdTavolo());
    }

    /**
     * Testa che il metodo getProdotti restituisca la lista dei prodotti ordinati
     */
    @Test
    public void testGetProdotti() {
        assertEquals("Pizza Margherita x2", comanda.getProdotti());
    }

    /**
     * Testa che il metodo getTipo restituisca il tipo di comanda (cibo/bevanda)
     */
    @Test
    public void testGetTipo() {
        assertEquals("cibo", comanda.getTipo());
    }

    /**
     * Testa che il metodo getStato restituisca lo stato attuale della comanda
     */
    @Test
    public void testGetStato() {
        assertEquals("in_preparazione", comanda.getStato());
    }

    /**
     * Testa che il metodo setStato modifichi correttamente lo stato della comanda
     */
    @Test
    public void testSetStato() {
        // Act
        comanda.setStato("completata");
        
        // Assert
        assertEquals("completata", comanda.getStato());
    }

    /**
     * Testa che il metodo getDataOra restituisca la data e ora di creazione
     */
    @Test
    public void testGetDataOra() {
        assertEquals(dataTest, comanda.getDataOra());
    }

    /**
     * Testa che il metodo getNote restituisca le note associate alla comanda
     */
    @Test
    public void testGetNote() {
        assertEquals("Senza cipolla", comanda.getNote());
    }

    /**
     * Testa che il metodo getIdCameriere restituisca l'ID del cameriere
     */
    @Test
    public void testGetIdCameriere() {
        assertEquals(10, comanda.getIdCameriere());
    }

    /**
     * Testa il comportamento con note vuote (edge case)
     */
    @Test
    public void testComandaConNoteVuote() {
        // Test edge case con note vuote
        Comanda comandaSenzaNote = new Comanda(
            2, 3, "Acqua x1", "bevanda", "servita", 
            LocalDateTime.now(), "", 5
        );
        
        assertEquals("", comandaSenzaNote.getNote());
    }

    /**
     * Testa i diversi stati possibili di una comanda (ricevuta, in preparazione, pronta, servita)
     */
    @Test
    public void testStatiComandaDiversi() {
        // Test dei diversi stati possibili
        comanda.setStato("ricevuta");
        assertEquals("ricevuta", comanda.getStato());
        
        comanda.setStato("in_preparazione");
        assertEquals("in_preparazione", comanda.getStato());
        
        comanda.setStato("pronta");
        assertEquals("pronta", comanda.getStato());
        
        comanda.setStato("servita");
        assertEquals("servita", comanda.getStato());
    }

    /**
     * Testa i diversi tipi di comanda (cibo e bevande)
     */
    @Test
    public void testTipiComandaDiversi() {
        // Test per diversi tipi di comanda
        Comanda comandaCibo = new Comanda(3, 1, "Pasta", "cibo", "ricevuta", LocalDateTime.now(), "", 1);
        Comanda comandaBevanda = new Comanda(4, 1, "Vino", "bevanda", "servita", LocalDateTime.now(), "", 1);
        
        assertEquals("cibo", comandaCibo.getTipo());
        assertEquals("bevanda", comandaBevanda.getTipo());
    }
}
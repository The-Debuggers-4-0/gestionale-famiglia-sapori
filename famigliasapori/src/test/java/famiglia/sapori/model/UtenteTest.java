package famiglia.sapori.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test unitari per la classe Utente
 */
public class UtenteTest {

    private Utente utente;

    @BeforeEach
    public void setUp() {
        // Arrange - Prepara un utente di test
        utente = new Utente(1, "Mario Rossi", "mario.rossi", "password123", "cameriere");
    }

    /**
     * Testa che il costruttore della classe Utente inizializzi correttamente tutti i campi
     */
    @Test
    public void testCostruttoreUtente() {
        // Assert - Verifica che l'oggetto sia stato creato correttamente
        assertNotNull(utente);
        assertEquals(1, utente.getId());
        assertEquals("Mario Rossi", utente.getNome());
        assertEquals("mario.rossi", utente.getUsername());
        assertEquals("password123", utente.getPassword());
        assertEquals("cameriere", utente.getRuolo());
    }

    /**
     * Testa che il metodo getId restituisca l'ID corretto dell'utente
     */
    @Test
    public void testGetId() {
        
        assertEquals(1, utente.getId());
    }

    /**
     * Testa che il metodo getNome restituisca il nome corretto dell'utente
     */
    @Test
    public void testGetNome() {
        
        assertEquals("Mario Rossi", utente.getNome());
    }

    /**
     * Testa che il metodo getUsername restituisca lo username corretto
     */
    @Test
    public void testGetUsername() {
        
        assertEquals("mario.rossi", utente.getUsername());
    }

    /**
     * Testa che il metodo getPassword restituisca la password corretta
     */
    @Test
    public void testGetPassword() {
    
        assertEquals("password123", utente.getPassword());
    }

    /**
     * Testa che il metodo getRuolo restituisca il ruolo corretto dell'utente
     */
    @Test
    public void testGetRuolo() {
       
        assertEquals("cameriere", utente.getRuolo());
    }

    /**
     * Testa la creazione di utenti con ruoli diversi (cuoco, manager, cameriere)
     */
    @Test
    public void testUtenteConRuoliDiversi() {
        // Test per diversi ruoli
        Utente cuoco = new Utente(2, "Luca Bianchi", "luca.cuoco", "pass456", "cuoco");
        Utente manager = new Utente(3, "Anna Verdi", "anna.manager", "pass789", "manager");

        assertEquals("cuoco", cuoco.getRuolo());
        assertEquals("manager", manager.getRuolo());
    }

    /**
     * Testa il comportamento con valori vuoti o di default (edge case)
     */
    @Test
    public void testUtenteConDatiVuoti() {
        
        Utente utenteVuoto = new Utente(0, "", "", "", "");
        
        assertEquals(0, utenteVuoto.getId());
        assertEquals("", utenteVuoto.getNome());
        assertEquals("", utenteVuoto.getUsername());
        assertEquals("", utenteVuoto.getPassword());
        assertEquals("", utenteVuoto.getRuolo());
    }
}
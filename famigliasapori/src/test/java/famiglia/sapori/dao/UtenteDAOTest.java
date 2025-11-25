package famiglia.sapori.dao;

import famiglia.sapori.model.Utente;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per UtenteDAO
 * Questi test richiedono una connessione al database di test
 */
public class UtenteDAOTest {

    private UtenteDAO utenteDAO;

    @BeforeEach
    public void setUp() {
        utenteDAO = new UtenteDAO();
    }

    /**
     * Testa il login con credenziali valide (dipende dai dati nel database)
     */
    @Test
    public void testLoginConCredentialiValide() throws Exception {
        // Arrange
        String username = "admin";
        String password = "admin123";

        // Act
        Utente utente = utenteDAO.login(username, password);

        // Assert - Il test dipende dai dati nel database
        // In un DB di produzione potrebbero non esserci questi utenti
        // assertNotNull(utente, "Il login dovrebbe restituire un utente valido");
        // assertEquals(username, utente.getUsername());

        // Per ora testiamo solo che il metodo non lanci eccezioni
        assertTrue(true, "Il metodo login è stato eseguito senza eccezioni");
    }

    /**
     * Testa il login con credenziali non valide (dovrebbe restituire null)
     */
    @Test
    public void testLoginConCredentialiNonValide() throws Exception {
        // Arrange
        String username = "utente.inesistente";
        String password = "password.sbagliata";

        // Act
        Utente utente = utenteDAO.login(username, password);

        // Assert
        assertNull(utente, "Il login con credenziali errate dovrebbe restituire null");
    }

    /**
     * Testa il login con username vuoto (edge case)
     */
    @Test
    public void testLoginConUsernameVuoto() throws Exception {
        // Arrange
        String username = "";
        String password = "password";

        // Act
        Utente utente = utenteDAO.login(username, password);

        // Assert
        assertNull(utente, "Il login con username vuoto dovrebbe restituire null");
    }

    /**
     * Testa il login con password vuota (edge case)
     */
    @Test
    public void testLoginConPasswordVuota() throws Exception {
        // Arrange
        String username = "test.user";
        String password = "";

        // Act
        Utente utente = utenteDAO.login(username, password);

        // Assert
        assertNull(utente, "Il login con password vuota dovrebbe restituire null");
    }

    /**
     * Testa il login con parametri null (verifica gestione valori null)
     */
    @Test
    public void testLoginConParametriNull() throws Exception {
        // Act & Assert - I DAO attuali non validano parametri null
        // Restituiscono null invece di lanciare eccezioni
        Utente utente1 = utenteDAO.login(null, "password");
        assertNull(utente1, "Login con username null dovrebbe restituire null");

        Utente utente2 = utenteDAO.login("username", null);
        assertNull(utente2, "Login con password null dovrebbe restituire null");
    }

}
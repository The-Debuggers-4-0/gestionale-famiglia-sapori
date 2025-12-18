package famiglia.sapori.dao;

import famiglia.sapori.model.Utente;
import famiglia.sapori.database.DatabaseTestBase;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UtenteDAOTest extends DatabaseTestBase {

    /**
     * Verifica che con credenziali valide venga restituito un utente corretto.
     */
    @Test
    void login_withValidCredentials_returnsUser() throws SQLException {
        UtenteDAO dao = new UtenteDAO();
        Utente u = dao.login("mario", "pwd123");
        assertNotNull(u);
        assertEquals("mario", u.getUsername());
        assertEquals("Cameriere", u.getRuolo());
    }

    /**
     * Controlla che con credenziali errate venga restituito null.
     */
    @Test
    void login_withInvalidCredentials_returnsNull() throws SQLException {
        UtenteDAO dao = new UtenteDAO();
        Utente u = dao.login("mario", "wrong");
        assertNull(u);
    }

    /**
     * Verifica che username inesistente restituisca null.
     */
    @Test
    void login_withNonExistentUsername_returnsNull() throws SQLException {
        UtenteDAO dao = new UtenteDAO();
        Utente u = dao.login("nonexistent", "anypassword");
        assertNull(u, "Username inesistente dovrebbe restituire null");
    }

    /**
     * Verifica che password vuota non consenta il login.
     */
    @Test
    void login_withEmptyPassword_returnsNull() throws SQLException {
        UtenteDAO dao = new UtenteDAO();
        Utente u = dao.login("mario", "");
        assertNull(u, "Password vuota non dovrebbe consentire il login");
    }

    /**
     * Verifica che username vuoto non consenta il login.
     */
    @Test
    void login_withEmptyUsername_returnsNull() throws SQLException {
        UtenteDAO dao = new UtenteDAO();
        Utente u = dao.login("", "pwd123");
        assertNull(u, "Username vuoto non dovrebbe consentire il login");
    }

    /**
     * Verifica che il login sia case-sensitive per l'username.
     */
    @Test
    void login_usernameCaseSensitive() throws SQLException {
        UtenteDAO dao = new UtenteDAO();
        Utente validUser = dao.login("mario", "pwd123");
        assertNotNull(validUser, "Login con username corretto dovrebbe funzionare");
        
        Utente invalidUser = dao.login("MARIO", "pwd123");
        assertNull(invalidUser, "Username dovrebbe essere case-sensitive");
    }

    @Test
    void getAllInsertUpdateDeleteUtente_roundTrip() throws SQLException {
        UtenteDAO dao = new UtenteDAO();

        assertTrue(dao.getAllUtenti().size() >= 2, "Dovrebbero esserci utenti seed");

        String uniqueUsername = "user_" + UUID.randomUUID();
        Utente toInsert = new Utente(0, "Test User", uniqueUsername, "pw", "Cameriere");
        dao.insertUtente(toInsert);

        Utente inserted = dao.getAllUtenti().stream()
                .filter(u -> uniqueUsername.equals(u.getUsername()))
                .findFirst()
                .orElseThrow();
        assertEquals("Test User", inserted.getNome());
        assertEquals("Cameriere", inserted.getRuolo());

        Utente toUpdate = new Utente(inserted.getId(), "Test User 2", uniqueUsername, "pw2", "Gestore");
        dao.updateUtente(toUpdate);

        Utente updated = dao.getAllUtenti().stream()
                .filter(u -> u.getId() == inserted.getId())
                .findFirst()
                .orElseThrow();
        assertEquals("Test User 2", updated.getNome());
        assertEquals("pw2", updated.getPassword());
        assertEquals("Gestore", updated.getRuolo());

        dao.deleteUtente(inserted.getId());
        assertTrue(dao.getAllUtenti().stream().noneMatch(u -> u.getId() == inserted.getId()));
    }
}

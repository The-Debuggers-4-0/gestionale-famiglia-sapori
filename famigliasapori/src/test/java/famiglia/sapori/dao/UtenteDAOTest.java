package famiglia.sapori.dao;

import famiglia.sapori.model.Utente;
import famiglia.sapori.database.DatabaseTestBase;
import famiglia.sapori.util.PasswordUtil;
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
        
        // Setup
        UtenteDAO dao = new UtenteDAO();

        // Esegui il login con credenziali valide (passando l'hash della password come farebbe il controller)
        String pwdHash = PasswordUtil.hashPassword("pwd123");
        Utente u = dao.login("mario", pwdHash);

        // Verifiche
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

        // Prova con username corretto
        String pwdHash = PasswordUtil.hashPassword("pwd123");
        Utente validUser = dao.login("mario", pwdHash);
        assertNotNull(validUser, "Login con username corretto dovrebbe funzionare");
        
        // Prova con username in maiuscolo
        Utente invalidUser = dao.login("MARIO", pwdHash);
        assertNull(invalidUser, "Username dovrebbe essere case-sensitive");
    }

    // Verifica l'inserimento, l'aggiornamento e la cancellazione di un utente.
    @Test
    void getAllInsertUpdateDeleteUtente_roundTrip() throws SQLException {
        UtenteDAO dao = new UtenteDAO();

        // Verifica che ci siano utenti seed
        assertTrue(dao.getAllUtenti().size() >= 2, "Dovrebbero esserci utenti seed");

        // Inserimento
        String uniqueUsername = "user_" + UUID.randomUUID();
        Utente toInsert = new Utente(0, "Test User", uniqueUsername, "pw", "Cameriere");
        dao.insertUtente(toInsert);

        // Verifica inserimento
        Utente inserted = dao.getAllUtenti().stream()
                .filter(u -> uniqueUsername.equals(u.getUsername()))
                .findFirst()
                .orElseThrow();
        assertEquals("Test User", inserted.getNome());
        assertEquals("Cameriere", inserted.getRuolo());

        // Aggiornamento
        Utente toUpdate = new Utente(inserted.getId(), "Test User 2", uniqueUsername, "pw2", "Gestore");
        dao.updateUtente(toUpdate);

        // Verifica aggiornamento
        Utente updated = dao.getAllUtenti().stream()
                .filter(u -> u.getId() == inserted.getId())
                .findFirst()
                .orElseThrow();
        assertEquals("Test User 2", updated.getNome());
        assertEquals("pw2", updated.getPassword());
        assertEquals("Gestore", updated.getRuolo());

        // Cancellazione
        dao.deleteUtente(inserted.getId());
        assertTrue(dao.getAllUtenti().stream().noneMatch(u -> u.getId() == inserted.getId()));
    }
}

package famiglia.sapori.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class UtenteTest {

    /**
     * Verifica che due utenti con lo stesso ID siano considerati uguali
     * indipendentemente dagli altri attributi (equals/hashCode by id).
     */
    @Test
    void equals_byId_sameIdEquals() {
        Utente u1 = new Utente(1, "Mario", "mario", "pass123", "Cameriere");
        Utente u2 = new Utente(1, "Luigi", "luigi", "pass456", "Cuoco");
        
        assertEquals(u1, u2, "Utenti con lo stesso ID dovrebbero essere uguali");
        assertEquals(u1.hashCode(), u2.hashCode(), "HashCode dovrebbe essere uguale per stesso ID");
    }

    /**
     * Verifica che utenti con ID diversi non siano uguali.
     */
    @Test
    void equals_differentId_notEquals() {
        Utente u1 = new Utente(1, "Mario", "mario", "pass123", "Cameriere");
        Utente u2 = new Utente(2, "Mario", "mario", "pass123", "Cameriere");
        
        assertNotEquals(u1, u2, "Utenti con ID diversi non dovrebbero essere uguali");
    }

    /**
     * Verifica che i ruoli validi siano riconosciuti correttamente.
     * Ruoli attesi: Cameriere, Cuoco, Barista, Cassiere.
     */
    @ParameterizedTest
    @ValueSource(strings = {"Cameriere", "Cuoco", "Barista", "Cassiere"})
    void ruolo_validRoles_accepted(String ruolo) {
        Utente u = new Utente(1, "Test", "test", "pass", ruolo);
        assertEquals(ruolo, u.getRuolo(), "Il ruolo dovrebbe essere valorizzato correttamente");
    }

    /**
     * Verifica che il toString restituisca il nome utente.
     */
    @Test
    void toString_returnsNome() {
        Utente u = new Utente(1, "Mario Rossi", "mario", "pass123", "Cameriere");
        String result = u.toString();
        assertTrue(result.contains("Mario"), "toString dovrebbe contenere il nome");
    }

    /**
     * Verifica che username sia case-sensitive (almeno nella rappresentazione).
     */
    @Test
    void username_caseSensitive() {
        Utente u1 = new Utente(1, "Test", "Admin", "pass", "Cameriere");
        Utente u2 = new Utente(2, "Test", "admin", "pass", "Cameriere");
        
        assertNotEquals(u1.getUsername(), u2.getUsername(), "Username dovrebbe essere case-sensitive");
    }
}

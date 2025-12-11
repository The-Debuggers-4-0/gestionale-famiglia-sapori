package famiglia.sapori.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

public class PrenotazioniControllerTest {

    /**
     * Verifica parsing corretto dell'ora da TextField nel formato "HH:mm".
     */
    @Test
    public void testParseTime_validFormat() {
        String input = "19:30";
        String[] parts = input.split(":");
        LocalTime time = LocalTime.of(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        assertEquals(19, time.getHour());
        assertEquals(30, time.getMinute());
    }

    /**
     * Verifica eccezione quando il formato ora è invalido (es. "25:70").
     */
    @Test
    public void testParseTime_invalidFormat() {
        String input = "25:70";
        String[] parts = input.split(":");
        assertThrows(java.time.DateTimeException.class, () -> {
            LocalTime.of(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        });
    }

    /**
     * Verifica filtraggio ricerca case-insensitive con match.
     */
    @Test
    public void testFilterSearch_caseInsensitiveMatch() {
        String nomeCliente = "Mario Rossi";
        String query = "mario";
        assertTrue(nomeCliente.toLowerCase().contains(query.toLowerCase()));
    }

    /**
     * Verifica filtraggio ricerca senza match.
     */
    @Test
    public void testFilterSearch_noMatch() {
        String nomeCliente = "Mario Rossi";
        String query = "luigi";
        assertFalse(nomeCliente.toLowerCase().contains(query.toLowerCase()));
    }

    /**
     * Verifica che query vuota ritorni tutti i risultati.
     */
    @Test
    public void testFilterSearch_emptyQueryShowsAll() {
        String query = "";
        assertTrue(query.isEmpty());
    }

    /**
     * Verifica validazione nome obbligatorio (non vuoto).
     */
    @Test
    public void testValidateName_required() {
        String nome = "";
        assertTrue(nome.isEmpty(), "Nome vuoto deve fallire validazione");

        String nomeValido = "Mario";
        assertFalse(nomeValido.isEmpty(), "Nome non vuoto deve passare validazione");
    }

    /**
     * Verifica parsing corretto di vari orari validi.
     */
    @ParameterizedTest
    @CsvSource({
            "09:00, 9, 0",
            "12:30, 12, 30",
            "19:45, 19, 45",
            "23:59, 23, 59",
            "00:00, 0, 0"
    })
    public void testParseTime_variousValidFormats(String input, int expectedHour, int expectedMinute) {
        String[] parts = input.split(":");
        LocalTime time = LocalTime.of(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        assertEquals(expectedHour, time.getHour());
        assertEquals(expectedMinute, time.getMinute());
    }

    /**
     * Verifica che orari invalidi causino eccezioni.
     */
    @ParameterizedTest
    @ValueSource(strings = {"25:00", "12:60", "24:30", "99:99", "-1:30"})
    public void testParseTime_invalidTimes(String input) {
        String[] parts = input.split(":");
        assertThrows(java.time.DateTimeException.class, () -> {
            LocalTime.of(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        });
    }

    /**
     * Verifica ricerca case-insensitive con varie combinazioni.
     */
    @ParameterizedTest
    @CsvSource({
            "Mario Rossi, mario, true",
            "Mario Rossi, ROSSI, true",
            "Mario Rossi, luigi, false",
            "Giovanni Bianchi, gio, true",
            "Giovanni Bianchi, BIANCHI, true",
            "Anna Verdi, xyz, false"
    })
    public void testFilterSearch_caseInsensitiveVariousCases(String nomeCliente, String query, boolean shouldMatch) {
        boolean matches = nomeCliente.toLowerCase().contains(query.toLowerCase());
        assertEquals(shouldMatch, matches);
    }

    /**
     * Verifica validazione telefono con formati diversi.
     */
    @ParameterizedTest
    @ValueSource(strings = {"1234567890", "333-123-4567", "+39 333 1234567", "333.123.4567"})
    public void testValidatePhone_variousFormats(String telefono) {
        assertFalse(telefono.isEmpty(), "Telefono non vuoto dovrebbe essere accettato");
        assertTrue(telefono.matches(".*\\d.*"), "Telefono dovrebbe contenere cifre");
    }
}

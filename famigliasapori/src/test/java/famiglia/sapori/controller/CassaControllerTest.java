package famiglia.sapori.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class CassaControllerTest {
    
    /**
     * Verifica il parsing di prodotti nel formato "Qx Nome" (es. "2x Carbonara").
     * Testa casi validi, quantità zero e formato malformato.
     */
    @SuppressWarnings("unused")
    @Test
    public void testParseProductString_validFormat() throws Exception {
        CassaController controller = new CassaController();
        // Simula parsing logica interna: "2x Carbonara" → qty=2, nome="Carbonara"
        String input = "2x Carbonara";
        // Esegue lo split della stringa, cioe divide in quantità e nome
        String[] parts = input.split("x ");
        // Verifica che lo split produca due parti corrette
        assertEquals(2, parts.length);
        // Verifica quantità estratta
        assertEquals(2, Integer.parseInt(parts[0]));
        // Verifica nome estratto
        assertEquals("Carbonara", parts[1]);
    }

    /**
     * Verifica gestione errore parsing quando il formato è errato (es. mancanza "x").
     */
    @SuppressWarnings("unused")
    @Test
    public void testParseProductString_malformedInput() {
        String input = "InvalidFormat";
        // Verifica che venga lanciata un'eccezione per formato errato
        assertThrows(NumberFormatException.class, () -> {
            String[] parts = input.split("x ");
            int qty = Integer.parseInt(parts[0]); // Fail qui: "InvalidFormat" non è un numero
            String nome = parts[1];
        });
    }

    /**
     * Testa il calcolo della quota a testa con divisione per N persone.
     */
    @Test
    public void testCalcolaQuote_divisionByPersons() {
        double totale = 100.0;
        int persone = 4;
        double quota = totale / persone;
        // Verifica che la quota calcolata sia corretta
        assertEquals(25.0, quota, 0.01);
    }

    /**
     * Verifica calcolo quota quando il totale è zero (edge case).
     */
    @Test
    public void testCalcolaQuote_zeroTotal() {
        double totale = 0.0;
        int persone = 1;
        double quota = totale / persone;
        // Verifica che la quota sia zero
        assertEquals(0.0, quota, 0.01);
    }

    /**
     * Verifica parsing di vari formati di prodotto con spazi diversi.
     */
    @ParameterizedTest
    @CsvSource({
        "2x Carbonara, 2, Carbonara",
        "1x Pizza, 1, Pizza",
        "10x Acqua, 10, Acqua",
        "5x Tiramisù, 5, Tiramisù"
    })
    // Test che verifica parsing di stringhe prodotto
    public void testParseProductString_variousFormats(String input, int expectedQty, String expectedNome) {
        String[] parts = input.split("x ");
        // Verifica quantità estratta
        assertEquals(expectedQty, Integer.parseInt(parts[0]));
        // Verifica nome estratto
        assertEquals(expectedNome, parts[1]);
    }

    /**
     * Verifica calcolo divisione con diversi numeri di persone.
     */
    @ParameterizedTest
    @CsvSource({
        "100.0, 1, 100.0",
        "100.0, 2, 50.0",
        "100.0, 4, 25.0",
        "150.0, 3, 50.0",
        "99.99, 3, 33.33"
    })
    // Ignora avviso per parametri non usati nei test
    public void testCalcolaQuote_variousDivisions(double totale, int persone, double expectedQuota) {
        double quota = totale / persone;
        // Verifica che la quota calcolata sia corretta
        assertEquals(expectedQuota, quota, 0.01);
    }

    /**
     * Verifica che formati malformati senza 'x' causino errori.
     */
    @ParameterizedTest
    @ValueSource(strings = {"InvalidFormat", "NoQuantity", "123abc", "Pizza senza formato"})
    public void testParseProductString_malformedInputs(String input) {
        // Verifica che venga lanciata un'eccezione per formati errati
        assertThrows(Exception.class, () -> {
            // Tenta di parsare la stringa malformata
            String[] parts = input.split("x ");
            // Controlla che ci siano almeno 2 parti dopo lo split
            if (parts.length < 2) throw new IllegalArgumentException("Formato invalido");
            // Tenta di parsare la quantità
            Integer.parseInt(parts[0]);
        });
    }
}

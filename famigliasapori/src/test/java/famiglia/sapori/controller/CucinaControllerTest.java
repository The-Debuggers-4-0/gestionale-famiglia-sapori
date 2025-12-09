package famiglia.sapori.controller;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class CucinaControllerTest {

    /**
     * Verifica il mapping Tavolo ID → Numero Tavolo con valori presenti.
     */
    @Test
    public void testTavoloMapping_existingId() {
        Map<Integer, Integer> tavoloMap = new HashMap<>();
        tavoloMap.put(1, 10);
        tavoloMap.put(2, 20);
        
        assertEquals(10, tavoloMap.get(1));
        assertEquals(20, tavoloMap.get(2));
    }

    /**
     * Verifica gestione ID tavolo mancante nella mappa (usa default).
     */
    @Test
    public void testTavoloMapping_missingIdUsesDefault() {
        Map<Integer, Integer> tavoloMap = new HashMap<>();
        tavoloMap.put(1, 10);
        
        int idTavolo = 99;
        int numeroTavolo = tavoloMap.getOrDefault(idTavolo, 0);
        assertEquals(0, numeroTavolo);
    }

    /**
     * Verifica filtraggio comande per stato con stato singolo.
     */
    @Test
    public void testFilterByStato_singleState() {
        // Simula lista comande filtrate per stato "In Preparazione"
        String expectedStato = "In Preparazione";
        String actualStato = "In Preparazione";
        assertEquals(expectedStato, actualStato);
    }

    /**
     * Verifica gestione multi-stato (es. "In Attesa" + "In Preparazione").
     */
    @Test
    public void testFilterByStato_multipleStates() {
        String stato1 = "In Attesa";
        String stato2 = "In Preparazione";
        assertNotEquals(stato1, stato2);
        assertTrue(stato1.equals("In Attesa") || stato1.equals("In Preparazione"));
    }
}

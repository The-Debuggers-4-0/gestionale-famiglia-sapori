package famiglia.sapori.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ComandaTest {

    /**
     * Verifica la creazione di una comanda con tutti i parametri.
     */
    @Test
    void constructor_createsComandaWithAllFields() {
        LocalDateTime now = LocalDateTime.now();
        Comanda comanda = new Comanda(1, 5, "2x Pizza Margherita", "Cucina", "In attesa", now, "Senza cipolle", 3);
        
        assertEquals(1, comanda.getId());
        assertEquals(5, comanda.getIdTavolo());
        assertEquals("2x Pizza Margherita", comanda.getProdotti());
        assertEquals("Cucina", comanda.getTipo());
        assertEquals("In attesa", comanda.getStato());
        assertEquals(now, comanda.getDataOra());
        assertEquals("Senza cipolle", comanda.getNote());
        assertEquals(3, comanda.getIdCameriere());
    }

    /**
     * Verifica il setter dello stato.
     */
    @Test
    void setStato_updatesStatoCorrectly() {
        LocalDateTime now = LocalDateTime.now();
        Comanda comanda = new Comanda(1, 2, "1x Pasta", "Cucina", "In attesa", now, "", 1);
        
        assertEquals("In attesa", comanda.getStato());
        
        comanda.setStato("In preparazione");
        assertEquals("In preparazione", comanda.getStato());
        
        comanda.setStato("Pronto");
        assertEquals("Pronto", comanda.getStato());
        
        comanda.setStato("Servito");
        assertEquals("Servito", comanda.getStato());
    }

    /**
     * Verifica la creazione di comanda con note vuote.
     */
    @Test
    void constructor_acceptsEmptyNote() {
        LocalDateTime now = LocalDateTime.now();
        Comanda comanda = new Comanda(10, 3, "1x Acqua", "Bar", "Pronto", now, "", 2);
        
        assertNotNull(comanda.getNote());
        assertEquals("", comanda.getNote());
    }

    /**
     * Verifica che i getter restituiscano i valori corretti.
     */
    @Test
    void getters_returnCorrectValues() {
        LocalDateTime timestamp = LocalDateTime.of(2024, 12, 15, 20, 30);
        Comanda comanda = new Comanda(42, 7, "3x Birra", "Bar", "Servito", timestamp, "Fredda", 5);
        
        assertEquals(42, comanda.getId());
        assertEquals(7, comanda.getIdTavolo());
        assertEquals("3x Birra", comanda.getProdotti());
        assertEquals("Bar", comanda.getTipo());
        assertEquals("Servito", comanda.getStato());
        assertEquals(timestamp, comanda.getDataOra());
        assertEquals("Fredda", comanda.getNote());
        assertEquals(5, comanda.getIdCameriere());
    }

    /**
     * Verifica il cambio di stato attraverso workflow completo.
     */
    @Test
    void statoWorkflow_completesSuccessfully() {
        LocalDateTime now = LocalDateTime.now();
        Comanda comanda = new Comanda(1, 1, "1x Carbonara", "Cucina", "In attesa", now, "", 1);
        
        // Workflow: In attesa -> In preparazione -> Pronto -> Servito -> Pagato
        assertEquals("In attesa", comanda.getStato());
        
        comanda.setStato("In preparazione");
        assertEquals("In preparazione", comanda.getStato());
        
        comanda.setStato("Pronto");
        assertEquals("Pronto", comanda.getStato());
        
        comanda.setStato("Servito");
        assertEquals("Servito", comanda.getStato());
        
        comanda.setStato("Pagato");
        assertEquals("Pagato", comanda.getStato());
    }

    /**
     * Verifica la creazione di comande con diversi tipi (Cucina/Bar).
     */
    @Test
    void constructor_supportsDifferentTypes() {
        LocalDateTime now = LocalDateTime.now();
        
        Comanda cucinaComanda = new Comanda(1, 1, "Pasta", "Cucina", "Pronto", now, "", 1);
        assertEquals("Cucina", cucinaComanda.getTipo());
        
        Comanda barComanda = new Comanda(2, 1, "Caffè", "Bar", "Pronto", now, "", 1);
        assertEquals("Bar", barComanda.getTipo());
    }

    /**
     * Verifica che prodotti con quantità siano gestiti correttamente.
     */
    @Test
    void prodotti_handlesQuantityFormat() {
        LocalDateTime now = LocalDateTime.now();
        Comanda comanda = new Comanda(1, 3, "2x Pizza Margherita\n1x Coca Cola\n3x Birra", "Cucina", "In attesa", now, "", 2);
        
        String prodotti = comanda.getProdotti();
        assertTrue(prodotti.contains("2x Pizza Margherita"));
        assertTrue(prodotti.contains("1x Coca Cola"));
        assertTrue(prodotti.contains("3x Birra"));
    }
}

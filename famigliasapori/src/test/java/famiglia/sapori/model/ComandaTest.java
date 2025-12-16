package famiglia.sapori.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ComandaTest {

    /**
     * Verifica il cambio di stato attraverso workflow completo.
     */
    @Test
    void statoWorkflow_completesSuccessfully() {
        LocalDateTime now = LocalDateTime.now();
        Comanda comanda = new Comanda(1, 1, "1x Carbonara", 12.00, "Cucina", "In attesa", now, "", 1);
        
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
        
        Comanda cucinaComanda = new Comanda(1, 1, "Pasta", 10.00, "Cucina", "Pronto", now, "", 1);
        assertEquals("Cucina", cucinaComanda.getTipo());
        
        Comanda barComanda = new Comanda(2, 1, "Caffè", 2.00, "Bar", "Pronto", now, "", 1);
        assertEquals("Bar", barComanda.getTipo());
    }

    /**
     * Verifica che prodotti con quantità siano gestiti correttamente.
     */
    @Test
    void prodotti_handlesQuantityFormat() {
        LocalDateTime now = LocalDateTime.now();
        Comanda comanda = new Comanda(1, 3, "2x Pizza Margherita\n1x Coca Cola\n3x Birra", 25.50, "Cucina", "In attesa", now, "", 2);
        
        String prodotti = comanda.getProdotti();
        assertTrue(prodotti.contains("2x Pizza Margherita"));
        assertTrue(prodotti.contains("1x Coca Cola"));
        assertTrue(prodotti.contains("3x Birra"));
    }
}

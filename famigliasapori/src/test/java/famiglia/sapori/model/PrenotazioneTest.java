package famiglia.sapori.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class PrenotazioneTest {

    /**
     * Verifica che toString includa nome cliente, numero persone e orario formattato.
     */
    @Test
    void toString_containsClientePersoneOrario() {
        LocalDateTime now = LocalDateTime.of(2024, 1, 1, 19, 30);
        Prenotazione p = new Prenotazione(1, "Luca", "123", 2, now, "Note");
        String s = p.toString();
        assertTrue(s.contains("Luca"));
        assertTrue(s.contains("2p"));
        assertTrue(s.contains("19:30"));
    }

    /**
     * Controlla che l'idTavolo possa essere nullo e impostato a un valore.
     */
    @Test
    void idTavolo_canBeSetNull_orValue() {
        Prenotazione p = new Prenotazione(1, "Luca", "123", 2, LocalDateTime.now(), "Note", null);
        assertNull(p.getIdTavolo());
        p.setIdTavolo(5);
        assertEquals(5, p.getIdTavolo());
        p.setIdTavolo(null);
        assertNull(p.getIdTavolo());
    }
}

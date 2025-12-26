package famiglia.sapori.controller;

import famiglia.sapori.FamigliaSaporiApplication;
import famiglia.sapori.dao.ComandaDAO;
import famiglia.sapori.dao.PrenotazioneDAO;
import famiglia.sapori.dao.TavoloDAO;
import famiglia.sapori.model.Comanda;
import famiglia.sapori.model.Piatto;
import famiglia.sapori.model.Prenotazione;
import famiglia.sapori.model.Tavolo;
import famiglia.sapori.model.Utente;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class SalaControllerTest {

    private static final AtomicBoolean FX_INITIALIZED = new AtomicBoolean(false);

    // Inizializza JavaFX una volta prima di tutti i test
    @BeforeAll
    static void initJavaFx() {
        if (FX_INITIALIZED.compareAndSet(false, true)) {
            try {
                Platform.startup(() -> {
                });
            } catch (IllegalStateException ignored) {
                // JavaFX runtime already started
            }
        }
    }

    // Resetta l'utente corrente dopo ogni test
    @AfterEach
    void resetCurrentUser() {
        FamigliaSaporiApplication.currentUser = null;
    }

    // Esegue un'azione sul thread JavaFX e attende il completamento
    private static void runOnFxThread(Runnable action) throws Exception {
        if (Platform.isFxApplicationThread()) {
            action.run();
            return;
        }
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Throwable> error = new AtomicReference<>();
        Platform.runLater(() -> {
            try {
                action.run();
            } catch (Throwable t) {
                error.set(t);
            } finally {
                latch.countDown();
            }
        });
        assertTrue(latch.await(10, TimeUnit.SECONDS), "Timed out waiting for FX thread");
        if (error.get() != null) {
            throw new AssertionError(error.get());
        }
    }

    // Imposta il valore di un campo privato tramite reflection
    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(target, value);
    }

    // Invoca un metodo privato senza argomenti tramite reflection
    private static void invokeNoArg(Object target, String methodName) throws Exception {
        Method m = target.getClass().getDeclaredMethod(methodName);
        m.setAccessible(true);
        m.invoke(target);
    }

    // Invoca il metodo privato sendComanda tramite reflection
    private static void invokeSendComanda(SalaController controller, Map<Piatto, Integer> items, String tipo) throws Exception {
        Method m = SalaController.class.getDeclaredMethod("sendComanda", Map.class, String.class);
        m.setAccessible(true);
        m.invoke(controller, items, tipo);
    }

    // Fake DAO per i test
    private static final class FakeTavoloDAO extends TavoloDAO {
        private final List<Tavolo> tavoli;

        FakeTavoloDAO(List<Tavolo> tavoli) {
            this.tavoli = tavoli;
        }

        @Override
        public List<Tavolo> getAllTavoli() {
            return tavoli;
        }
    }

    // Fake DAO per i test
    private static final class FakePrenotazioneDAO extends PrenotazioneDAO {
        private final List<Prenotazione> prenotazioni;

        FakePrenotazioneDAO(List<Prenotazione> prenotazioni) {
            this.prenotazioni = prenotazioni;
        }

        @Override
        public List<Prenotazione> getReservationsForDate(LocalDate date) {
            return prenotazioni;
        }
    }

    private static class FakeComandaDAO extends ComandaDAO {
        Comanda lastInserted;

        @Override
        public void insertComanda(Comanda comanda) {
            lastInserted = comanda;
        }

        @Override
        public boolean hasPaidComandaAfter(int idTavolo, LocalDateTime after) throws SQLException {
            return false;
        }
    }

    // Fake DAO che simula comande pagate per un tavolo specifico
    private static final class FakeComandaDAOWithFulfilled extends FakeComandaDAO {
        private final int fulfilledTableId;

        FakeComandaDAOWithFulfilled(int fulfilledTableId) {
            this.fulfilledTableId = fulfilledTableId;
        }

        @Override
        public boolean hasPaidComandaAfter(int idTavolo, LocalDateTime after) {
            return idTavolo == fulfilledTableId;
        }
    }

    /**
     * Verifica la logica di classificazione drink del SalaController (non-FX)
     * chiamando il metodo privato via reflection con varie categorie.
     */
    @Test
    public void testIsDrinkCategories() throws Exception {
        SalaController controller = new SalaController();
        var m = SalaController.class.getDeclaredMethod("isDrink", String.class);
        m.setAccessible(true);
        assertFalse((boolean) m.invoke(controller, (Object) null));
        assertTrue((boolean) m.invoke(controller, "Bevande"));
        assertTrue((boolean) m.invoke(controller, "VINI Rossi"));
        assertTrue((boolean) m.invoke(controller, "birre"));
        assertTrue((boolean) m.invoke(controller, "caffè"));
        assertTrue((boolean) m.invoke(controller, "drink"));
        assertFalse((boolean) m.invoke(controller, "Secondi"));
    }

    // Test del metodo loadTavoli per verificare gli stati dei tavoli
    @Test
    void loadTavoli_appliesReservedOccupiedAndFreeStatuses() throws Exception {
        runOnFxThread(() -> {
            try {
                SalaController controller = new SalaController();

                // Configura il container dei tavoli
                FlowPane container = new FlowPane();
                setField(controller, "tavoliContainer", container);

                // Configura i dati di test
                List<Tavolo> tavoli = List.of(
                        new Tavolo(1, 1, "Prenotato", 4, ""),
                        new Tavolo(2, 2, "Occupato", 2, ""),
                        new Tavolo(3, 3, "Libero", 6, "")
                );

                // Configura le prenotazioni per il giorno corrente
                LocalDateTime now = LocalDateTime.now();
                List<Prenotazione> prenotazioni = List.of(
                        new Prenotazione(11, "A", "1", 2, now.plusHours(2), "", 1),
                        new Prenotazione(12, "B", "2", 2, now.plusHours(3), "", 2),
                        new Prenotazione(13, "C", "3", 2, now.plusHours(4), "", 3)
                );

                // Configura i DAO fittizi
                setField(controller, "tavoloDAO", new FakeTavoloDAO(tavoli));
                setField(controller, "prenotazioneDAO", new FakePrenotazioneDAO(prenotazioni));
                setField(controller, "comandaDAO", new FakeComandaDAOWithFulfilled(3));

                invokeNoArg(controller, "loadTavoli");

                // Verifica che i tavoli siano stati caricati correttamente
                assertEquals(3, container.getChildren().size());

                // Verifica i colori degli stati
                VBox box1 = (VBox) container.getChildren().get(0);
                Rectangle r1 = (Rectangle) box1.getChildren().get(0);
                assertEquals(Color.web("#f39c12"), r1.getFill());

                VBox box2 = (VBox) container.getChildren().get(1);
                Rectangle r2 = (Rectangle) box2.getChildren().get(0);
                assertEquals(Color.RED, r2.getFill());

                VBox box3 = (VBox) container.getChildren().get(2);
                Rectangle r3 = (Rectangle) box3.getChildren().get(0);
                assertEquals(Color.GREEN, r3.getFill());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    // Test del metodo sendComanda per verificare la costruzione della comanda
    @Test
    void sendComanda_buildsProductsAndTotalAndCopiesNote() throws Exception {
        runOnFxThread(() -> {
            try {
                SalaController controller = new SalaController();

                //  Configura il FakeComandaDAO
                FakeComandaDAO fake = new FakeComandaDAO();
                setField(controller, "comandaDAO", fake);

                // Configura il tavolo selezionato e le note
                setField(controller, "selectedTavolo", new Tavolo(5, 5, "Libero", 4, ""));
                TextArea note = new TextArea("nota prova");
                setField(controller, "txtNote", note);

                FamigliaSaporiApplication.currentUser = new Utente(9, "Test", "u", "p", "Cameriere");

                // Prepara gli elementi della comanda
                Piatto p1 = new Piatto(1, "Pizza", "", 6.00, "Primi", true, "");
                Piatto p2 = new Piatto(2, "Acqua", "", 1.50, "Bevande", true, "");

                // Costruisci la mappa degli elementi
                Map<Piatto, Integer> items = new LinkedHashMap<>();
                items.put(p1, 2);
                items.put(p2, 1);

                // Invia la comanda
                invokeSendComanda(controller, items, "Cucina");

                // Verifica che la comanda sia stata costruita correttamente
                assertNotNull(fake.lastInserted);
                assertEquals(5, fake.lastInserted.getIdTavolo());
                assertEquals("Cucina", fake.lastInserted.getTipo());
                assertEquals("In Attesa", fake.lastInserted.getStato());
                assertEquals("nota prova", fake.lastInserted.getNote());
                assertEquals(9, fake.lastInserted.getIdCameriere());

                // Verifica i prodotti e il totale
                String prodotti = fake.lastInserted.getProdotti();
                assertTrue(prodotti.contains("2x Pizza"), "Prodotti: " + prodotti);
                assertTrue(prodotti.contains("1x Acqua"), "Prodotti: " + prodotti);

                // Totale: 2*6.00 + 1*1.50 = 13.50
                assertEquals(13.50, fake.lastInserted.getTotale(), 0.0001);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}

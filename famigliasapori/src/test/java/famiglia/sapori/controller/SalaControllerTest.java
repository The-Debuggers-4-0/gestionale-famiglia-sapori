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

    @AfterEach
    void resetCurrentUser() {
        FamigliaSaporiApplication.currentUser = null;
    }

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

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(target, value);
    }

    private static void invokeNoArg(Object target, String methodName) throws Exception {
        Method m = target.getClass().getDeclaredMethod(methodName);
        m.setAccessible(true);
        m.invoke(target);
    }

    private static void invokeSendComanda(SalaController controller, Map<Piatto, Integer> items, String tipo) throws Exception {
        Method m = SalaController.class.getDeclaredMethod("sendComanda", Map.class, String.class);
        m.setAccessible(true);
        m.invoke(controller, items, tipo);
    }

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

    @Test
    void loadTavoli_appliesReservedOccupiedAndFreeStatuses() throws Exception {
        runOnFxThread(() -> {
            try {
                SalaController controller = new SalaController();

                FlowPane container = new FlowPane();
                setField(controller, "tavoliContainer", container);

                List<Tavolo> tavoli = List.of(
                        new Tavolo(1, 1, "Prenotato", 4, ""),
                        new Tavolo(2, 2, "Occupato", 2, ""),
                        new Tavolo(3, 3, "Libero", 6, "")
                );

                LocalDateTime now = LocalDateTime.now();
                List<Prenotazione> prenotazioni = List.of(
                        new Prenotazione(11, "A", "1", 2, now.plusHours(2), "", 1),
                        new Prenotazione(12, "B", "2", 2, now.plusHours(3), "", 2),
                        new Prenotazione(13, "C", "3", 2, now.plusHours(4), "", 3)
                );

                setField(controller, "tavoloDAO", new FakeTavoloDAO(tavoli));
                setField(controller, "prenotazioneDAO", new FakePrenotazioneDAO(prenotazioni));
                setField(controller, "comandaDAO", new FakeComandaDAOWithFulfilled(3));

                invokeNoArg(controller, "loadTavoli");

                assertEquals(3, container.getChildren().size());

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

    @Test
    void sendComanda_buildsProductsAndTotalAndCopiesNote() throws Exception {
        runOnFxThread(() -> {
            try {
                SalaController controller = new SalaController();

                FakeComandaDAO fake = new FakeComandaDAO();
                setField(controller, "comandaDAO", fake);

                setField(controller, "selectedTavolo", new Tavolo(5, 5, "Libero", 4, ""));
                TextArea note = new TextArea("nota prova");
                setField(controller, "txtNote", note);

                FamigliaSaporiApplication.currentUser = new Utente(9, "Test", "u", "p", "Cameriere");

                Piatto p1 = new Piatto(1, "Pizza", "", 6.00, "Primi", true, "");
                Piatto p2 = new Piatto(2, "Acqua", "", 1.50, "Bevande", true, "");

                Map<Piatto, Integer> items = new LinkedHashMap<>();
                items.put(p1, 2);
                items.put(p2, 1);

                invokeSendComanda(controller, items, "Cucina");

                assertNotNull(fake.lastInserted);
                assertEquals(5, fake.lastInserted.getIdTavolo());
                assertEquals("Cucina", fake.lastInserted.getTipo());
                assertEquals("In Attesa", fake.lastInserted.getStato());
                assertEquals("nota prova", fake.lastInserted.getNote());
                assertEquals(9, fake.lastInserted.getIdCameriere());

                String prodotti = fake.lastInserted.getProdotti();
                assertTrue(prodotti.contains("2x Pizza"), "Prodotti: " + prodotti);
                assertTrue(prodotti.contains("1x Acqua"), "Prodotti: " + prodotti);

                assertEquals(13.50, fake.lastInserted.getTotale(), 0.0001);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}

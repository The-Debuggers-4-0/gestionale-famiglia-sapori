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
import famiglia.sapori.dao.MenuDAO;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import java.util.ArrayList;
import java.util.HashMap;
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
        FamigliaSaporiApplication.setCurrentUser(null);
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
        Class<?> clazz = target.getClass();
        Field f = null;
        while (clazz != null) {
            try {
                f = clazz.getDeclaredField(fieldName);
                break;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        if (f == null) throw new NoSuchFieldException(fieldName);
        f.setAccessible(true);
        f.set(target, value);
    }

    // Ottiene il valore di un campo privato tramite reflection
    private static Object getField(Object target, String fieldName) throws Exception {
        Class<?> clazz = target.getClass();
        Field f = null;
        while (clazz != null) {
            try {
                f = clazz.getDeclaredField(fieldName);
                break;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        if (f == null) throw new NoSuchFieldException(fieldName);
        f.setAccessible(true);
        return f.get(target);
    }

    // Invoca un metodo privato senza argomenti tramite reflection
    private static void invokeNoArg(Object target, String methodName) throws Exception {
        Class<?> clazz = target.getClass();
        Method m = null;
        while (clazz != null) {
            try {
                m = clazz.getDeclaredMethod(methodName);
                break;
            } catch (NoSuchMethodException e) {
                clazz = clazz.getSuperclass();
            }
        }
        if (m == null) throw new NoSuchMethodException(methodName);
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

    // Fake DAOs
    private static class FakeMenuDAO extends MenuDAO {
        @Override public List<String> getAllCategorie() throws SQLException { return List.of("Primi", "Bevande"); }
        @Override public List<Piatto> getAllPiatti() throws SQLException {
            return List.of(
                new Piatto(1, "Pasta", "D", 10.0, "Primi", true, ""),
                new Piatto(2, "Acqua", "D", 2.0, "Bevande", true, ""),
                new Piatto(3, "Vino", "D", 5.0, "Bevande", false, "") // Not available
            );
        }
    }

    private static class FakeComandaDAOList extends ComandaDAO {
        List<Comanda> inserted = new ArrayList<>();
        @Override public void insertComanda(Comanda c) throws SQLException { inserted.add(c); }
        @Override public boolean hasPaidComandaAfter(int idTavolo, LocalDateTime after) throws SQLException { return false; }
    }

    private static class FakeTavoloDAOStatus extends TavoloDAO {
        boolean statusUpdated = false;
        @Override public void updateStatoTavolo(int id, String stato) throws SQLException { statusUpdated = true; }
        @Override public List<Tavolo> getAllTavoli() throws SQLException { return List.of(); }
    }

    private static class FakePrenotazioneDAODelete extends PrenotazioneDAO {
        boolean deleted = false;
        @Override public List<Prenotazione> getReservationsForDate(LocalDate date) throws SQLException { return List.of(); }
        @Override public void deletePrenotazione(int id) throws SQLException { deleted = true; }
    }

    @Test
    void testLoadMenuAndInteraction() throws Exception {
        runOnFxThread(() -> {
            try {
                SalaController controller = new SalaController();
                setField(controller, "menuDAO", new FakeMenuDAO());
                setField(controller, "currentOrder", new HashMap<Piatto, Integer>());
                
                TabPane tabPane = new TabPane();
                setField(controller, "menuTabPane", tabPane);
                setField(controller, "txtRiepilogo", new TextArea());
                setField(controller, "lblTotale", new Label());

                invokeNoArg(controller, "loadMenu");

                assertEquals(2, tabPane.getTabs().size());
                assertEquals("Primi", tabPane.getTabs().get(0).getText());
                assertEquals("Bevande", tabPane.getTabs().get(1).getText());

                // Check content of first tab
                ScrollPane scroll = (ScrollPane) tabPane.getTabs().get(0).getContent();
                VBox content = (VBox) scroll.getContent();
                assertEquals(1, content.getChildren().size()); // 1 Primi

                // Check content of second tab
                ScrollPane scroll2 = (ScrollPane) tabPane.getTabs().get(1).getContent();
                VBox content2 = (VBox) scroll2.getContent();
                assertEquals(2, content2.getChildren().size()); // 2 Bevande

                // Test interaction with buttons
                HBox row = (HBox) content.getChildren().get(0); // Pasta
                Button btnPlus = (Button) row.getChildren().get(4);
                Label lblQty = (Label) row.getChildren().get(3);
                
                btnPlus.fire();
                assertEquals("1", lblQty.getText());
                Map<Piatto, Integer> order = (Map<Piatto, Integer>) getField(controller, "currentOrder");
                assertEquals(1, order.size());
                
                btnPlus.fire();
                assertEquals("2", lblQty.getText());
                
                Button btnMinus = (Button) row.getChildren().get(2);
                btnMinus.fire();
                assertEquals("1", lblQty.getText());
                
                btnMinus.fire();
                assertEquals("0", lblQty.getText());
                assertTrue(order.isEmpty());

                // Test unavailable item
                HBox rowUnavailable = (HBox) content2.getChildren().get(1); // Vino (index 1 in Bevande)
                assertTrue(rowUnavailable.isDisabled());
                
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void testHandleInviaComanda() throws Exception {
        runOnFxThread(() -> {
            try {
                TestableSalaController controller = new TestableSalaController();
                FakeComandaDAOList comandaDAO = new FakeComandaDAOList();
                FakeTavoloDAOStatus tavoloDAO = new FakeTavoloDAOStatus();
                FakePrenotazioneDAODelete prenotazioneDAO = new FakePrenotazioneDAODelete();
                
                setField(controller, "comandaDAO", comandaDAO);
                setField(controller, "tavoloDAO", tavoloDAO);
                setField(controller, "prenotazioneDAO", prenotazioneDAO);
                setField(controller, "txtNote", new TextArea("Note"));
                setField(controller, "txtRiepilogo", new TextArea());
                setField(controller, "lblTotale", new Label());
                setField(controller, "menuTabPane", new TabPane());
                setField(controller, "menuDAO", new FakeMenuDAO()); // Needed for reset
                setField(controller, "tavoliContainer", new FlowPane()); // Needed for loadTavoli

                // Case 1: No table selected
                setField(controller, "selectedTavolo", null);
                invokeNoArg(controller, "handleInviaComanda");
                assertTrue(comandaDAO.inserted.isEmpty());

                // Case 2: Empty order
                Tavolo t = new Tavolo(1, 1, "Libero", 4, "");
                setField(controller, "selectedTavolo", t);
                setField(controller, "currentOrder", new HashMap<Piatto, Integer>());
                invokeNoArg(controller, "handleInviaComanda");
                assertTrue(comandaDAO.inserted.isEmpty());

                // Case 3: Not logged in
                Map<Piatto, Integer> order = new HashMap<>();
                order.put(new Piatto(1, "Pasta", "", 10.0, "Primi", true, ""), 1);
                setField(controller, "currentOrder", order);
                FamigliaSaporiApplication.setCurrentUser(null);
                invokeNoArg(controller, "handleInviaComanda");
                assertTrue(comandaDAO.inserted.isEmpty());

                // Case 4: Success (Split Kitchen/Bar)
                FamigliaSaporiApplication.setCurrentUser(new Utente(1, "U", "u", "p", "Cameriere"));
                order.put(new Piatto(2, "Acqua", "", 2.0, "Bevande", true, ""), 2);
                setField(controller, "currentOrder", order);
                
                invokeNoArg(controller, "handleInviaComanda");
                
                assertEquals(2, comandaDAO.inserted.size());
                boolean hasKitchen = comandaDAO.inserted.stream().anyMatch(c -> c.getTipo().equals("Cucina"));
                boolean hasBar = comandaDAO.inserted.stream().anyMatch(c -> c.getTipo().equals("Bar"));
                assertTrue(hasKitchen);
                assertTrue(hasBar);
                assertTrue(tavoloDAO.statusUpdated);
                
                // Verify Alert
                assertEquals("Successo", controller.lastAlertTitle);
                
                // Verify reset
                Map<Piatto, Integer> orderAfter = (Map<Piatto, Integer>) getField(controller, "currentOrder");
                assertTrue(orderAfter.isEmpty());

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static class TestableSalaController extends SalaController {
        public String lastAlertTitle;
        public String lastAlertContent;

        @Override
        protected void showAlert(String title, String content) {
            this.lastAlertTitle = title;
            this.lastAlertContent = content;
        }
    }
}

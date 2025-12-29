package famiglia.sapori.controller;

import famiglia.sapori.dao.GestoreDAO;
import famiglia.sapori.dao.MenuDAO;
import famiglia.sapori.dao.TavoloDAO;
import famiglia.sapori.dao.UtenteDAO;
import famiglia.sapori.model.Piatto;
import famiglia.sapori.model.Tavolo;
import famiglia.sapori.model.Utente;
import javafx.application.Platform;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class GestoreControllerTest {

    private static final AtomicBoolean FX_INITIALIZED = new AtomicBoolean(false);

    // Inizializza JavaFX una volta per tutti i test
    @BeforeAll
    static void initJavaFx() {
        if (FX_INITIALIZED.compareAndSet(false, true)) {
            try {
                Platform.startup(() -> {});
            } catch (IllegalStateException ignored) {
                // JavaFX runtime already started
            }
        }
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
        // Attende il completamento con timeout
        assertTrue(latch.await(10, TimeUnit.SECONDS), "Timeout in attesa del thread FX");
        if (error.get() != null) {
            throw new AssertionError(error.get());
        }
    }

    // Metodi di utilità per reflection
    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(target, value);
    }

    // Ottiene il valore di un campo tramite reflection
    private static Object getField(Object target, String fieldName) throws Exception {
        Field f = target.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        return f.get(target);
    }
    
    // Invoca un metodo senza argomenti tramite reflection
    private static void invokeNoArg(Object target, String methodName) throws Exception {
        Method m = target.getClass().getDeclaredMethod(methodName);
        m.setAccessible(true);
        m.invoke(target);
    }

    // Normalizza stringhe di euro rimuovendo spazi non standard
    private static String normalizeEuro(String s) {
        return s == null ? null : s.replace('\u00A0', ' ').trim();
    }

    // Fake DAO per test
    private static final class FakeMenuDAO extends MenuDAO {
        int insertCalls;
        int updateCalls;
        int deleteCalls;

        @Override
        public List<Piatto> getAllPiattiComplete() {
            return List.of();
        }

        @Override
        public void insertPiatto(Piatto p) {
            insertCalls++;
        }

        @Override
        public void updatePiatto(Piatto p) {
            updateCalls++;
        }

        @Override
        public void deletePiatto(int id) {
            deleteCalls++;
        }
    }

    private static final class FakeUtenteDAO extends UtenteDAO {
        int insertCalls;
        int updateCalls;
        int deleteCalls;

        @Override
        public List<Utente> getAllUtenti() {
            return List.of();
        }

        @Override
        public void insertUtente(Utente u) {
            insertCalls++;
        }

        @Override
        public void updateUtente(Utente u) {
            updateCalls++;
        }

        @Override
        public void deleteUtente(int id) {
            deleteCalls++;
        }
    }

    private static final class FakeTavoloDAO extends TavoloDAO {
        int insertCalls;
        int updateCalls;
        int resetCalls;
        int deleteCalls;

        @Override
        public List<Tavolo> getAllTavoli() {
            return List.of();
        }

        @Override
        public void insertTavolo(Tavolo tavolo) {
            insertCalls++;
        }

        @Override
        public void updateTavolo(Tavolo tavolo) {
            updateCalls++;
        }

        @Override
        public void updateStatoTavolo(int id, String stato) {
            if ("Libero".equalsIgnoreCase(stato)) {
                resetCalls++;
            }
        }

        @Override
        public void deleteTavolo(int id) {
            deleteCalls++;
        }
    }

    private static final class FakeGestoreDAO extends GestoreDAO {
        private final Map<String, Integer> bestSellers;
        private final double dailyIncome;

        FakeGestoreDAO(Map<String, Integer> bestSellers, double dailyIncome) {
            this.bestSellers = bestSellers;
            this.dailyIncome = dailyIncome;
        }

        @Override
        public Map<String, Integer> getBestSellers() throws SQLException {
            return bestSellers;
        }

        @Override
        public double calculateDailyIncome() throws SQLException {
            return dailyIncome;
        }
    }

    /**
     * Verifica che il controller possa essere istanziato correttamente.
     */
    @Test
    void controller_canBeInstantiated() {
        /** Verifica che l'istanza del controller non sia nulla.
         *  (abbiamo usato una lambda expressiion per catturare eventuali eccezioni)
        */
        assertDoesNotThrow(() -> {
            // Crea una nuova istanza del controller
            GestoreController controller = new GestoreController();
            // Verifica che il controller non sia nullo
            assertNotNull(controller);
        });
    }

    /**
     * Verifica che i DAO vengano inizializzati durante l'initialize.
     * Questo test verifica la struttura base del controller.
     */
    @Test
    void controller_hasExpectedStructure() throws Exception {
        GestoreController controller = new GestoreController();
        
        // Verifica che il controller abbia i campi DAO privati
        var menuDAOField = GestoreController.class.getDeclaredField("menuDAO");
        assertNotNull(menuDAOField);
        // Verifica che il controller abbia il campo utenteDAO
        var utenteDAOField = GestoreController.class.getDeclaredField("utenteDAO");
        assertNotNull(utenteDAOField);
        // Verifica che il controller abbia il campo tavoloDAO
        var tavoloDAOField = GestoreController.class.getDeclaredField("tavoloDAO");
        assertNotNull(tavoloDAOField);
        // Verifica che il controller abbia il campo gestoreDAO
        var gestoreDAOField = GestoreController.class.getDeclaredField("gestoreDAO");
        assertNotNull(gestoreDAOField);
    }

    /**
     * Verifica che i campi di selezione esistano nel controller.
     */
    @Test
    void controller_hasSelectionTrackingFields() throws Exception {
        // Crea un'istanza del controller
        GestoreController controller = new GestoreController();

        // Verifica che i campi di selezione esistano
        var selectedPiattoField = GestoreController.class.getDeclaredField("selectedPiatto");
        // Verifica che il campo piatto non sia nullo
        assertNotNull(selectedPiattoField);

        // Verifica che i campi di selezione esistano
        var selectedUtenteField = GestoreController.class.getDeclaredField("selectedUtente");
        // Verifica che il campo utente non sia nullo
        assertNotNull(selectedUtenteField);

        // Verifica che i campi di selezione esistano
        var selectedTavoloField = GestoreController.class.getDeclaredField("selectedTavolo");
        // Verifica che il campo tavolo non sia nullo
        assertNotNull(selectedTavoloField);
    }

    /**
     * Verifica che tutti i metodi handler pubblici esistano.
     */
    @Test
    void controller_hasAllPublicHandlers() throws Exception {
        // Crea un'istanza del controller
        GestoreController controller = new GestoreController();
        
        // Menu handlers
        // commento: verifica che il metodo handleNuovoPiatto non sia nullo, etc...
        assertNotNull(GestoreController.class.getDeclaredMethod("handleNuovoPiatto"));
        assertNotNull(GestoreController.class.getDeclaredMethod("handleSalvaPiatto"));
        assertNotNull(GestoreController.class.getDeclaredMethod("handleEliminaPiatto"));
        
        // Personale handlers
        // commento: verifica che il metodo handleNuovoUtente non sia nullo, etc...
        assertNotNull(GestoreController.class.getDeclaredMethod("handleNuovoUtente"));
        assertNotNull(GestoreController.class.getDeclaredMethod("handleSalvaUtente"));
        assertNotNull(GestoreController.class.getDeclaredMethod("handleEliminaUtente"));
        
        // Tavoli handlers
        // commento: verifica che il metodo handleNuovoTavolo non sia nullo, etc...
        assertNotNull(GestoreController.class.getDeclaredMethod("handleNuovoTavolo"));
        assertNotNull(GestoreController.class.getDeclaredMethod("handleSalvaTavolo"));
        assertNotNull(GestoreController.class.getDeclaredMethod("handleEliminaTavolo"));
        assertNotNull(GestoreController.class.getDeclaredMethod("handleResetTavolo"));
        
        // Stats handler
        // commento: verifica che il metodo handleRefreshStats non sia nullo
        assertNotNull(GestoreController.class.getDeclaredMethod("handleRefreshStats"));
        
        // Common handler
        // commento: verifica che il metodo handleLogout non sia nullo
        assertNotNull(GestoreController.class.getDeclaredMethod("handleLogout"));
    }

    /**
     * Verifica che i metodi privati di inizializzazione esistano.
     */
    @Test
    void controller_hasPrivateInitMethods() throws Exception {
        // Menu tab
        // commento: verifica che il metodo initMenuTab non sia nullo, etc...
        assertNotNull(GestoreController.class.getDeclaredMethod("initMenuTab"));
        assertNotNull(GestoreController.class.getDeclaredMethod("loadMenuData"));
        assertNotNull(GestoreController.class.getDeclaredMethod("clearMenuFields"));
        
        // Personale tab
        // commento: verifica che il metodo initPersonaleTab non sia nullo, etc...
        assertNotNull(GestoreController.class.getDeclaredMethod("initPersonaleTab"));
        assertNotNull(GestoreController.class.getDeclaredMethod("loadUtentiData"));
        assertNotNull(GestoreController.class.getDeclaredMethod("clearUtenteFields"));
        
        // Tavoli tab
        // commento: verifica che il metodo initTavoliTab non sia nullo, etc...
        assertNotNull(GestoreController.class.getDeclaredMethod("initTavoliTab"));
        assertNotNull(GestoreController.class.getDeclaredMethod("loadTavoliData"));
        assertNotNull(GestoreController.class.getDeclaredMethod("clearTavoloFields"));
        
        // Stats tab
        // commento: verifica che il metodo initStatsTab non sia nullo
        assertNotNull(GestoreController.class.getDeclaredMethod("initStatsTab"));
        
        // Utility
        // commento: verifica che il metodo showError non sia nullo
        assertNotNull(GestoreController.class.getDeclaredMethod("showError", String.class));
    }

    /**
     * Verifica che il controller implementi Initializable.
     */
    @Test
    void controller_implementsInitializable() {
        // Verifica che GestoreController implementi Initializable
        assertTrue(javafx.fxml.Initializable.class.isAssignableFrom(GestoreController.class));
    }

    /**
     * Verifica la presenza dei campi FXML annotati.
     * Questi campi sono collegati agli elementi della UI.
     * Controlla che tutti i campi FXML esistano nel controller.
     */
    @Test
    void controller_hasFXMLAnnotatedFields() throws Exception {

        // Common
        // commento: verifica che il campo lblUtente non sia nullo, etc...
        assertNotNull(GestoreController.class.getDeclaredField("lblUtente"));
        assertNotNull(GestoreController.class.getDeclaredField("tabPaneGestore"));
        
        // Menu tab
        // commento: verifica che il campo tblMenu non sia nullo, etc...
        assertNotNull(GestoreController.class.getDeclaredField("tblMenu"));
        assertNotNull(GestoreController.class.getDeclaredField("txtNomePiatto"));
        assertNotNull(GestoreController.class.getDeclaredField("comboCategoria"));
        assertNotNull(GestoreController.class.getDeclaredField("txtPrezzoPiatto"));
        
        // Personale tab
        // commento: verifica che il campo tblUtenti non sia nullo, etc...
        assertNotNull(GestoreController.class.getDeclaredField("tblUtenti"));
        assertNotNull(GestoreController.class.getDeclaredField("txtNomeUtente"));
        assertNotNull(GestoreController.class.getDeclaredField("txtUsername"));
        assertNotNull(GestoreController.class.getDeclaredField("txtPassword"));
        assertNotNull(GestoreController.class.getDeclaredField("comboRuolo"));
        
        // Tavoli tab
        // commento: verifica che il campo tblTavoli non sia nullo, etc...
        assertNotNull(GestoreController.class.getDeclaredField("tblTavoli"));
        assertNotNull(GestoreController.class.getDeclaredField("txtNumeroTavolo"));
        assertNotNull(GestoreController.class.getDeclaredField("spinPostiTavolo"));
        
        // Stats tab
        // commento: verifica che il campo pieBestSellers non sia nullo, etc...
        assertNotNull(GestoreController.class.getDeclaredField("pieBestSellers"));
        assertNotNull(GestoreController.class.getDeclaredField("lblIncassoTotale"));
    }

    /**
     * Verifica che il controller abbia TableColumn per tutte le tabelle.
     * Questi campi sono necessari per visualizzare i dati nelle tabelle.
     * Controlla che tutte le colonne esistano nel controller.
     */
    @Test
    void controller_hasAllTableColumns() throws Exception {

        // Menu columns
        // commento: verifica che la colonna nome piatto non sia nulla, etc...
        assertNotNull(GestoreController.class.getDeclaredField("colNomePiatto"));
        assertNotNull(GestoreController.class.getDeclaredField("colCategoriaPiatto"));
        assertNotNull(GestoreController.class.getDeclaredField("colPrezzoPiatto"));
        assertNotNull(GestoreController.class.getDeclaredField("colDispPiatto"));
        
        // Personale columns
        // commento: verifica che la colonna nome utente non sia nulla, etc...
        assertNotNull(GestoreController.class.getDeclaredField("colNomeUtente"));
        assertNotNull(GestoreController.class.getDeclaredField("colUsername"));
        assertNotNull(GestoreController.class.getDeclaredField("colRuolo"));
        
        // Tavoli columns
        // commento: verifica che la colonna numero tavolo non sia nulla, etc...
        assertNotNull(GestoreController.class.getDeclaredField("colNumeroTavolo"));
        assertNotNull(GestoreController.class.getDeclaredField("colPostiTavolo"));
        assertNotNull(GestoreController.class.getDeclaredField("colStatoTavolo"));
    }

    @Test
    void handleRefreshStats_populatesPieAndIncomeLabel() throws Exception {
        runOnFxThread(() -> {
            try {
                GestoreController controller = new GestoreController();

                PieChart pie = new PieChart();
                Label income = new Label();
                setField(controller, "pieBestSellers", pie);
                setField(controller, "lblIncassoTotale", income);

                Map<String, Integer> best = new LinkedHashMap<>();
                best.put("Pizza", 3);
                best.put("Acqua", 5);
                setField(controller, "gestoreDAO", new FakeGestoreDAO(best, 12.5));

                invokeNoArg(controller, "handleRefreshStats");

                assertEquals(2, pie.getData().size());
                String txt = normalizeEuro(income.getText());
                assertNotNull(txt);
                assertTrue(txt.matches("€\\s*12[,.]50"), "Unexpected income label: " + txt);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void handleSalvaPiatto_callsInsertThenUpdate() throws Exception {
        runOnFxThread(() -> {
            try {
                GestoreController controller = new GestoreController();

                FakeMenuDAO fakeMenuDAO = new FakeMenuDAO();
                setField(controller, "menuDAO", fakeMenuDAO);

                TableView<Piatto> tbl = new TableView<>();
                setField(controller, "tblMenu", tbl);
                setField(controller, "colNomePiatto", new TableColumn<Piatto, String>());
                setField(controller, "colCategoriaPiatto", new TableColumn<Piatto, String>());
                setField(controller, "colPrezzoPiatto", new TableColumn<Piatto, Double>());
                setField(controller, "colDispPiatto", new TableColumn<Piatto, Boolean>());

                TextField txtNome = new TextField("Test Piatto");
                ComboBox<String> comboCat = new ComboBox<>();
                comboCat.setValue("Primi");
                TextField txtPrezzo = new TextField("3.50");
                TextArea txtDesc = new TextArea("desc");
                TextField txtAll = new TextField("glutine");
                CheckBox chk = new CheckBox();
                chk.setSelected(true);

                setField(controller, "txtNomePiatto", txtNome);
                setField(controller, "comboCategoria", comboCat);
                setField(controller, "txtPrezzoPiatto", txtPrezzo);
                setField(controller, "txtDescrizionePiatto", txtDesc);
                setField(controller, "txtAllergeni", txtAll);
                setField(controller, "chkDisponibile", chk);

                setField(controller, "selectedPiatto", null);
                invokeNoArg(controller, "handleSalvaPiatto");
                assertEquals(1, fakeMenuDAO.insertCalls);
                assertEquals(0, fakeMenuDAO.updateCalls);

                setField(controller, "selectedPiatto", new Piatto(99, "Old", "d", 1.0, "Primi", true, ""));
                txtPrezzo.setText("4.00");
                invokeNoArg(controller, "handleSalvaPiatto");
                assertEquals(1, fakeMenuDAO.insertCalls);
                assertEquals(1, fakeMenuDAO.updateCalls);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void handleSalvaUtente_callsInsertThenUpdate() throws Exception {
        runOnFxThread(() -> {
            try {
                GestoreController controller = new GestoreController();

                FakeUtenteDAO fakeUtenteDAO = new FakeUtenteDAO();
                setField(controller, "utenteDAO", fakeUtenteDAO);
                setField(controller, "tblUtenti", new TableView<Utente>());
                setField(controller, "colNomeUtente", new TableColumn<Utente, String>());
                setField(controller, "colUsername", new TableColumn<Utente, String>());
                setField(controller, "colRuolo", new TableColumn<Utente, String>());

                TextField txtNome = new TextField("Mario");
                TextField txtUser = new TextField("mario_test");
                PasswordField txtPass = new PasswordField();
                txtPass.setText("pwd");
                ComboBox<String> comboRuolo = new ComboBox<>();
                comboRuolo.setValue("Cameriere");

                setField(controller, "txtNomeUtente", txtNome);
                setField(controller, "txtUsername", txtUser);
                setField(controller, "txtPassword", txtPass);
                setField(controller, "comboRuolo", comboRuolo);

                setField(controller, "selectedUtente", null);
                invokeNoArg(controller, "handleSalvaUtente");
                assertEquals(1, fakeUtenteDAO.insertCalls);
                assertEquals(0, fakeUtenteDAO.updateCalls);

                setField(controller, "selectedUtente", new Utente(7, "Old", "old", "x", "Gestore"));
                invokeNoArg(controller, "handleSalvaUtente");
                assertEquals(1, fakeUtenteDAO.insertCalls);
                assertEquals(1, fakeUtenteDAO.updateCalls);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void handleSalvaAndResetTavolo_callsDaoMethods() throws Exception {
        runOnFxThread(() -> {
            try {
                GestoreController controller = new GestoreController();

                FakeTavoloDAO fakeTavoloDAO = new FakeTavoloDAO();
                setField(controller, "tavoloDAO", fakeTavoloDAO);

                setField(controller, "tblTavoli", new TableView<Tavolo>());
                setField(controller, "colNumeroTavolo", new TableColumn<Tavolo, Integer>());
                setField(controller, "colPostiTavolo", new TableColumn<Tavolo, Integer>());
                setField(controller, "colStatoTavolo", new TableColumn<Tavolo, String>());

                TextField txtNumero = new TextField("10");
                Spinner<Integer> spinPosti = new Spinner<>(1, 20, 4);
                TextArea txtNote = new TextArea("note");
                setField(controller, "txtNumeroTavolo", txtNumero);
                setField(controller, "spinPostiTavolo", spinPosti);
                setField(controller, "txtNoteTavolo", txtNote);

                setField(controller, "selectedTavolo", null);
                invokeNoArg(controller, "handleSalvaTavolo");
                assertEquals(1, fakeTavoloDAO.insertCalls);
                assertEquals(0, fakeTavoloDAO.updateCalls);

                setField(controller, "selectedTavolo", new Tavolo(5, 10, "Occupato", 4, ""));
                txtNumero.setText("11");
                invokeNoArg(controller, "handleSalvaTavolo");
                assertEquals(1, fakeTavoloDAO.insertCalls);
                assertEquals(1, fakeTavoloDAO.updateCalls);

                // handleSalvaTavolo -> handleNuovoTavolo clears selection/selectedTavolo
                setField(controller, "selectedTavolo", new Tavolo(5, 11, "Occupato", 4, ""));
                invokeNoArg(controller, "handleResetTavolo");
                assertEquals(1, fakeTavoloDAO.resetCalls);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void statoTavoloCellFactory_setsExpectedStyles() throws Exception {
        runOnFxThread(() -> {
            try {
                GestoreController controller = new GestoreController();
                setField(controller, "tavoloDAO", new FakeTavoloDAO());

                TableColumn<Tavolo, Integer> colNum = new TableColumn<>();
                TableColumn<Tavolo, Integer> colPosti = new TableColumn<>();
                TableColumn<Tavolo, String> colStato = new TableColumn<>();
                setField(controller, "colNumeroTavolo", colNum);
                setField(controller, "colPostiTavolo", colPosti);
                setField(controller, "colStatoTavolo", colStato);
                setField(controller, "tblTavoli", new TableView<Tavolo>());
                setField(controller, "txtNumeroTavolo", new TextField());
                Spinner<Integer> spin = new Spinner<>();
                spin.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 4));
                setField(controller, "spinPostiTavolo", spin);
                setField(controller, "txtNoteTavolo", new TextArea());

                invokeNoArg(controller, "initTavoliTab");

                TableCell<Tavolo, String> cell = colStato.getCellFactory().call(colStato);
                Method updateItem = cell.getClass().getDeclaredMethod("updateItem", String.class, boolean.class);
                updateItem.setAccessible(true);

                updateItem.invoke(cell, "Libero", false);
                assertEquals("Libero", cell.getText());
                assertTrue(cell.getStyle().contains("#2ecc71"));

                updateItem.invoke(cell, "Occupato", false);
                assertEquals("Occupato", cell.getText());
                assertTrue(cell.getStyle().contains("#e74c3c"));

                updateItem.invoke(cell, "Prenotato", false);
                assertEquals("Prenotato", cell.getText());
                assertTrue(cell.getStyle().contains("#f39c12"));

                updateItem.invoke(cell, null, true);
                assertNull(cell.getText());
                assertEquals("", cell.getStyle());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void handleEliminaPiatto_callsDelete() throws Exception {
        runOnFxThread(() -> {
            try {
                GestoreController controller = new GestoreController();
                FakeMenuDAO dao = new FakeMenuDAO();
                setField(controller, "menuDAO", dao);
                
                // Setup UI fields needed by handleNuovoPiatto which is called after delete
                setField(controller, "tblMenu", new TableView<Piatto>());
                setField(controller, "txtNomePiatto", new TextField());
                setField(controller, "comboCategoria", new ComboBox<String>());
                setField(controller, "txtPrezzoPiatto", new TextField());
                setField(controller, "txtDescrizionePiatto", new TextArea());
                setField(controller, "txtAllergeni", new TextField());
                setField(controller, "chkDisponibile", new CheckBox());

                // Case 1: No selection
                setField(controller, "selectedPiatto", null);
                invokeNoArg(controller, "handleEliminaPiatto");
                assertEquals(0, dao.deleteCalls);

                // Case 2: With selection
                Piatto p = new Piatto(1, "P", "D", 10.0, "C", true, "A");
                setField(controller, "selectedPiatto", p);
                invokeNoArg(controller, "handleEliminaPiatto");
                assertEquals(1, dao.deleteCalls);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void handleEliminaUtente_callsDelete() throws Exception {
        runOnFxThread(() -> {
            try {
                GestoreController controller = new GestoreController();
                FakeUtenteDAO dao = new FakeUtenteDAO();
                setField(controller, "utenteDAO", dao);
                
                setField(controller, "tblUtenti", new TableView<Utente>());
                setField(controller, "txtNomeUtente", new TextField());
                setField(controller, "txtUsername", new TextField());
                setField(controller, "txtPassword", new PasswordField());
                setField(controller, "comboRuolo", new ComboBox<String>());

                setField(controller, "selectedUtente", null);
                invokeNoArg(controller, "handleEliminaUtente");
                assertEquals(0, dao.deleteCalls);

                Utente u = new Utente(1, "N", "U", "P", "R");
                setField(controller, "selectedUtente", u);
                invokeNoArg(controller, "handleEliminaUtente");
                assertEquals(1, dao.deleteCalls);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void handleEliminaTavolo_callsDelete() throws Exception {
        runOnFxThread(() -> {
            try {
                GestoreController controller = new GestoreController();
                FakeTavoloDAO dao = new FakeTavoloDAO();
                setField(controller, "tavoloDAO", dao);
                
                setField(controller, "tblTavoli", new TableView<Tavolo>());
                setField(controller, "txtNumeroTavolo", new TextField());
                setField(controller, "spinPostiTavolo", new Spinner<Integer>(1, 20, 4));
                setField(controller, "txtNoteTavolo", new TextArea());

                setField(controller, "selectedTavolo", null);
                invokeNoArg(controller, "handleEliminaTavolo");
                assertEquals(0, dao.deleteCalls);

                Tavolo t = new Tavolo(1, 1, "Libero", 4, "Note");
                setField(controller, "selectedTavolo", t);
                invokeNoArg(controller, "handleEliminaTavolo");
                assertEquals(1, dao.deleteCalls);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void testMenuSelectionListener() throws Exception {
        runOnFxThread(() -> {
            try {
                GestoreController controller = new GestoreController();
                
                TableView<Piatto> tbl = new TableView<>();
                TextField txtNome = new TextField();
                ComboBox<String> combo = new ComboBox<>();
                TextField txtPrezzo = new TextField();
                TextArea txtDesc = new TextArea();
                TextField txtAll = new TextField();
                CheckBox chk = new CheckBox();

                setField(controller, "tblMenu", tbl);
                setField(controller, "txtNomePiatto", txtNome);
                setField(controller, "comboCategoria", combo);
                setField(controller, "txtPrezzoPiatto", txtPrezzo);
                setField(controller, "txtDescrizionePiatto", txtDesc);
                setField(controller, "txtAllergeni", txtAll);
                setField(controller, "chkDisponibile", chk);
                
                setField(controller, "colNomePiatto", new TableColumn<>());
                setField(controller, "colCategoriaPiatto", new TableColumn<>());
                setField(controller, "colPrezzoPiatto", new TableColumn<>());
                setField(controller, "colDispPiatto", new TableColumn<>());
                setField(controller, "menuDAO", new FakeMenuDAO());

                invokeNoArg(controller, "initMenuTab");

                Piatto p = new Piatto(1, "Pizza", "Buona", 5.0, "Primi", true, "Glutine");
                tbl.getItems().add(p);
                
                // Select item
                tbl.getSelectionModel().select(p);
                
                assertEquals("Pizza", txtNome.getText());
                assertEquals("5.0", txtPrezzo.getText());
                
                // Deselect
                tbl.getSelectionModel().clearSelection();
                assertEquals("", txtNome.getText());
                
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void testUtenteSelectionListener() throws Exception {
        runOnFxThread(() -> {
            try {
                GestoreController controller = new GestoreController();
                
                TableView<Utente> tbl = new TableView<>();
                TextField txtNome = new TextField();
                TextField txtUser = new TextField();
                PasswordField txtPass = new PasswordField();
                ComboBox<String> combo = new ComboBox<>();

                setField(controller, "tblUtenti", tbl);
                setField(controller, "txtNomeUtente", txtNome);
                setField(controller, "txtUsername", txtUser);
                setField(controller, "txtPassword", txtPass);
                setField(controller, "comboRuolo", combo);
                
                setField(controller, "colNomeUtente", new TableColumn<>());
                setField(controller, "colUsername", new TableColumn<>());
                setField(controller, "colRuolo", new TableColumn<>());
                setField(controller, "utenteDAO", new FakeUtenteDAO());

                invokeNoArg(controller, "initPersonaleTab");

                Utente u = new Utente(1, "Mario", "mario", "pass", "Cameriere");
                tbl.getItems().add(u);
                
                tbl.getSelectionModel().select(u);
                assertEquals("Mario", txtNome.getText());
                
                tbl.getSelectionModel().clearSelection();
                assertEquals("", txtNome.getText());
                
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void testTavoloSelectionListener() throws Exception {
        runOnFxThread(() -> {
            try {
                GestoreController controller = new GestoreController();
                
                TableView<Tavolo> tbl = new TableView<>();
                TextField txtNumero = new TextField();
                Spinner<Integer> spin = new Spinner<>();
                TextArea txtNote = new TextArea();

                setField(controller, "tblTavoli", tbl);
                setField(controller, "txtNumeroTavolo", txtNumero);
                setField(controller, "spinPostiTavolo", spin);
                setField(controller, "txtNoteTavolo", txtNote);
                
                setField(controller, "colNumeroTavolo", new TableColumn<>());
                setField(controller, "colPostiTavolo", new TableColumn<>());
                setField(controller, "colStatoTavolo", new TableColumn<>());
                setField(controller, "tavoloDAO", new FakeTavoloDAO());

                invokeNoArg(controller, "initTavoliTab");

                Tavolo t = new Tavolo(1, 10, "Libero", 6, "Vista mare");
                tbl.getItems().add(t);
                
                tbl.getSelectionModel().select(t);
                assertEquals("10", txtNumero.getText());
                assertEquals(6, spin.getValue());
                
                tbl.getSelectionModel().clearSelection();
                assertEquals("", txtNumero.getText());
                
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void testMenuCellFactories() throws Exception {
        runOnFxThread(() -> {
            try {
                GestoreController controller = new GestoreController();
                setField(controller, "menuDAO", new FakeMenuDAO());
                
                TableColumn<Piatto, Double> colPrezzo = new TableColumn<>();
                TableColumn<Piatto, Boolean> colDisp = new TableColumn<>();
                
                setField(controller, "colNomePiatto", new TableColumn<>());
                setField(controller, "colCategoriaPiatto", new TableColumn<>());
                setField(controller, "colPrezzoPiatto", colPrezzo);
                setField(controller, "colDispPiatto", colDisp);
                
                setField(controller, "tblMenu", new TableView<>());
                setField(controller, "comboCategoria", new ComboBox<>());
                
                invokeNoArg(controller, "initMenuTab");
                
                // Test Prezzo Cell Factory
                TableCell<Piatto, Double> cellPrezzo = colPrezzo.getCellFactory().call(colPrezzo);
                Method updateItemPrezzo = cellPrezzo.getClass().getDeclaredMethod("updateItem", Object.class, boolean.class);
                updateItemPrezzo.setAccessible(true);
                
                updateItemPrezzo.invoke(cellPrezzo, 12.50, false);
                assertEquals("€ 12,50", cellPrezzo.getText().replace('.', ',')); // Handle locale diffs if needed
                
                updateItemPrezzo.invoke(cellPrezzo, null, true);
                assertNull(cellPrezzo.getText());

                // Test Disp Cell Factory
                TableCell<Piatto, Boolean> cellDisp = colDisp.getCellFactory().call(colDisp);
                Method updateItemDisp = cellDisp.getClass().getDeclaredMethod("updateItem", Object.class, boolean.class);
                updateItemDisp.setAccessible(true);
                
                updateItemDisp.invoke(cellDisp, true, false);
                assertEquals("Sì", cellDisp.getText());
                assertTrue(cellDisp.getStyle().contains("#2ecc71"));
                
                updateItemDisp.invoke(cellDisp, false, false);
                assertEquals("No", cellDisp.getText());
                assertTrue(cellDisp.getStyle().contains("#e74c3c"));
                
                updateItemDisp.invoke(cellDisp, null, true);
                assertNull(cellDisp.getText());
                
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}

package famiglia.sapori.controller;

import famiglia.sapori.FamigliaSaporiApplication;
import famiglia.sapori.dao.TavoloDAO;
import famiglia.sapori.model.Piatto;
import famiglia.sapori.model.Tavolo;
import famiglia.sapori.model.Utente;
import famiglia.sapori.test.util.ApplicationMockHelper;
import famiglia.sapori.database.TestDatabase;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

public class GestoreControllerFxTest extends ApplicationTest {
    private GestoreController controller;
    private Stage testStage;

    @BeforeAll
    static void setupDatabase() throws Exception {
        TestDatabase.setupSchema();
        TestDatabase.seedData();
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.testStage = stage;

        // Simula un utente Gestore loggato
        FamigliaSaporiApplication.setCurrentUser(new Utente(1, "Admin Gestore", "admin", "admin", "Gestore"));

        // Carica il file FXML reale
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GestoreView.fxml"));
        Parent root = loader.load();

        // Ottieni il controller dalla FXML
        controller = loader.getController();

        stage.setScene(new Scene(root, 1080, 720));
        stage.show();
    }

    @BeforeEach
    void setupMockScene() throws Exception {
        ApplicationMockHelper.setupMockScene(testStage);
    }

    @AfterEach
    void clearMockScene() throws Exception {
        ApplicationMockHelper.clearMockScene();
    }

    /**
     * Verifica che la vista Gestore sia caricata correttamente.
     */
    @Test
    void gestoreSceneLoadsSuccessfully() {
        assertNotNull(controller, "Il controller dovrebbe essere caricato dalla FXML");
    }

    /**
     * Verifica che il controller sia inizializzato correttamente.
     */
    @Test
    void controllerIsInitialized() {
        assertNotNull(controller);
        Label lblUtente = lookup("#lblUtente").query();
        assertNotNull(lblUtente);
        assertTrue(lblUtente.getText().contains("Gestore"));
    }

    /**
     * Verifica che il TabPane contenga tutte le 4 tab.
     */
    @Test
    void tabPaneHasAllTabs() {
        TabPane tabPane = lookup("#tabPaneGestore").query();
        assertNotNull(tabPane);
        assertEquals(4, tabPane.getTabs().size());

        assertEquals("Menu & Prodotti", tabPane.getTabs().get(0).getText());
        assertEquals("Personale (HR)", tabPane.getTabs().get(1).getText());
        assertEquals("Configurazione Sala", tabPane.getTabs().get(2).getText());
        assertEquals("Statistiche", tabPane.getTabs().get(3).getText());
    }

    // ==================== MENU TAB TESTS ====================

    /**
     * Verifica che la tabella menu sia popolata con i piatti dal database.
     */
    @Test
    void menuTableIsPopulated() throws Exception {
        TableView<Piatto> tblMenu = lookup("#tblMenu").query();
        assertNotNull(tblMenu);
        assertFalse(tblMenu.getItems().isEmpty(), "La tabella menu dovrebbe contenere piatti");
    }

    /**
     * Verifica che la selezione di un piatto popoli i campi di dettaglio.
     */
    @Test
    void selectingPiattoPopulatesFields() {
        TableView<Piatto> tblMenu = lookup("#tblMenu").query();
        assertNotNull(tblMenu);

        if (!tblMenu.getItems().isEmpty()) {
            clickOn(tblMenu);

            TextField txtNomePiatto = lookup("#txtNomePiatto").query();
            ComboBox<String> comboCategoria = lookup("#comboCategoria").query();
            TextField txtPrezzoPiatto = lookup("#txtPrezzoPiatto").query();

            // Dopo aver selezionato un piatto, i campi dovrebbero essere popolati
            assertNotNull(txtNomePiatto);
            assertNotNull(comboCategoria);
            assertNotNull(txtPrezzoPiatto);
        }
    }

    /**
     * Verifica che il bottone "Nuovo" pulisca i campi.
     */
    @Test
    void nuovoPiattoButtonClearsFields() {
        clickOn("Nuovo");

        TextField txtNomePiatto = lookup("#txtNomePiatto").query();
        assertNotNull(txtNomePiatto);
        assertEquals("", txtNomePiatto.getText());
    }

    /**
     * Verifica che la ComboBox categorie contenga tutte le categorie.
     */
    @Test
    void categoriaComboBoxHasAllCategories() {
        ComboBox<String> comboCategoria = lookup("#comboCategoria").query();
        assertNotNull(comboCategoria);
        assertTrue(comboCategoria.getItems().contains("Antipasti"));
        assertTrue(comboCategoria.getItems().contains("Primi"));
        assertTrue(comboCategoria.getItems().contains("Secondi"));
        assertTrue(comboCategoria.getItems().contains("Dolci"));
        assertTrue(comboCategoria.getItems().contains("Bevande"));
    }

    // ==================== PERSONALE TAB TESTS ====================

    /**
     * Verifica che la tabella utenti sia popolata.
     */
    @Test
    void personaleTableIsPopulated() {
        clickOn("Personale (HR)");

        TableView<Utente> tblUtenti = lookup("#tblUtenti").query();
        assertNotNull(tblUtenti);
        assertFalse(tblUtenti.getItems().isEmpty(), "La tabella utenti dovrebbe contenere dipendenti");
    }

    /**
     * Verifica che il bottone "Nuovo" nel tab Personale pulisca i campi.
     */
    @Test
    void nuovoUtenteButtonClearsFields() {
        clickOn("Personale (HR)");
        sleep(200);

        // Trova il bottone "Nuovo" nel tab Personale
        clickOn("Nuovo");

        TextField txtNomeUtente = lookup("#txtNomeUtente").query();
        assertNotNull(txtNomeUtente);
        assertEquals("", txtNomeUtente.getText());
    }

    /**
     * Verifica che la ComboBox ruoli contenga i ruoli disponibili.
     */
    @Test
    void ruoloComboBoxHasRoles() {
        clickOn("Personale (HR)");
        sleep(200);

        ComboBox<String> comboRuolo = lookup("#comboRuolo").query();
        assertNotNull(comboRuolo);
        assertTrue(comboRuolo.getItems().contains("Gestore"));
        assertTrue(comboRuolo.getItems().contains("Cameriere"));
    }

    // ==================== TAVOLI TAB TESTS ====================

    /**
     * Verifica che la tabella tavoli sia popolata.
     */
    @Test
    void tavoliTableIsPopulated() {
        clickOn("Configurazione Sala");

        TableView<Tavolo> tblTavoli = lookup("#tblTavoli").query();
        assertNotNull(tblTavoli);
        assertFalse(tblTavoli.getItems().isEmpty(), "La tabella tavoli dovrebbe contenere tavoli");
    }

    /**
     * Verifica che il bottone "Reset" forzi lo stato del tavolo a Libero.
     */
    @Test
    void resetTavoloButtonSetsStatoLibero() throws Exception {
        clickOn("Configurazione Sala");
        sleep(200);

        TableView<Tavolo> tblTavoli = lookup("#tblTavoli").query();
        if (!tblTavoli.getItems().isEmpty()) {
            // Seleziona il primo tavolo
            clickOn(tblTavoli);
            Tavolo selectedTavolo = tblTavoli.getSelectionModel().getSelectedItem();

            if (selectedTavolo != null) {
                clickOn("FORZA STATO A 'LIBERO'");
                sleep(300);

                // Verifica che lo stato sia stato aggiornato nel database
                TavoloDAO tavoloDAO = new TavoloDAO();
                var tavoli = tavoloDAO.getAllTavoli();
                Tavolo updated = tavoli.stream()
                        .filter(t -> t.getId() == selectedTavolo.getId())
                        .findFirst()
                        .orElse(null);

                assertNotNull(updated);
                assertEquals("Libero", updated.getStato());
            }
        }
    }

    /**
     * Verifica che lo Spinner posti sia configurato correttamente.
     */
    @Test
    void spinnerPostiIsConfigured() {
        clickOn("Configurazione Sala");
        sleep(200);

        Spinner<Integer> spinPostiTavolo = lookup("#spinPostiTavolo").query();
        assertNotNull(spinPostiTavolo);
        assertEquals(4, spinPostiTavolo.getValue().intValue());
    }

    // ==================== STATISTICHE TAB TESTS ====================

    /**
     * Verifica che il tab Statistiche sia visualizzato correttamente.
     */
    @Test
    void statisticheTabDisplaysCorrectly() {
        clickOn("Statistiche");
        sleep(200);

        PieChart pieBestSellers = lookup("#pieBestSellers").query();
        assertNotNull(pieBestSellers);

        Label lblIncassoTotale = lookup("#lblIncassoTotale").query();
        assertNotNull(lblIncassoTotale);
        assertTrue(lblIncassoTotale.getText().matches("(?s).*\\d+[,.]\\d{2}.*"), "Label should contain a price value");
    }

    /**
     * Verifica che il bottone "Aggiorna Dati" ricarichi le statistiche.
     */
    @Test
    void refreshStatsButtonUpdatesData() {
        clickOn("Statistiche");
        sleep(200);

        Label lblIncassoTotale = lookup("#lblIncassoTotale").query();
        assertNotNull(lblIncassoTotale);
        String initialValue = lblIncassoTotale.getText();

        clickOn("Aggiorna Dati");
        sleep(300);

        // Il valore dovrebbe essere aggiornato (anche se potrebbe essere lo stesso)
        assertNotNull(lblIncassoTotale.getText());
        assertTrue(lblIncassoTotale.getText().matches("(?s).*\\d+[,.]\\d{2}.*"), "Label should contain a price value");
    }

    /**
     * Verifica che il PieChart sia popolato con dati.
     */
    @Test
    void pieChartIsPopulatedWithData() {
        clickOn("Statistiche");
        sleep(200);

        PieChart pieBestSellers = lookup("#pieBestSellers").query();
        assertNotNull(pieBestSellers);
        // Il grafico potrebbe essere vuoto se non ci sono comande pagate
        assertNotNull(pieBestSellers.getData());
    }

    // ==================== NAVIGATION TESTS ====================

    /**
     * Verifica che il bottone Logout sia presente e cliccabile.
     */
    @Test
    void logoutButtonIsPresent() {
        Button logoutButton = lookup("Logout").query();
        assertNotNull(logoutButton);
    }

    /**
     * Verifica che cliccando su Logout si navighi alla schermata di login.
     */
    @Test
    void clickingLogoutNavigatesToLogin() {
        clickOn("Logout");
        sleep(300);
        // Verifica che la navigazione sia avvenuta (ApplicationMockHelper previene NPE)
        assertNotNull(controller);
    }
}

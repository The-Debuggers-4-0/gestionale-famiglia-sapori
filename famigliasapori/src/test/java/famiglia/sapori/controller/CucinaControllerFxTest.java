package famiglia.sapori.controller;

import famiglia.sapori.dao.ComandaDAO;
import famiglia.sapori.database.DatabaseConnection;
import famiglia.sapori.database.TestDatabase;
import famiglia.sapori.model.Comanda;
import famiglia.sapori.test.util.ApplicationMockHelper;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

public class CucinaControllerFxTest extends ApplicationTest {
    private CucinaController controller;
    private Stage testStage;
    private ComandaDAO comandaDAO;

    // Prepara lo schema del database prima di tutti i test
    @BeforeAll
    static void setupDatabase() throws Exception {
        TestDatabase.setupSchema();
    }

    // Avvia l'applicazione prima dei test
    @Override
    public void start(Stage stage) throws Exception {
        this.testStage = stage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CucinaView.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        stage.setScene(new Scene(root, 1080, 720));
        stage.show();
    }
    
    // Prepara la scena di test prima di ogni test
    @BeforeEach
    void setup() throws Exception {
        ApplicationMockHelper.setupMockScene(testStage);
        
        // Stop polling to prevent interference during tests
        if (controller != null) {
            Platform.runLater(() -> controller.stopPolling());
            WaitForAsyncUtils.waitForFxEvents();
        }

        comandaDAO = new ComandaDAO();
        // Reimposta i dati prima di ogni test per garantire l'isolamento
        TestDatabase.setupSchema(); 
        TestDatabase.seedData();
    }
    
    // Pulisce la scena di test dopo ogni test
    @AfterEach
    void tearDown() throws Exception {
        if (controller != null) {
            Platform.runLater(() -> controller.stopPolling());
            WaitForAsyncUtils.waitForFxEvents();
        }
        ApplicationMockHelper.clearMockScene();
    }

    /**
     * Verifica che la vista Cucina sia caricata correttamente dalla FXML.
     */
    @Test
    void cucinaSceneLoadsSuccessfully() {
        // Verifica che il controller non sia nullo
        assertNotNull(controller, "Il controller dovrebbe essere caricato dalla FXML");
        // Verifica che il titolo della finestra sia corretto
        verifyThat(".header-title", hasText("Cucina - Comande in Arrivo"));
    }

    /**
     * Verifica gestione lista comande vuota.
     */
    @Test
    void handlesEmptyCommandList() throws Exception {
        // 1. Pulisce tutte le comande esistenti
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement st = conn.createStatement()) {
            st.execute("DELETE FROM Comande");
        }

        // 2. Ricarica comande
        Platform.runLater(() -> controller.loadComande());
        WaitForAsyncUtils.waitForFxEvents();

        // 3. Verifica che venga mostrato il messaggio di nessuna comanda
        verifyThat("#ordersContainer", (javafx.scene.layout.FlowPane pane) -> 
            pane.getChildren().stream()
                .filter(n -> n instanceof Label)
                .map(n -> (Label) n)
                .anyMatch(l -> l.getText().contains("Nessuna comanda"))
        );
    }

    /**
     * Verifica che vengano caricate solo comande di tipo Cucina.
     */
    @Test
    void loadsOnlyCucinaTypeComande() throws Exception {
        // 1. Pulisce tutte le comande esistenti
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement st = conn.createStatement()) {
            st.execute("DELETE FROM Comande");
        }

        // 2. Inserisce comanda Bar
        Comanda bar = new Comanda(0, 1, "Caffe", 1.0, "Bar", "In Attesa", LocalDateTime.now(), "", 1);
        comandaDAO.insertComanda(bar);

        // 3. Inserisce comanda Cucina
        Comanda cucina = new Comanda(0, 1, "Pasta", 10.0, "Cucina", "In Attesa", LocalDateTime.now(), "", 1);
        comandaDAO.insertComanda(cucina);

        // 4. Ricarica
        Platform.runLater(() -> controller.loadComande());
        WaitForAsyncUtils.waitForFxEvents();

        // 5. Verifica che venga mostrata solo la comanda Cucina
        verifyThat("#ordersContainer", (javafx.scene.layout.FlowPane pane) -> {
            boolean hasPasta = pane.lookupAll(".label").stream().anyMatch(n -> ((Label)n).getText().contains("Pasta"));
            boolean hasCaffe = pane.lookupAll(".label").stream().anyMatch(n -> ((Label)n).getText().contains("Caffe"));
            return hasPasta && !hasCaffe;
        });
    }

    /**
     * Verifica gestione stati comanda: In Attesa e In Preparazione.
     */
    @Test
    void handlesMultipleComandaStates() throws Exception {
        // 1. Pulisce tutte le comande esistenti
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement st = conn.createStatement()) {
            st.execute("DELETE FROM Comande");
        }

        // 2. Inserisce comanda "In Attesa"
        Comanda attesa = new Comanda(0, 1, "Pizza", 6.0, "Cucina", "In Attesa", LocalDateTime.now(), "", 1);
        comandaDAO.insertComanda(attesa);

        // 3. Inserisce comanda "In Preparazione"
        Comanda prep = new Comanda(0, 1, "Bistecca", 15.0, "Cucina", "In Preparazione", LocalDateTime.now(), "", 1);
        comandaDAO.insertComanda(prep);

        // 4. Ricarica
        Platform.runLater(() -> controller.loadComande());
        WaitForAsyncUtils.waitForFxEvents();

        // 5. Verifica che i bottoni "Inizia" e "Pronto" siano presenti
        verifyThat("Inizia", (Button b) -> b.isVisible());
        verifyThat("Pronto", (Button b) -> b.isVisible());
    }

    /**
     * Verifica che il bottone Esci sia presente.
     */
    @Test
    void logoutButtonIsPresent() {
        verifyThat("Esci", (Button b) -> b.isVisible());
    }
}

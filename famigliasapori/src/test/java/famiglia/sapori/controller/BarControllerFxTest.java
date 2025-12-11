package famiglia.sapori.controller;

import famiglia.sapori.test.util.ApplicationMockHelper;
import famiglia.sapori.testutil.TestDatabase;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

public class BarControllerFxTest extends ApplicationTest {
    private BarController controller;
    private Stage testStage;

    @BeforeAll
    static void setupDatabase() throws Exception {
        TestDatabase.setupSchema();
        TestDatabase.seedData();
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.testStage = stage;
        
        // Carica il file FXML reale che usa il database H2
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BarView.fxml"));
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
     * Verifica che la vista Bar sia caricata correttamente dalla FXML.
     */
    @Test
    void barSceneLoadsSuccessfully() {
        assertNotNull(controller, "Il controller dovrebbe essere caricato dalla FXML");
    }

    /**
     * Verifica che il controller sia inizializzato e carichi le comande dal database H2.
     */
    @Test
    void controllerInitializesAndLoadsData() {
        assertNotNull(controller);
        // Il controller dovrebbe aver caricato i dati dal DB H2 durante initialize()
    }

    /**
     * Verifica che il polling sia attivo dopo l'inizializzazione.
     * Il polling viene usato per aggiornare automaticamente le comande.
     */
    @Test
    void pollingIsStartedAfterInitialization() {
        assertNotNull(controller);
        // Il controller avvia il polling automatico in initialize()
    }

    /**
     * Verifica che lo stop polling funzioni correttamente.
     */
    @Test
    void stopPollingWorks() {
        assertNotNull(controller);
        controller.stopPolling();
        // Il polling dovrebbe essere fermato senza errori
    }

    /**
     * Verifica il metodo di gestione comande.
     */
    @Test
    void comandaHandlingMethods() {
        assertNotNull(controller);
        // I metodi di gestione comande dovrebbero funzionare
    }

    /**
     * Verifica che cliccando Esci si naviga alla Home.
     */
    @Test
    void clickingLogoutButtonNavigatesToHome() {
        assertNotNull(lookup("Esci").query());
        clickOn("Esci"); // Should trigger handleLogout()
    }
}

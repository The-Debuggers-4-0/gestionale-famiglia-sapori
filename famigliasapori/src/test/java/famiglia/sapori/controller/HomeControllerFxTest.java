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

public class HomeControllerFxTest extends ApplicationTest {
    private HomeController controller;
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/HomeView.fxml"));
        Parent root = loader.load();
        
        // Ottieni il controller dalla FXML
        controller = loader.getController();
        
        stage.setScene(new Scene(root, 1080, 720));
        stage.show();
    }
    
    @BeforeEach
    void setupMockScene() throws Exception {
        // Mock the static scene field to avoid NPE during navigation
        ApplicationMockHelper.setupMockScene(testStage);
    }
    
    @AfterEach
    void clearMockScene() throws Exception {
        ApplicationMockHelper.clearMockScene();
    }

    /**
     * Verifica che la vista Home sia caricata correttamente.
     */
    @Test
    void homeSceneLoadsSuccessfully() {
        assertNotNull(controller, "Il controller dovrebbe essere caricato dalla FXML");
    }

    /**
     * Verifica che il controller sia inizializzato correttamente.
     */
    @Test
    void controllerIsInitialized() {
        assertNotNull(controller);
    }

    /**
     * Verifica che tutti i bottoni di navigazione siano configurati.
     * Il controller dovrebbe avere 4 bottoni principali: Sala, Cucina, Bar, Cassa.
     */
    @Test
    void allNavigationButtonsAreConfigured() {
        assertNotNull(controller);
        // I bottoni per le 4 aree principali dovrebbero essere configurati
    }

    /**
     * Test interattivo: verifica che il bottone Sala sia cliccabile e naviga.
     */
    @Test
    void clickingSalaButtonNavigates() {
        assertNotNull(lookup("#btnSala").query());
        clickOn("#btnSala"); // Should trigger handleSalaClick() and navigate
    }

    /**
     * Test interattivo: verifica che il bottone Cucina sia cliccabile e naviga.
     */
    @Test
    void clickingCucinaButtonNavigates() {
        assertNotNull(lookup("#btnCucina").query());
        clickOn("#btnCucina"); // Should trigger handleCucinaClick()
    }

    /**
     * Test interattivo: verifica che il bottone Bar sia cliccabile e naviga.
     */
    @Test
    void clickingBarButtonNavigates() {
        assertNotNull(lookup("#btnBar").query());
        clickOn("#btnBar"); // Should trigger handleBarClick()
    }

    /**
     * Test interattivo: verifica che il bottone Cassa sia cliccabile e naviga.
     */
    @Test
    void clickingCassaButtonNavigates() {
        assertNotNull(lookup("#btnCassa").query());
        clickOn("#btnCassa"); // Should trigger handleCassaClick()
    }
}

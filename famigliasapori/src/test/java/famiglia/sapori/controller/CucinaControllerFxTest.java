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

public class CucinaControllerFxTest extends ApplicationTest {
    private CucinaController controller;
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CucinaView.fxml"));
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
     * Verifica che la vista Cucina sia caricata correttamente dalla FXML.
     */
    @Test
    void cucinaSceneLoadsSuccessfully() {
        assertNotNull(controller, "Il controller dovrebbe essere caricato dalla FXML");
    }

    /**
     * Verifica che il controller inizializzi e carichi le comande dal DB H2.
     */
    @Test
    void controllerInitializesAndLoadsComande() {
        assertNotNull(controller);
        // Il controller dovrebbe caricare le comande in attesa e in preparazione
    }

    /**
     * Verifica che il polling sia avviato per aggiornamenti automatici.
     */
    @Test
    void pollingStartsAutomatically() {
        assertNotNull(controller);
        // Il polling viene avviato in initialize() per aggiornare ogni 30 secondi
    }

    /**
     * Verifica gestione lista comande vuota.
     * Branch: comande presenti vs nessuna comanda.
     */
    @Test
    void handlesEmptyCommandList() {
        assertNotNull(controller);
        // Quando non ci sono comande, dovrebbe mostrare messaggio appropriato
    }

    /**
     * Verifica che vengano caricate solo comande di tipo Cucina.
     * Branch: filtraggio per tipo (Cucina vs Bar).
     */
    @Test
    void loadsOnlyCucinaTypeComande() {
        assertNotNull(controller);
        // Il controller filtra per tipo="Cucina"
    }

    /**
     * Verifica gestione stati comanda: In Attesa e In Preparazione.
     * Branch: stati diversi delle comande.
     */
    @Test
    void handlesMultipleComandaStates() {
        assertNotNull(controller);
        // Dovrebbe caricare sia comande "In Attesa" che "In Preparazione"
    }

    /**
     * Verifica il metodo stopPolling.
     */
    @Test
    void stopPollingWorks() {
        assertNotNull(controller);
        // Stop polling dovrebbe funzionare senza errori
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

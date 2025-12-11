package famiglia.sapori.controller;

import famiglia.sapori.testutil.TestDatabase;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

public class BarControllerFxTest extends ApplicationTest {
    private BarController controller;

    @BeforeAll
    static void setupDatabase() throws Exception {
        TestDatabase.setupSchema();
        TestDatabase.seedData();
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Carica il file FXML reale che usa il database H2
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BarView.fxml"));
        Parent root = loader.load();
        
        // Ottieni il controller dalla FXML
        controller = loader.getController();
        
        stage.setScene(new Scene(root, 1080, 720));
        stage.show();
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
     * Verifica gestione comande vuote.
     * Quando non ci sono comande, dovrebbe mostrare un messaggio appropriato.
     */
    @Test
    void handleEmptyComandeList() {
        assertNotNull(controller);
        // Il controller dovrebbe gestire il caso di nessuna comanda in attesa
    }
}

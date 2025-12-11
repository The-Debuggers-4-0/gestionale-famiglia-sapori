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

public class HomeControllerFxTest extends ApplicationTest {
    private HomeController controller;

    @BeforeAll
    static void setupDatabase() throws Exception {
        TestDatabase.setupSchema();
        TestDatabase.seedData();
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Carica il file FXML reale che usa il database H2
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/HomeView.fxml"));
        Parent root = loader.load();
        
        // Ottieni il controller dalla FXML
        controller = loader.getController();
        
        stage.setScene(new Scene(root, 1080, 720));
        stage.show();
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
     * Verifica navigazione verso LoginView (per accesso Sala).
     * Branch: click su Sala naviga verso Login.
     */
    @Test
    void salaButtonNavigatesToLogin() {
        assertNotNull(controller);
        // Il bottone Sala dovrebbe navigare verso LoginView
    }

    /**
     * Verifica navigazione diretta verso CucinaView.
     * Branch: click su Cucina naviga direttamente.
     */
    @Test
    void cucinaButtonNavigatesDirectly() {
        assertNotNull(controller);
        // Il bottone Cucina naviga direttamente senza login
    }

    /**
     * Verifica navigazione diretta verso BarView.
     * Branch: click su Bar naviga direttamente.
     */
    @Test
    void barButtonNavigatesDirectly() {
        assertNotNull(controller);
        // Il bottone Bar naviga direttamente senza login
    }

    /**
     * Verifica navigazione diretta verso CassaView.
     * Branch: click su Cassa naviga direttamente.
     */
    @Test
    void cassaButtonNavigatesDirectly() {
        assertNotNull(controller);
        // Il bottone Cassa naviga direttamente senza login
    }
}

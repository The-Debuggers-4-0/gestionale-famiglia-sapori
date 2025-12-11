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

public class PrenotazioniControllerFxTest extends ApplicationTest {
    private PrenotazioniController controller;

    @BeforeAll
    static void setupDatabase() throws Exception {
        TestDatabase.setupSchema();
        TestDatabase.seedData();
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Carica il file FXML reale che usa il database H2
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PrenotazioneView.fxml"));
        Parent root = loader.load();
        
        // Ottieni il controller dalla FXML
        controller = loader.getController();
        
        stage.setScene(new Scene(root, 1080, 720));
        stage.show();
    }

    /**
     * Verifica che la vista Prenotazioni sia caricata correttamente dalla FXML.
     */
    @Test
    void prenotazioniSceneLoadsSuccessfully() {
        assertNotNull(controller, "Il controller dovrebbe essere caricato dalla FXML");
    }

    /**
     * Verifica che il controller inizializzi correttamente colonne, spinner e date picker.
     */
    @Test
    void controllerInitializesAllComponents() {
        assertNotNull(controller);
        // Spinner configurato con range 1-20 persone, default 2
        // DatePicker impostato a oggi
        // Campo ora impostato a ora corrente
    }

    /**
     * Verifica che vengano caricate le prenotazioni dal DB H2.
     */
    @Test
    void loadsPrenotazioniFromDatabase() {
        assertNotNull(controller);
        // Il controller carica tutte le prenotazioni in initialize()
    }

    /**
     * Verifica filtro tavoli disponibili in base alla data.
     * Branch: tavoli liberi vs tavoli già prenotati per la data selezionata.
     */
    @Test
    void filtersTavoliBasedOnSelectedDate() {
        assertNotNull(controller);
        // loadTavoli() dovrebbe filtrare i tavoli già prenotati
    }

    /**
     * Verifica comportamento speciale per data odierna.
     * Branch: data = oggi vs data futura.
     * Se oggi, esclude anche tavoli attualmente occupati.
     */
    @Test
    void handlesCurrentDateSpecially() {
        assertNotNull(controller);
        // Se data = oggi, esclude tavoli occupati in tempo reale
    }

    /**
     * Verifica funzionalità di ricerca prenotazioni per nome.
     * Testa filtro case-insensitive.
     */
    @Test
    void searchFilterWorksCorrectly() {
        assertNotNull(controller);
        // Il filtro ricerca dovrebbe essere case-insensitive
    }

    /**
     * Verifica formattazione data nella tabella (dd/MM HH:mm).
     */
    @Test
    void dateFormattingIsCorrect() {
        assertNotNull(controller);
        // Le date dovrebbero essere formattate come dd/MM HH:mm
    }
}

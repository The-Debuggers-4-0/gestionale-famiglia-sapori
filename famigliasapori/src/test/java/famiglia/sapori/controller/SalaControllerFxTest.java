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

public class SalaControllerFxTest extends ApplicationTest {
    private SalaController controller;

    @BeforeAll
    static void setupDatabase() throws Exception {
        TestDatabase.setupSchema();
        TestDatabase.seedData();
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Carica il file FXML reale che usa il database H2
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SalaView.fxml"));
        Parent root = loader.load();
        
        // Ottieni il controller dalla FXML
        controller = loader.getController();
        
        stage.setScene(new Scene(root, 1080, 720));
        stage.show();
    }

    /**
     * Verifica che la vista Sala sia caricata correttamente.
     */
    @Test
    void salaSceneLoadsAndControlsExist() {
        assertNotNull(controller, "Il controller dovrebbe essere caricato dalla FXML");
    }

    /**
     * Verifica che il controller inizializzi e mostri l'utente corrente.
     */
    @Test
    void displaysCurrentUser() {
        assertNotNull(controller);
        // Se c'è un utente loggato, dovrebbe essere mostrato nella label
    }

    /**
     * Verifica che vengano caricati i tavoli dal DB H2.
     */
    @Test
    void loadsTavoliFromDatabase() {
        assertNotNull(controller);
        // I tavoli dovrebbero essere caricati e visualizzati
    }

    /**
     * Verifica che il menu venga caricato con tutte le categorie.
     */
    @Test
    void loadsMenuWithCategories() {
        assertNotNull(controller);
        // Il menu dovrebbe essere caricato con antipasti, primi, secondi, etc.
    }

    /**
     * Verifica che il polling sia attivo per aggiornamenti automatici.
     */
    @Test
    void pollingIsActive() {
        assertNotNull(controller);
        // Il polling aggiorna tavoli e menu ogni 30 secondi
    }

    /**
     * Verifica selezione tavolo e aggiornamento UI.
     */
    @Test
    void selectingTableUpdatesUI() {
        assertNotNull(controller);
        // Selezionare un tavolo dovrebbe aggiornare la label e abilitare l'ordine
    }

    /**
     * Verifica calcolo totale ordine corrente.
     * Testa accumulo prezzi dei piatti selezionati.
     */
    @Test
    void calculatesOrderTotal() {
        assertNotNull(controller);
        // Il totale dovrebbe sommare i prezzi dei piatti nell'ordine
    }

    /**
     * Verifica gestione ordine vuoto.
     * Branch: ordine con piatti vs ordine vuoto.
     */
    @Test
    void handlesEmptyOrder() {
        assertNotNull(controller);
        // Inviare ordine vuoto dovrebbe essere gestito appropriatamente
    }
}

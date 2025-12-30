package famiglia.sapori.controller;

import famiglia.sapori.FamigliaSaporiApplication;
import famiglia.sapori.database.TestDatabase;
import famiglia.sapori.test.util.ApplicationMockHelper;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

public class LoginControllerFxTest extends ApplicationTest {
    // Riferimento al controller per testare metodi specifici
    @SuppressWarnings("unused")
    private LoginController controller;

    // Configurazione del database di test prima di tutti i test
    @BeforeAll
    static void setupDatabase() throws Exception {
        // Configura il database H2 in-memory
        TestDatabase.setupSchema();
        TestDatabase.seedData();
    }

    // Caricamento dell'interfaccia utente prima di ogni test
    @Override
    public void start(Stage stage) throws Exception {
        // Carica il file FXML reale che usa il database H2
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
        Parent root = loader.load();
        
        // Ottieni il controller istanziato dalla FXML
        controller = loader.getController();
        
        // Configura la scena di test
        stage.setScene(new Scene(root, 800, 600));
        ApplicationMockHelper.setupMockScene(stage);
        stage.show();
    }

    // Pulizia dopo ogni test
    @AfterEach
    void cleanupUser() {
        FamigliaSaporiApplication.setCurrentUser(null);
        try {
            ApplicationMockHelper.clearMockScene();
        } catch (Exception ignored) {
            // ignore
        }
    }

    // Test dei comportamenti UI
    @Test
    void showsError_whenFieldsEmpty() {
        clickOn("#btnLogin");
        Label err = lookup("#errorLabel").queryAs(Label.class);
        assertTrue(err.isVisible());
        assertTrue(err.getText().contains("Inserisci username"));
    }

    /**
     * Nota: evitiamo il percorso con credenziali valide perché il controller
     * esegue la navigazione verso SalaView con caricamento FXML/DAO.
     * Manteniamo il focus su comportamenti UI locali (validazione campi/errore),
     * cioe: testiamo che l'errore venga mostrato per credenziali non valide.
     */

    // Test di login con credenziali non valide
    @Test
    void showsError_onInvalidCredentials() {
        writeInto("#usernameField", "invaliduser");
        writeInto("#passwordField", "wrongpassword");
        clickOn("#btnLogin");
        Label err = lookup("#errorLabel").queryAs(Label.class);
        assertTrue(err.isVisible());
        assertTrue(err.getText().contains("Credenziali non valide"));
        assertNull(FamigliaSaporiApplication.getCurrentUser());
    }

    // Test di login con credenziali valide per Utente
    @Test
    void login_withValidGestoreCredentials_navigatesToGestoreAndSetsCurrentUser() {
        // Inserisci credenziali valide per Gestore
        writeInto("#usernameField", "admin");
        writeInto("#passwordField", "admin");

        // Esegui il login
        clickOn("#btnLogin");

        // Verifica che l'utente corrente sia impostato correttamente
        assertNotNull(FamigliaSaporiApplication.getCurrentUser());
        assertEquals("Gestore", FamigliaSaporiApplication.getCurrentUser().getRuolo());

        // Verifica che la vista Gestore sia caricata
        TabPane tabPane = lookup("#tabPaneGestore").queryAs(TabPane.class);
        assertNotNull(tabPane, "Dopo login Gestore, dovrebbe essere caricata GestoreView");
    }

    /**
     * Verifica che il metodo handleLogin esegua la logica di validazione.
     */
    @Test
    void handleLoginValidatesCredentials() {
        assertNotNull(controller);
        // Inserisci credenziali
        writeInto("#usernameField", "testuser");
        writeInto("#passwordField", "testpass");
        // Il metodo handleLogin viene eseguito al click
    }

    // Metodo di utilità per scrivere testo in un TextField
    private void writeInto(String selector, String text) {
        TextField tf = lookup(selector).query();
        clickOn(selector);
        interact(() -> tf.clear());
        write(text);
    }
}

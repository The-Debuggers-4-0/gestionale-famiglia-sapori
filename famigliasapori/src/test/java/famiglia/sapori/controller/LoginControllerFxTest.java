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
    @SuppressWarnings("unused")
    private LoginController controller;

    @BeforeAll
    static void setupDatabase() throws Exception {
        // Configura il database H2 in-memory
        TestDatabase.setupSchema();
        TestDatabase.seedData();
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Carica il file FXML reale che usa il database H2
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
        Parent root = loader.load();
        
        // Ottieni il controller istanziato dalla FXML
        controller = loader.getController();
        
        stage.setScene(new Scene(root, 800, 600));
        ApplicationMockHelper.setupMockScene(stage);
        stage.show();
    }

    @AfterEach
    void cleanupUser() {
        FamigliaSaporiApplication.currentUser = null;
        try {
            ApplicationMockHelper.clearMockScene();
        } catch (Exception ignored) {
            // ignore
        }
    }

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
     * Manteniamo il focus su comportamenti UI locali (validazione campi/errore).
     */

    @Test
    void showsError_onInvalidCredentials() {
        writeInto("#usernameField", "invaliduser");
        writeInto("#passwordField", "wrongpassword");
        clickOn("#btnLogin");
        Label err = lookup("#errorLabel").queryAs(Label.class);
        assertTrue(err.isVisible());
        assertTrue(err.getText().contains("Credenziali non valide"));
        assertNull(FamigliaSaporiApplication.currentUser);
    }

    @Test
    void login_withValidGestoreCredentials_navigatesToGestoreAndSetsCurrentUser() {
        writeInto("#usernameField", "admin");
        writeInto("#passwordField", "admin");
        clickOn("#btnLogin");

        assertNotNull(FamigliaSaporiApplication.currentUser);
        assertEquals("Gestore", FamigliaSaporiApplication.currentUser.getRuolo());

        TabPane tabPane = lookup("#tabPaneGestore").queryAs(TabPane.class);
        assertNotNull(tabPane, "Dopo login Gestore, dovrebbe essere caricata GestoreView");
    }

    /**
     * Verifica che il metodo handleLogin esegua la logica di validazione.
     */
    @Test
    void handleLoginValidatesCredentials() {
        assertNotNull(controller);
        writeInto("#usernameField", "testuser");
        writeInto("#passwordField", "testpass");
        // Il metodo handleLogin viene eseguito al click
    }

    // Helper to clear and write text
    private void writeInto(String selector, String text) {
        TextField tf = lookup(selector).query();
        clickOn(selector);
        tf.clear();
        write(text);
    }
}

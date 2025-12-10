package famiglia.sapori.controller;

import famiglia.sapori.FamigliaSaporiApplication;
import famiglia.sapori.model.Utente;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

public class LoginControllerFxTest extends ApplicationTest {

    private LoginController controller;

    @Override
    public void start(Stage stage) throws Exception {
        // Build a minimal scene mimicking LoginView controls
        TextField usernameField = new TextField();
        usernameField.setId("usernameField");
        PasswordField passwordField = new PasswordField();
        passwordField.setId("passwordField");
        Button btnLogin = new Button("Login");
        btnLogin.setId("btnLogin");
        Label errorLabel = new Label();
        errorLabel.setId("errorLabel");
        errorLabel.setVisible(false);

        VBox root = new VBox(8, usernameField, passwordField, btnLogin, errorLabel);
        stage.setScene(new Scene(root, 400, 200));
        stage.show();

        // Instantiate controller and inject @FXML fields via reflection
        controller = new LoginController();
        controller.initialize(null, null);

        injectField(controller, "usernameField", usernameField);
        injectField(controller, "passwordField", passwordField);
        injectField(controller, "btnLogin", btnLogin);
        injectField(controller, "errorLabel", errorLabel);

        // Replace DAO with a stub to avoid real DB
        famiglia.sapori.dao.UtenteDAO stubDao = new famiglia.sapori.dao.UtenteDAO() {
            @Override
            public Utente login(String username, String password) {
                if ("mario".equals(username) && "pwd123".equals(password)) {
                    return new Utente(1, "Mario Rossi", "mario", "pwd123", "Cameriere");
                }
                return null;
            }
        };
        injectField(controller, "utenteDAO", stubDao);

        // Wire button to controller's handleLogin method
        Method handleLogin = LoginController.class.getDeclaredMethod("handleLogin");
        handleLogin.setAccessible(true);
        btnLogin.setOnAction(e -> {
            try {
                handleLogin.invoke(controller);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    @AfterEach
    void cleanupUser() {
        FamigliaSaporiApplication.currentUser = null;
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
        writeInto("#usernameField", "mario");
        writeInto("#passwordField", "wrong");
        clickOn("#btnLogin");
        Label err = lookup("#errorLabel").queryAs(Label.class);
        assertTrue(err.isVisible());
        assertTrue(err.getText().contains("Credenziali non valide"));
        assertNull(FamigliaSaporiApplication.currentUser);
    }

    // Helper to inject private @FXML fields
    private static void injectField(Object target, String name, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(target, value);
    }

    // Helper to clear and write text
    private void writeInto(String selector, String text) {
        TextField tf = lookup(selector).query();
        clickOn(selector);
        tf.clear();
        write(text);
    }
}

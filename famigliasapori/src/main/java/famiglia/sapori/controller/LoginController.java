package famiglia.sapori.controller;
 
import famiglia.sapori.FamigliaSaporiApplication;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
 
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
 
/**
 * Controller per la LoginView
 */
public class LoginController implements Initializable {
 
    @FXML
    private TextField usernameField;
 
    @FXML
    private PasswordField passwordField;
 
    @FXML
    private Button btnLogin;
 
    @FXML
    private Button btnBack;
 
    @FXML
    private Label errorLabel;
 
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configurazione iniziale
    }
 
    @FXML
    private void handleLogin() {
        // manca query al database per verifica credenziali
        try {
            FamigliaSaporiApplication.setRoot("SalaView");
        } catch (IOException e) {
            System.err.println("Errore nel caricamento della SalaView: " + e.getMessage());
        }
    }
 
    @FXML
    private void handleBack() {
        try {
            FamigliaSaporiApplication.setRoot("HomeView");
        } catch (IOException e) {
            System.err.println("Errore nel ritorno alla HomeView: " + e.getMessage());
        }
    }
}
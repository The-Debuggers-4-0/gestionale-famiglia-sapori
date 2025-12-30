package famiglia.sapori.controller;
 
import famiglia.sapori.FamigliaSaporiApplication;
import famiglia.sapori.dao.UtenteDAO;
import famiglia.sapori.model.Utente;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
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

    private UtenteDAO utenteDAO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        utenteDAO = new UtenteDAO();
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Inserisci username e password");
            return;
        }

        try {
           Utente utente = utenteDAO.login(username, password);
            if (utente != null) {
                FamigliaSaporiApplication.currentUser = utente;
                if ("Gestore".equalsIgnoreCase(utente.getRuolo())) {
                    FamigliaSaporiApplication.setRoot("GestoreView");
                } else {
                    FamigliaSaporiApplication.setRoot("SalaView");
                }
            } else {
                showError("Credenziali non valide");
            }
        } catch (SQLException | IOException e) {
            System.err.println("Errore durante il login: " + e.getMessage());
            showError("Errore: " + e.getMessage());
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
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
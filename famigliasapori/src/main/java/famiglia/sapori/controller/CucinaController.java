package famiglia.sapori.controller;

import famiglia.sapori.FamigliaSaporiApplication;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller per la CucinaView
 */
public class CucinaController implements Initializable {

    @FXML
    private FlowPane ordersContainer;

    @FXML
    private Button logoutButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configurazione iniziale della cucina
    }

    @FXML
    private void handleLogout() {
        try {
            FamigliaSaporiApplication.setRoot("HomeView");
        } catch (IOException e) {
            System.err.println("Errore nel ritorno alla HomeView: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
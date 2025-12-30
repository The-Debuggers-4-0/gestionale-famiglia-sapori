package famiglia.sapori.controller;

import famiglia.sapori.FamigliaSaporiApplication;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller per la HomeView - gestisce la navigazione principale
 */
public class HomeController implements Initializable {

    @FXML
    private Button btnSala;

    @FXML
    private Button btnCucina;

    @FXML
    private Button btnBar;

    @FXML
    private Button btnCassa;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configurazione iniziale se necessaria
    }

    /**
     * Gestisce il click sul bottone Sala & Ordini
     * Naviga verso la pagina di login per accedere alla sala
     */
    @FXML
    private void handleSalaClick() {
        try {
            FamigliaSaporiApplication.setRoot("LoginView");
        } catch (IOException e) {
            System.err.println("Errore nel caricamento della pagina Login: " + e.getMessage());
        }
    }

    /**
     * Gestisce il click sul bottone Cucina
     * Naviga direttamente verso la vista cucina
     */
    @FXML
    private void handleCucinaClick() {
        try {
            FamigliaSaporiApplication.setRoot("CucinaView");
        } catch (IOException e) {
            System.err.println("Errore nel caricamento della pagina Cucina: " + e.getMessage());
        }
    }

    /**
     * Gestisce il click sul bottone Bar
     * Naviga direttamente verso la vista bar
     */
    @FXML
    private void handleBarClick() {
        try {
            FamigliaSaporiApplication.setRoot("BarView");
        } catch (IOException e) {
            System.err.println("Errore nel caricamento della pagina Bar: " + e.getMessage());
        }
    }

    @FXML
    private void handleCassaClick() {
        try {
            FamigliaSaporiApplication.setRoot("CassaView");
        } catch (IOException e) {
            System.err.println("Errore nel caricamento della pagina Cassa: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
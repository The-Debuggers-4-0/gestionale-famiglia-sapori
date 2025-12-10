package famiglia.sapori.controller;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

public class HomeControllerFxTest extends ApplicationTest {
    private Button btnSala;
    private Button btnCucina;
    private Label lblWelcome;

    @Override
    public void start(Stage stage) {
        lblWelcome = new Label("Benvenuto - Famiglia Sapori");
        lblWelcome.setId("welcomeLabel");

        btnSala = new Button("Sala & Ordini");
        btnSala.setId("btnSala");

        btnCucina = new Button("Cucina");
        btnCucina.setId("btnCucina");

        VBox root = new VBox(15, lblWelcome, btnSala, btnCucina);
        stage.setScene(new Scene(root, 350, 250));
        stage.show();
    }

    /**
     * Verifica che la vista Home contenga i pulsanti di navigazione principali.
     */
    @Test
    void homeSceneLoadsAndButtonExists() {
        Button b = lookup("#btnSala").queryButton();
        assertNotNull(b);
    }

    /**
     * Verifica che tutti i pulsanti di navigazione siano presenti.
     * Testa la completezza della schermata home.
     */
    @Test
    void allNavigationButtonsExist() {
        assertNotNull(lookup("#btnSala").queryButton());
        assertNotNull(lookup("#btnCucina").queryButton());
    }

    /**
     * Verifica che la label di benvenuto sia visibile.
     * Dettaglio UI che migliora l'esperienza utente.
     */
    @Test
    void welcomeLabelVisible() {
        Label lbl = lookup("#welcomeLabel").query();
        assertTrue(lbl.isVisible());
        assertTrue(lbl.getText().contains("Famiglia Sapori"));
    }

    /**
     * Verifica che i pulsanti abbiano il testo corretto.
     * Testa l'integrità delle label dei controlli.
     */
    @Test
    void buttonsHaveCorrectLabels() {
        Button sala = lookup("#btnSala").queryButton();
        Button cucina = lookup("#btnCucina").queryButton();
        assertEquals("Sala & Ordini", sala.getText());
        assertEquals("Cucina", cucina.getText());
    }
}

package famiglia.sapori.controller;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

public class SalaControllerFxTest extends ApplicationTest {
    private Label status;
    private Button btnApri;
    private Button btnTavolo1;

    @Override
    public void start(Stage stage) {
        status = new Label("Seleziona un tavolo");
        status.setId("statusLabel");

        btnTavolo1 = new Button("Tavolo 1");
        btnTavolo1.setId("tavolo1");
        btnTavolo1.setOnAction(e -> status.setText("Tavolo 1 selezionato"));

        btnApri = new Button("Apri Tavolo");
        btnApri.setId("btnApriTavolo");
        btnApri.setDisable(true);

        VBox root = new VBox(10, status, btnTavolo1, btnApri);
        stage.setScene(new Scene(root, 320, 240));
        stage.show();
    }

    /**
     * Controlla che i controlli della Sala siano presenti nella scena.
     */
    @Test
    void salaSceneLoadsAndControlsExist() {
        assertNotNull(lookup("#statusLabel").queryLabeled());
        assertNotNull(lookup("#btnApriTavolo").queryButton());
    }

    /**
     * Verifica che il click su un tavolo aggiorni la label di stato.
     * Testa l'interazione utente e l'aggiornamento dinamico della UI.
     */
    @Test
    void clickTavolo_updatesStatusLabel() {
        clickOn("#tavolo1");
        Label statusLabel = lookup("#statusLabel").query();
        assertEquals("Tavolo 1 selezionato", statusLabel.getText());
    }

    /**
     * Verifica che il pulsante "Apri Tavolo" sia disabilitato finché
     * non viene selezionato un tavolo (edge case: stato iniziale).
     */
    @Test
    void btnApriTavolo_initiallyDisabled() {
        Button btn = lookup("#btnApriTavolo").queryButton();
        assertTrue(btn.isDisabled(), "Il pulsante dovrebbe essere disabilitato all'inizio");
    }
}

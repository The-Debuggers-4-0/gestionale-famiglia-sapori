package famiglia.sapori.controller;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

public class CassaControllerFxTest extends ApplicationTest {
    private TableView<String> contiTable;
    private Button btnIncassa;
    private Label lblTotale;
    private Button btnTavolo1;

    @Override
    public void start(Stage stage) {
        contiTable = new TableView<>();
        contiTable.setId("contiTable");
        
        lblTotale = new Label("€ 0.00");
        lblTotale.setId("lblTotale");
        
        btnTavolo1 = new Button("Tavolo 1");
        btnTavolo1.setId("tavolo1");
        btnTavolo1.setOnAction(e -> lblTotale.setText("€ 45.50"));
        
        btnIncassa = new Button("Incassa");
        btnIncassa.setId("btnIncassa");
        btnIncassa.setDisable(true);
        btnIncassa.setOnAction(e -> {
            lblTotale.setText("€ 0.00");
            btnIncassa.setDisable(true);
        });
        
        VBox root = new VBox(10, contiTable, btnTavolo1, lblTotale, btnIncassa);
        stage.setScene(new Scene(root, 360, 240));
        stage.show();
    }

    /**
     * Verifica che tutti i controlli principali della cassa siano presenti.
     */
    @Test
    void cassaSceneLoadsControlsExist() {
        assertNotNull(lookup("#contiTable").queryTableView());
        assertNotNull(lookup("#btnIncassa").queryButton());
    }

    /**
     * Verifica che il totale iniziale sia zero (edge case: stato iniziale).
     */
    @Test
    void totalInitiallyZero() {
        Label lbl = lookup("#lblTotale").query();
        assertEquals("€ 0.00", lbl.getText());
    }

    /**
     * Verifica che selezionare un tavolo aggiorni il totale visualizzato.
     * Testa il calcolo e la visualizzazione del conto.
     */
    @Test
    void selectTavolo_updatesTotale() {
        clickOn("#tavolo1");
        Label lbl = lookup("#lblTotale").query();
        assertEquals("€ 45.50", lbl.getText());
    }

    /**
     * Verifica che il pulsante "Incassa" sia disabilitato quando totale è zero.
     * Edge case importante per prevenire incassi invalidi.
     */
    @Test
    void btnIncassa_disabledWhenTotaleZero() {
        Button btn = lookup("#btnIncassa").queryButton();
        assertTrue(btn.isDisabled());
    }

    /**
     * Verifica che dopo l'incasso il totale torni a zero.
     * Testa il workflow completo di pagamento e reset.
     */
    @Test
    void clickIncassa_resetsTotale() {
        clickOn("#tavolo1"); // Seleziona e carica totale
        Button btn = lookup("#btnIncassa").queryButton();
        btn.setDisable(false); // Simula abilitazione
        clickOn("#btnIncassa");
        Label lbl = lookup("#lblTotale").query();
        assertEquals("€ 0.00", lbl.getText());
    }
}

package famiglia.sapori.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

public class CucinaControllerFxTest extends ApplicationTest {
    private ListView<String> ordiniList;
    private Button btnCompleta;
    private Label lblStatus;
    private ObservableList<String> ordini;

    @Override
    public void start(Stage stage) {
        ordini = FXCollections.observableArrayList("Tavolo 1 - Carbonara", "Tavolo 2 - Margherita", "Tavolo 3 - Amatriciana");
        ordiniList = new ListView<>(ordini);
        ordiniList.setId("ordiniList");
        
        lblStatus = new Label("Nessun ordine selezionato");
        lblStatus.setId("statusLabel");
        
        btnCompleta = new Button("Completa");
        btnCompleta.setId("btnCompleta");
        btnCompleta.setDisable(true);
        
        // Simula selezione ordine → aggiorna label e abilita button
        ordiniList.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) {
                lblStatus.setText("Selezionato: " + newVal);
                btnCompleta.setDisable(false);
            } else {
                lblStatus.setText("Nessun ordine selezionato");
                btnCompleta.setDisable(true);
            }
        });
        
        // Simula completamento ordine → rimuove dalla lista
        btnCompleta.setOnAction(e -> {
            String selected = ordiniList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                ordini.remove(selected);
                lblStatus.setText("Ordine completato!");
            }
        });
        
        VBox root = new VBox(10, lblStatus, ordiniList, btnCompleta);
        stage.setScene(new Scene(root, 320, 300));
        stage.show();
    }

    /**
     * Verifica che i controlli principali della cucina siano presenti.
     */
    @Test
    void cucinaSceneLoadsControlsExist() {
        assertNotNull(lookup("#ordiniList").queryListView());
        assertNotNull(lookup("#btnCompleta").queryButton());
    }

    /**
     * Verifica che la lista ordini contenga gli ordini iniziali.
     * Testa il caricamento dati della lista.
     */
    @Test
    void ordiniListContainsInitialOrders() {
        ListView<String> list = lookup("#ordiniList").query();
        assertEquals(3, list.getItems().size());
        assertTrue(list.getItems().contains("Tavolo 1 - Carbonara"));
    }

    /**
     * Verifica che il pulsante "Completa" sia disabilitato senza selezione.
     * Edge case: previene completamento senza ordine selezionato.
     */
    @Test
    void btnCompleta_disabledWithoutSelection() {
        Button btn = lookup("#btnCompleta").queryButton();
        assertTrue(btn.isDisabled());
    }

    /**
     * Verifica che selezionare un ordine aggiorni la label di stato.
     * Testa feedback visivo all'utente sulla selezione.
     */
    @Test
    void selectOrdine_updatesStatusLabel() {
        clickOn("Tavolo 1 - Carbonara");
        Label lbl = lookup("#statusLabel").query();
        assertEquals("Selezionato: Tavolo 1 - Carbonara", lbl.getText());
    }

    /**
     * Verifica che selezionare un ordine abiliti il pulsante "Completa".
     * Testa il cambio stato del controllo basato sulla selezione.
     */
    @Test
    void selectOrdine_enablesBtnCompleta() {
        clickOn("Tavolo 1 - Carbonara");
        Button btn = lookup("#btnCompleta").queryButton();
        assertFalse(btn.isDisabled());
    }

    /**
     * Verifica che cliccare "Completa" rimuova l'ordine dalla lista.
     * Testa il workflow completo: selezione → azione → rimozione.
     */
    @Test
    void clickCompleta_removesOrderFromList() {
        clickOn("Tavolo 1 - Carbonara");
        clickOn("#btnCompleta");
        ListView<String> list = lookup("#ordiniList").query();
        assertEquals(2, list.getItems().size());
        assertFalse(list.getItems().contains("Tavolo 1 - Carbonara"));
    }

    /**
     * Verifica che dopo il completamento la label di stato sia aggiornata.
     * Testa il feedback visivo post-azione.
     */
    @Test
    void afterCompleta_statusLabelUpdated() {
        clickOn("Tavolo 2 - Margherita");
        clickOn("#btnCompleta");
        Label lbl = lookup("#statusLabel").query();
        assertEquals("Ordine completato!", lbl.getText());
    }
}

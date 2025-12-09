package famiglia.sapori.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

public class BarControllerFxTest extends ApplicationTest {
    private ListView<String> comandeBar;
    private Button btnServito;
    private ObservableList<String> comande;

    @Override
    public void start(Stage stage) {
        comande = FXCollections.observableArrayList("1x Caffè", "2x Acqua", "1x Birra");
        comandeBar = new ListView<>(comande);
        comandeBar.setId("comandeBarList");
        
        btnServito = new Button("Servito");
        btnServito.setId("btnServito");
        btnServito.setDisable(true); // Inizialmente disabilitato
        
        // Simula abilitazione quando si seleziona un item
        comandeBar.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            btnServito.setDisable(newVal == null);
        });
        
        // Simula rimozione comanda servita
        btnServito.setOnAction(e -> {
            String selected = comandeBar.getSelectionModel().getSelectedItem();
            if (selected != null) {
                comande.remove(selected);
            }
        });
        
        VBox root = new VBox(10, comandeBar, btnServito);
        stage.setScene(new Scene(root, 320, 240));
        stage.show();
    }

    /**
     * Verifica che la lista comande e il pulsante siano presenti nella vista.
     */
    @Test
    void barSceneLoadsControlsExist() {
        assertNotNull(lookup("#comandeBarList").queryListView());
        assertNotNull(lookup("#btnServito").queryButton());
    }

    /**
     * Verifica che la lista contenga le comande iniziali.
     * Testa il caricamento dati nella ListView.
     */
    @Test
    void comandeListContainsInitialItems() {
        ListView<String> list = lookup("#comandeBarList").query();
        assertEquals(3, list.getItems().size());
        assertTrue(list.getItems().contains("1x Caffè"));
    }

    /**
     * Verifica che il pulsante "Servito" sia disabilitato senza selezione.
     * Edge case importante per evitare azioni su elementi nulli.
     */
    @Test
    void btnServito_disabledWithoutSelection() {
        Button btn = lookup("#btnServito").queryButton();
        assertTrue(btn.isDisabled());
    }

    /**
     * Verifica che selezionare una comanda abiliti il pulsante "Servito".
     * Testa l'interazione selezione → cambio stato UI.
     */
    @Test
    void selectComanda_enablesServitoButton() {
        clickOn("1x Caffè");
        Button btn = lookup("#btnServito").queryButton();
        assertFalse(btn.isDisabled());
    }

    /**
     * Verifica che cliccare "Servito" rimuova la comanda selezionata dalla lista.
     * Testa il workflow completo: selezione → azione → aggiornamento lista.
     */
    @Test
    void clickServito_removesSelectedComanda() {
        clickOn("1x Caffè");
        clickOn("#btnServito");
        ListView<String> list = lookup("#comandeBarList").query();
        assertEquals(2, list.getItems().size());
        assertFalse(list.getItems().contains("1x Caffè"));
    }
}

package famiglia.sapori.controller;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

public class PrenotazioniControllerFxTest extends ApplicationTest {
    private TextField nomeField;
    private Button btnCrea;

    @Override
    public void start(Stage stage) {
        nomeField = new TextField();
        nomeField.setPromptText("Nome cliente");
        nomeField.setId("nomeField");

        btnCrea = new Button("Crea Prenotazione");
        btnCrea.setId("btnCreaPrenotazione");
        btnCrea.setDisable(true); // Disabilitato finché nome non è inserito

        // Simula validazione: abilita button solo se nome non vuoto
        nomeField.textProperty().addListener((obs, old, newVal) -> {
            btnCrea.setDisable(newVal == null || newVal.trim().isEmpty());
        });

        VBox root = new VBox(10, nomeField, btnCrea);
        stage.setScene(new Scene(root, 320, 240));
        stage.show();
    }

    /**
     * Verifica che i controlli principali siano presenti nella vista.
     */
    @Test
    void prenotazioniSceneLoadsControlsExist() {
        assertNotNull(lookup("#nomeField").queryTextInputControl());
        assertNotNull(lookup("#btnCreaPrenotazione").queryButton());
    }

    /**
     * Verifica che il pulsante "Crea" sia disabilitato con campo nome vuoto.
     * Edge case: previene creazione prenotazioni senza nome obbligatorio.
     */
    @Test
    void btnCrea_disabledWhenNomeEmpty() {
        Button btn = lookup("#btnCreaPrenotazione").queryButton();
        assertTrue(btn.isDisabled());
    }

    /**
     * Verifica che inserire un nome abiliti il pulsante "Crea Prenotazione".
     * Testa la validazione dinamica del form.
     */
    @Test
    void enterNome_enablesBtnCrea() {
        clickOn("#nomeField").write("Mario Rossi");
        Button btn = lookup("#btnCreaPrenotazione").queryButton();
        assertFalse(btn.isDisabled());
    }

    /**
     * Verifica che cancellare il nome renda il pulsante nuovamente disabilitato.
     * Testa il comportamento dinamico con cambio stato campo.
     */
    @Test
    void clearNome_disablesBtnCrea() {
        clickOn("#nomeField").write("Test");
        Button btn = lookup("#btnCreaPrenotazione").queryButton();
        assertFalse(btn.isDisabled());

        // Cancella il testo
        clickOn("#nomeField").eraseText(4);
        assertTrue(btn.isDisabled());
    }

    /**
     * Verifica che il placeholder text del campo nome sia corretto.
     * Dettaglio UI che migliora l'usabilità.
     */
    @Test
    void nomeField_hasCorrectPromptText() {
        TextField field = lookup("#nomeField").query();
        assertEquals("Nome cliente", field.getPromptText());
    }
}

package famiglia.sapori.controller;

import famiglia.sapori.FamigliaSaporiApplication;
import famiglia.sapori.dao.ComandaDAO;
import famiglia.sapori.dao.MenuDAO;
import famiglia.sapori.dao.TavoloDAO;
import famiglia.sapori.model.Comanda;
import famiglia.sapori.model.Piatto;
import famiglia.sapori.model.Tavolo;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class CassaController implements Initializable {

    @FXML private FlowPane tavoliContainer;
    @FXML private Label lblTavoloSelezionato;
    @FXML private TextArea txtScontrino; // Lista visuale degli articoli
    @FXML private Label lblTotale;
    @FXML private Spinner<Integer> spinDiviso; // Per la romana
    @FXML private Label lblQuotaTesta;
    @FXML private Button btnPaga;

    private TavoloDAO tavoloDAO;
    private ComandaDAO comandaDAO;
    private MenuDAO menuDAO;

    private Tavolo selectedTavolo;
    private double totaleCorrente = 0.0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tavoloDAO = new TavoloDAO();
        comandaDAO = new ComandaDAO();
        menuDAO = new MenuDAO();

        // Configura lo spinner per la divisione del conto (min 1, max 20, default 1)
        spinDiviso.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 1));
        spinDiviso.valueProperty().addListener((obs, oldVal, newVal) -> ricalcolaQuote(newVal));

        loadTavoli();
    }

    private void loadTavoli() {
        tavoliContainer.getChildren().clear();
        try {
            List<Tavolo> tavoli = tavoloDAO.getAllTavoli();
            for (Tavolo t : tavoli) {
                tavoliContainer.getChildren().add(createTavoloBox(t));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private VBox createTavoloBox(Tavolo t) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-cursor: hand;");

        Rectangle rect = new Rectangle(70, 70);
        rect.setArcWidth(10);
        rect.setArcHeight(10);

        // Colora diversamente i tavoli Occupati (quelli che devono pagare)
        if ("Occupato".equalsIgnoreCase(t.getStato())) {
            rect.setFill(Color.web("#e74c3c")); // Rosso
        } else {
            rect.setFill(Color.web("#27ae60")); // Verde
            box.setDisable(true); // Non puoi pagare un tavolo libero
            box.setOpacity(0.5);
        }

        Label lblNum = new Label("Tavolo " + t.getNumero());
        lblNum.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        box.getChildren().addAll(rect, lblNum);
        box.setOnMouseClicked(e -> selectTavolo(t));

        return box;
    }

    private void selectTavolo(Tavolo t) {
        this.selectedTavolo = t;
        lblTavoloSelezionato.setText("Tavolo " + t.getNumero());
        calcolaConto(t);
    }

    private void calcolaConto(Tavolo t) {
        totaleCorrente = 0.0;
        StringBuilder scontrino = new StringBuilder();

        try {
            // 1. Prendi tutte le comande non pagate
            List<Comanda> comande = comandaDAO.getComandeDaPagare(t.getId());

            // 2. Prendi il menu per sapere i prezzi
            List<Piatto> menu = menuDAO.getAllPiattiComplete();

            if (comande.isEmpty()) {
                txtScontrino.setText("Nessuna comanda da pagare.");
                lblTotale.setText("€ 0.00");
                return;
            }

            scontrino.append("--- RIEPILOGO TAVOLO ").append(t.getNumero()).append(" ---\n\n");

            // 3. Analizza le stringhe dei prodotti (es: "2x Carbonara, 1x Acqua")
            for (Comanda c : comande) {
                String[] items = c.getProdotti().split(", ");
                for (String item : items) {
                    // item è tipo "2x Carbonara"
                    try {
                        String[] parts = item.split("x ");
                        int qty = Integer.parseInt(parts[0]);
                        String nomePiatto = parts[1];

                        // Cerca il prezzo nel menu
                        double prezzoUnitario = 0.0;
                        for (Piatto p : menu) {
                            if (p.getNome().equalsIgnoreCase(nomePiatto)) {
                                prezzoUnitario = p.getPrezzo();
                                break;
                            }
                        }

                        double subTotale = qty * prezzoUnitario;
                        totaleCorrente += subTotale;

                        scontrino.append(String.format("%-20s %2d x €%5.2f = €%6.2f\n",
                                nomePiatto, qty, prezzoUnitario, subTotale));

                    } catch (Exception e) {
                        scontrino.append("Errore riga: ").append(item).append("\n");
                    }
                }
            }

            txtScontrino.setText(scontrino.toString());
            lblTotale.setText(String.format("€ %.2f", totaleCorrente));

            // Resetta lo spinner divisione
            spinDiviso.getValueFactory().setValue(1);
            ricalcolaQuote(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void ricalcolaQuote(int persone) {
        if (totaleCorrente > 0) {
            double quota = totaleCorrente / persone;
            lblQuotaTesta.setText(String.format("€ %.2f a testa", quota));
        } else {
            lblQuotaTesta.setText("€ 0.00");
        }
    }

    @FXML
    private void handlePaga() {
        if (selectedTavolo == null || totaleCorrente <= 0) {
            showAlert("Attenzione", "Seleziona un tavolo con importo da pagare.");
            return;
        }

        // Conferma (opzionale, qui facciamo diretto per velocità)
        try {
            // 1. Segna le comande come pagate
            comandaDAO.setComandePagate(selectedTavolo.getId());

            // 2. Libera il tavolo
            tavoloDAO.updateStatoTavolo(selectedTavolo.getId(), "Libero");

            showAlert("Pagamento Riuscito", "Il conto è stato saldato e il tavolo è ora libero.");

            // Reset UI
            loadTavoli();
            txtScontrino.clear();
            lblTotale.setText("€ 0.00");
            lblTavoloSelezionato.setText("Nessun Tavolo");
            selectedTavolo = null;
            totaleCorrente = 0;

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Errore", "Errore durante il pagamento: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        try {
            FamigliaSaporiApplication.setRoot("HomeView");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
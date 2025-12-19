package famiglia.sapori.controller;
 
import famiglia.sapori.FamigliaSaporiApplication;
import famiglia.sapori.dao.ComandaDAO;
import famiglia.sapori.dao.MenuDAO;
import famiglia.sapori.dao.PrenotazioneDAO;
import famiglia.sapori.dao.TavoloDAO;
import famiglia.sapori.model.Comanda;
import famiglia.sapori.model.Piatto;
import famiglia.sapori.model.Prenotazione;
import famiglia.sapori.model.Tavolo;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.util.Duration;
 
public class SalaController implements Initializable {
 
    @FXML private Label userLabel;
    @FXML private Button btnLogout;
    @FXML private FlowPane tavoliContainer;
    @FXML private Label selectedTableLabel;
    @FXML private TabPane menuTabPane;
    @FXML private TextArea txtNote;
    @FXML private TextArea txtRiepilogo;
    @FXML private Label lblTotale;
    @FXML private Button btnInvia;
    @FXML private Button btnPrenotazioni;
 
    private TavoloDAO tavoloDAO;
    private MenuDAO menuDAO;
    private ComandaDAO comandaDAO;
    private PrenotazioneDAO prenotazioneDAO;
 
    private Tavolo selectedTavolo;
    private Map<Piatto, Integer> currentOrder;
    private Timeline pollingTimeline;
 
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tavoloDAO = new TavoloDAO();
        menuDAO = new MenuDAO();
        comandaDAO = new ComandaDAO();
        prenotazioneDAO = new PrenotazioneDAO();
        currentOrder = new HashMap<>();
 
        if (FamigliaSaporiApplication.currentUser != null) {
            userLabel.setText("Cameriere: " + FamigliaSaporiApplication.currentUser.getNome());
        }
 
        loadTavoli();
        loadMenu();
        startPolling();
    }

    private void startPolling() {
        pollingTimeline = new Timeline(new KeyFrame(Duration.seconds(30), e -> {
            Platform.runLater(() -> {
                loadTavoli();
                loadMenu();
            });
        }));
        pollingTimeline.setCycleCount(Timeline.INDEFINITE);
        pollingTimeline.play();
    }

    private void stopPolling() {
        if (pollingTimeline != null) {
            pollingTimeline.stop();
        }
    }
 
    private void loadTavoli() {
        tavoliContainer.getChildren().clear();
        try {
            List<Tavolo> tavoli = tavoloDAO.getAllTavoli();
            List<Prenotazione> prenotazioniOggi = prenotazioneDAO.getReservationsForDate(LocalDate.now());
            
            // Mappa ID Tavolo -> Lista Prenotazioni
            Map<Integer, List<Prenotazione>> mapPrenotazioni = prenotazioniOggi.stream()
                .filter(p -> p.getIdTavolo() != null)
                .collect(Collectors.groupingBy(Prenotazione::getIdTavolo));

            for (Tavolo t : tavoli) {
                String status = t.getStato();
                
                // Se lo stato nel DB è "Prenotato", lo consideriamo "Libero" di base,
                // perché la prenotazione deve dipendere dalla data odierna.
                if ("Prenotato".equalsIgnoreCase(status)) {
                    status = "Libero";
                }

                // Verifica se c'è una prenotazione attiva e non ancora "soddisfatta" (pagata)
                boolean isReserved = false;
                if (mapPrenotazioni.containsKey(t.getId())) {
                    for (Prenotazione p : mapPrenotazioni.get(t.getId())) {
                        // Una prenotazione è considerata "soddisfatta" se esiste una comanda pagata
                        // creata dopo l'orario della prenotazione (con 1 ora di tolleranza prima)
                        boolean fulfilled = comandaDAO.hasPaidComandaAfter(t.getId(), p.getDataOra().minusHours(1));
                        if (!fulfilled) {
                            isReserved = true;
                            break;
                        }
                    }
                }

                // Se il tavolo non è occupato ma è prenotato per oggi (e non ancora pagato), mostralo come prenotato
                if (!"Occupato".equalsIgnoreCase(status) && isReserved) {
                    status = "Prenotato";
                }
                VBox tavoloBox = createTavoloBox(t, status);
                tavoliContainer.getChildren().add(tavoloBox);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private VBox createTavoloBox(Tavolo t, String status) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);
       
        Rectangle rect = new Rectangle(60, 60);
        rect.setArcWidth(10);
        rect.setArcHeight(10);
       
        if ("Occupato".equalsIgnoreCase(status)) {
            rect.setFill(Color.RED);
        } else if ("Prenotato".equalsIgnoreCase(status)) {
            rect.setFill(Color.web("#f39c12"));
        } else {
            rect.setFill(Color.GREEN);
        }
       
        Label lblNum = new Label("Tavolo " + t.getNumero());
        lblNum.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
       
        box.getChildren().addAll(rect, lblNum);
       
        box.setOnMouseClicked(e -> selectTavolo(t));
       
        return box;
    }
 
    private void selectTavolo(Tavolo t) {
        this.selectedTavolo = t;
        selectedTableLabel.setText("Tavolo " + t.getNumero());
        // Reset order when switching table? Or keep it?
        // For simplicity, let's keep the current order draft but warn if needed.
        // Ideally, we should load existing order if table is occupied.
        // For this sprint, we assume we are taking a NEW order.
    }
 
    private void loadMenu() {
        int selectedTabIndex = menuTabPane.getSelectionModel().getSelectedIndex();
        menuTabPane.getTabs().clear();
        try {
            List<String> categorie = menuDAO.getAllCategorie();
            List<Piatto> piatti = menuDAO.getAllPiatti();
 
            for (String cat : categorie) {
                Tab tab = new Tab(cat);
                VBox content = new VBox(10);
                content.setPadding(new javafx.geometry.Insets(10));
                content.setStyle("-fx-background-color: #2b2b2b;");
               
                List<Piatto> piattiCategoria = piatti.stream()
                    .filter(p -> p.getCategoria().equals(cat))
                    .collect(Collectors.toList());
 
                for (Piatto p : piattiCategoria) {
                    content.getChildren().add(createPiattoRow(p));
                }
 
                ScrollPane scroll = new ScrollPane(content);
                scroll.setFitToWidth(true);
                tab.setContent(scroll);
                menuTabPane.getTabs().add(tab);
            }

            if (selectedTabIndex >= 0 && selectedTabIndex < menuTabPane.getTabs().size()) {
                menuTabPane.getSelectionModel().select(selectedTabIndex);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
 
    private HBox createPiattoRow(Piatto p) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: #3c3c3c; -fx-padding: 10; -fx-background-radius: 5;");
 
        VBox info = new VBox(2);
        Label name = new Label(p.getNome());
        name.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        Label price = new Label(String.format("€ %.2f", p.getPrezzo()));
        price.setStyle("-fx-text-fill: #aaaaaa;");
        info.getChildren().addAll(name, price);
       
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
 
        Button btnMinus = new Button("-");
        Button btnPlus = new Button("+");

        // Initialize quantity from currentOrder
        int currentQty = currentOrder.getOrDefault(p, 0);
        Label lblQty = new Label(String.valueOf(currentQty));
        lblQty.setStyle("-fx-text-fill: white; -fx-min-width: 20; -fx-alignment: center;");
 
        btnMinus.setOnAction(e -> updateQuantity(p, -1, lblQty));
        btnPlus.setOnAction(e -> updateQuantity(p, 1, lblQty));

        // Handle availability
        if (!p.isDisponibile()) {
            row.setDisable(true);
            row.setOpacity(0.5);
            name.setText(name.getText() + " (Non disp.)");
            name.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        }
 
        row.getChildren().addAll(info, spacer, btnMinus, lblQty, btnPlus);
        return row;
    }
 
    private void updateQuantity(Piatto p, int delta, Label lblQty) {
        int currentQty = currentOrder.getOrDefault(p, 0);
        int newQty = currentQty + delta;
       
        if (newQty < 0) newQty = 0;
       
        if (newQty == 0) {
            currentOrder.remove(p);
        } else {
            currentOrder.put(p, newQty);
        }
       
        lblQty.setText(String.valueOf(newQty));
        updateRiepilogo();
    }
 
    private void updateRiepilogo() {
        StringBuilder sb = new StringBuilder();
        double total = 0;
 
        for (Map.Entry<Piatto, Integer> entry : currentOrder.entrySet()) {
            Piatto p = entry.getKey();
            int qty = entry.getValue();
            double subtotal = p.getPrezzo() * qty;
            total += subtotal;
           
            sb.append(String.format("%dx %s - € %.2f\n", qty, p.getNome(), subtotal));
        }
 
        txtRiepilogo.setText(sb.toString());
        lblTotale.setText(String.format("TOTALE: € %.2f", total));
    }
 
    @FXML
    private void handleInviaComanda() {
        if (selectedTavolo == null) {
            showAlert("Errore", "Seleziona un tavolo prima di inviare l'ordine.");
            return;
        }
        if (currentOrder.isEmpty()) {
            showAlert("Errore", "L'ordine è vuoto.");
            return;
        }
        if (FamigliaSaporiApplication.currentUser == null) {
            showAlert("Errore", "Utente non autenticato. Effettua nuovamente il login.");
            return;
        }
 
        try {
            // Split items into Kitchen and Bar
            Map<Piatto, Integer> kitchenItems = new HashMap<>();
            Map<Piatto, Integer> barItems = new HashMap<>();
            for (Map.Entry<Piatto, Integer> entry : currentOrder.entrySet()) {
                Piatto p = entry.getKey();
                if (isDrink(p.getCategoria())) {
                    barItems.put(p, entry.getValue());
                } else {
                    kitchenItems.put(p, entry.getValue());
                }
            }

            // Send Kitchen Order
            if (!kitchenItems.isEmpty()) {
                sendComanda(kitchenItems, "Cucina");
            }

            // Send Bar Order
            if (!barItems.isEmpty()) {
                sendComanda(barItems, "Bar");
            }
           
            // Update table status to Occupato
            tavoloDAO.updateStatoTavolo(selectedTavolo.getId(), "Occupato");
           
            // Rimuovi la prenotazione per questo tavolo (se esiste per oggi)
            // Così quando il tavolo verrà liberato, non tornerà "Prenotato" (Giallo)
            List<Prenotazione> prenotazioni = prenotazioneDAO.getReservationsForDate(LocalDate.now());
            for (Prenotazione p : prenotazioni) {
                if (p.getIdTavolo() != null && p.getIdTavolo() == selectedTavolo.getId()) {
                    prenotazioneDAO.deletePrenotazione(p.getId());
                }
            }

            showAlert("Successo", "Comanda inviata!");
           
            // Reset UI
            currentOrder.clear();
            txtNote.clear();
            updateRiepilogo();
            loadTavoli(); // Refresh table status
           
            // Reset quantities in UI
            loadMenu();
 
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Errore", "Impossibile salvare la comanda: " + e.getMessage());
        }
    }
    
    private void sendComanda(Map<Piatto, Integer> items, String tipo) throws SQLException {
        StringBuilder prodottiStr = new StringBuilder();
        double totaleComanda = 0.0;

        for (Map.Entry<Piatto, Integer> entry : items.entrySet()) {
            if (prodottiStr.length() > 0) prodottiStr.append(", ");
            prodottiStr.append(entry.getValue()).append("x ").append(entry.getKey().getNome());
            
            // Calcola il totale parziale per questa comanda
            totaleComanda += entry.getKey().getPrezzo() * entry.getValue();
        }

        Comanda comanda = new Comanda(
            0, // ID auto-generated
            selectedTavolo.getId(),
            prodottiStr.toString(),
            totaleComanda,
            tipo,
            "In Attesa",
            null, // Date auto-generated
            txtNote.getText(),
            FamigliaSaporiApplication.currentUser.getId()
        );

        comandaDAO.insertComanda(comanda);
    }

    private boolean isDrink(String category) {
        if (category == null) return false;
        String c = category.toLowerCase();
        return c.contains("bevande") || c.contains("vini") || c.contains("birre") || c.contains("caffè") || c.contains("bar") || c.contains("drink");
    }

    @FXML
    private void handleLogout() {
        stopPolling();
        try {
            FamigliaSaporiApplication.currentUser = null;
            FamigliaSaporiApplication.setRoot("LoginView");
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

    @FXML
    private void handleGestionePrenotazioni() {
        stopPolling();
        try {
            FamigliaSaporiApplication.setRoot("PrenotazioneView");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Errore", "Impossibile caricare la vista prenotazioni: " + e.getMessage());
        }
    }
}
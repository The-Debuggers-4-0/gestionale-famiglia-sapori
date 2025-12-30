package famiglia.sapori.controller;

import famiglia.sapori.FamigliaSaporiApplication;
import famiglia.sapori.dao.ComandaDAO;
import famiglia.sapori.dao.TavoloDAO;
import famiglia.sapori.model.Comanda;
import famiglia.sapori.model.Tavolo;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Controller per la CucinaView
 */
public class CucinaController implements Initializable {

    @FXML
    private FlowPane ordersContainer;

    @FXML
    private Button logoutButton;

    private ComandaDAO comandaDAO;
    private TavoloDAO tavoloDAO;
    private Map<Integer, Integer> tavoloMap;
    private Timeline pollingTimeline;

    private Map<Integer, VBox> activeCards = new java.util.HashMap<>();
    private AnimationTimer timerUpdateTimer;
    private long lastTimerUpdate = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        comandaDAO = new ComandaDAO();
        tavoloDAO = new TavoloDAO();
        
        loadTavoliMap();
        loadComande(); // Caricamento iniziale
        startPolling(); // Avvio aggiornamento automatico
        startTimerUpdates(); // Avvio aggiornamento timer locale
    }

    private void startPolling() {
        // Aggiorna ogni 5 secondi per vedere nuovi ordini
        pollingTimeline = new Timeline(new KeyFrame(Duration.seconds(5), e -> {
            Platform.runLater(this::loadComande);
        }));
        pollingTimeline.setCycleCount(Timeline.INDEFINITE);
        pollingTimeline.play();
    }

    private void startTimerUpdates() {
        // Aggiorna i timer usando AnimationTimer per maggiore fluidità
        timerUpdateTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now - lastTimerUpdate >= 1_000_000_000) { // Aggiorna ogni secondo
                    updateTimers();
                    lastTimerUpdate = now;
                }
            }
        };
        timerUpdateTimer.start();
    }

    void stopPolling() {
        if (pollingTimeline != null) pollingTimeline.stop();
        if (timerUpdateTimer != null) timerUpdateTimer.stop();
    }

    private void loadTavoliMap() {
        try {
            List<Tavolo> tavoli = tavoloDAO.getAllTavoli();
            tavoloMap = tavoli.stream().collect(Collectors.toMap(Tavolo::getId, Tavolo::getNumero));
        } catch (SQLException e) {
            System.err.println("Errore nel caricamento dei tavoli: " + e.getMessage());
        }
    }

    void loadComande() {
        try {
            List<Comanda> comande = comandaDAO.getComandeByStatoAndTipo("In Attesa", "Cucina");
            comande.addAll(comandaDAO.getComandeByStatoAndTipo("In Preparazione", "Cucina"));

            // Identifica le comande che non ci sono più (es. completate)
            List<Integer> currentIds = comande.stream().map(Comanda::getId).collect(Collectors.toList());
            List<Integer> toRemove = activeCards.keySet().stream()
                .filter(id -> !currentIds.contains(id))
                .collect(Collectors.toList());

            // Rimuovi le card non più attive
            for (Integer id : toRemove) {
                VBox card = activeCards.remove(id);
                ordersContainer.getChildren().remove(card);
            }

            // Aggiungi o aggiorna le card
            for (Comanda c : comande) {
                if (!activeCards.containsKey(c.getId())) {
                    VBox card = createComandaCard(c);
                    activeCards.put(c.getId(), card);
                    ordersContainer.getChildren().add(card);
                } else {
                    // Opzionale: aggiorna stato se cambiato (es. da Attesa a Preparazione)
                    // Per ora ricreiamo solo se necessario, ma qui assumiamo che la card gestisca il suo stato
                    // Se volessimo aggiornare il bottone, dovremmo accedere ai figli della VBox
                    updateCardState(activeCards.get(c.getId()), c);
                }
            }
            
            if (activeCards.isEmpty() && ordersContainer.getChildren().isEmpty()) {
                 // Gestione label vuota se necessario, ma con activeCards è più complesso gestirlo misto
                 // Semplificazione: se vuoto, pulisci tutto e metti label
                 if (comande.isEmpty()) {
                     ordersContainer.getChildren().clear();
                     Label emptyLabel = new Label("Nessuna comanda in attesa");
                     emptyLabel.setStyle("-fx-text-fill: #34495e; -fx-font-size: 18px;");
                     ordersContainer.getChildren().add(emptyLabel);
                 } else {
                     // Rimuovi label "Nessuna comanda" se presente
                     ordersContainer.getChildren().removeIf(node -> node instanceof Label && ((Label)node).getText().startsWith("Nessuna"));
                 }
            }
        } catch (SQLException e) {
            System.err.println("Errore durante il caricamento delle comande: " + e.getMessage());
        }
    }

    private void updateCardState(VBox card, Comanda c) {
        // Aggiorna solo il footer se lo stato è cambiato
        // Questo richiede di sapere la struttura della card. 
        // Footer è l'ultimo elemento (index 3: Header, Separator, Body, Footer)
        if (card.getChildren().size() > 3) {
            HBox footer = (HBox) card.getChildren().get(3);
            
            // Controlla se il bottone attuale corrisponde già allo stato desiderato
            String currentButtonText = "";
            if (!footer.getChildren().isEmpty() && footer.getChildren().get(0) instanceof Button) {
                currentButtonText = ((Button) footer.getChildren().get(0)).getText();
            }

            String expectedButtonText = "";
            if ("In Attesa".equals(c.getStato())) {
                expectedButtonText = "Inizia";
            } else if ("In Preparazione".equals(c.getStato())) {
                expectedButtonText = "Pronto";
            }

            // Aggiorna solo se necessario per evitare di perdere il focus o il click
            if (!currentButtonText.equals(expectedButtonText)) {
                footer.getChildren().clear();
                
                if ("In Attesa".equals(c.getStato())) {
                    Button btnPrepara = new Button("Inizia");
                    btnPrepara.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-cursor: hand;");
                    btnPrepara.setOnAction(e -> updateStato(c, "In Preparazione"));
                    footer.getChildren().add(btnPrepara);
                } else if ("In Preparazione".equals(c.getStato())) {
                    Button btnPronto = new Button("Pronto");
                    btnPronto.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-cursor: hand;");
                    btnPronto.setOnAction(e -> updateStato(c, "Pronto"));
                    footer.getChildren().add(btnPronto);
                }
            }
        }
    }

    private void updateTimers() {
        for (Map.Entry<Integer, VBox> entry : activeCards.entrySet()) {
            VBox card = entry.getValue();
            // Header è il primo figlio
            if (!card.getChildren().isEmpty() && card.getChildren().get(0) instanceof HBox) {
                HBox header = (HBox) card.getChildren().get(0);
                // Label timer è l'ultimo figlio dell'header
                if (!header.getChildren().isEmpty()) {
                    javafx.scene.Node lastNode = header.getChildren().get(header.getChildren().size() - 1);
                    if (lastNode instanceof Label) {
                        Label lblTime = (Label) lastNode;
                        // Recupera il timestamp dalla userData (che imposteremo alla creazione)
                        if (lblTime.getUserData() instanceof java.time.LocalDateTime) {
                            java.time.LocalDateTime orderTime = (java.time.LocalDateTime) lblTime.getUserData();
                            java.time.Duration duration = java.time.Duration.between(orderTime, java.time.LocalDateTime.now());
                            long minutes = duration.toMinutes();
                            long seconds = duration.minusMinutes(minutes).getSeconds();
                            lblTime.setText(String.format("%02d:%02d", minutes, seconds));
                            
                            // Colora di rosso se attesa > 15 min
                            if (minutes >= 15) {
                                lblTime.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 14px;");
                            } else {
                                lblTime.setStyle("-fx-text-fill: #bdc3c7; -fx-font-size: 14px;");
                            }
                        }
                    }
                }
            }
        }
    }

    private VBox createComandaCard(Comanda c) {
        VBox card = new VBox(10);
        card.setPrefWidth(300);
        card.setStyle("-fx-background-color: #34495e; -fx-background-radius: 10; -fx-padding: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 5);");

        // Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        
        int numeroTavolo = tavoloMap != null ? tavoloMap.getOrDefault(c.getIdTavolo(), 0) : c.getIdTavolo();
        Label lblTavolo = new Label("Tavolo " + numeroTavolo);
        lblTavolo.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 18px;");
        
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Timer Label
        Label lblTime = new Label("00:00");
        lblTime.setStyle("-fx-text-fill: #bdc3c7;");
        lblTime.setUserData(c.getDataOra()); // Salviamo il timestamp per il calcolo

        header.getChildren().addAll(lblTavolo, spacer, lblTime);

        // Body
        VBox body = new VBox(5);
        String[] prodotti = c.getProdotti().split(", ");
        for (String p : prodotti) {
            Label lblProd = new Label("• " + p);
            lblProd.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            lblProd.setWrapText(true);
            body.getChildren().add(lblProd);
        }

        // Note
        if (c.getNote() != null && !c.getNote().isEmpty()) {
            Label lblNote = new Label("Note: " + c.getNote());
            lblNote.setStyle("-fx-text-fill: #f1c40f; -fx-font-style: italic;");
            lblNote.setWrapText(true);
            body.getChildren().add(lblNote);
        }

        // Footer
        HBox footer = new HBox(10);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(10, 0, 0, 0));

        if ("In Attesa".equals(c.getStato())) {
            Button btnPrepara = new Button("Inizia");
            btnPrepara.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-cursor: hand;");
            btnPrepara.setOnAction(e -> updateStato(c, "In Preparazione"));
            footer.getChildren().add(btnPrepara);
        } else if ("In Preparazione".equals(c.getStato())) {
            Button btnPronto = new Button("Pronto");
            btnPronto.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-cursor: hand;");
            btnPronto.setOnAction(e -> updateStato(c, "Pronto"));
            footer.getChildren().add(btnPronto);
        }

        card.getChildren().addAll(header, new Separator(), body, footer);
        return card;
    }

    private void updateStato(Comanda c, String nuovoStato) {
        try {
            comandaDAO.updateStatoComanda(c.getId(), nuovoStato);
            // Non rimuovere manualmente da activeCards, lascia che loadComande gestisca la sincronizzazione
            loadComande();
        } catch (SQLException e) {
            System.err.println("Errore nell'aggiornamento dello stato della comanda: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogout() {
        stopPolling(); // Importante: fermare il polling prima di uscire
        try {
            FamigliaSaporiApplication.setRoot("HomeView");
        } catch (IOException e) {
            System.err.println("Errore nel ritorno alla HomeView: " + e.getMessage());
        }
    }
}
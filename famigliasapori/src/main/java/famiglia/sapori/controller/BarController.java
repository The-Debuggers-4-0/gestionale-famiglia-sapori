package famiglia.sapori.controller;

import famiglia.sapori.FamigliaSaporiApplication;
import famiglia.sapori.dao.ComandaDAO;
import famiglia.sapori.dao.MenuDAO;
import famiglia.sapori.dao.TavoloDAO;
import famiglia.sapori.model.Comanda;
import famiglia.sapori.model.Piatto;
import famiglia.sapori.model.Tavolo;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class BarController implements Initializable {

    @FXML
    private FlowPane ordersContainer;
    @FXML
    private TextField searchField;
    @FXML
    private ListView<Piatto> drinksList;

    private ComandaDAO comandaDAO;
    private TavoloDAO tavoloDAO;
    private MenuDAO menuDAO;
    private Map<Integer, Integer> tavoloMap;
    private Timeline pollingTimeline;
    private ObservableList<Piatto> allDrinks;

    private Map<Integer, VBox> activeCards = new java.util.HashMap<>();
    private AnimationTimer timerUpdateTimer;
    private long lastTimerUpdate = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        comandaDAO = new ComandaDAO();
        tavoloDAO = new TavoloDAO();
        menuDAO = new MenuDAO();

        loadTavoliMap();
        loadComande();
        loadDrinks();
        startPolling();
        startTimerUpdates();

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterDrinks(newValue);
        });
    }

    private void startPolling() {
        pollingTimeline = new Timeline(new KeyFrame(Duration.seconds(5), e -> {
            Platform.runLater(this::loadComande);
        }));
        pollingTimeline.setCycleCount(Animation.INDEFINITE);
        pollingTimeline.play();
    }

    private void startTimerUpdates() {
        timerUpdateTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now - lastTimerUpdate >= 1_000_000_000) {
                    updateTimers();
                    lastTimerUpdate = now;
                }
            }
        };
        timerUpdateTimer.start();
    }

    public void stopPolling() {
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

    private void loadComande() {
        try {
            List<Comanda> comande = comandaDAO.getComandeByStatoAndTipo(Comanda.STATO_IN_ATTESA, "Bar");
            comande.addAll(comandaDAO.getComandeByStatoAndTipo(Comanda.STATO_IN_PREPARAZIONE, "Bar"));

            List<Integer> currentIds = comande.stream().map(Comanda::getId).collect(Collectors.toList());
            List<Integer> toRemove = activeCards.keySet().stream()
                .filter(id -> !currentIds.contains(id))
                .collect(Collectors.toList());

            for (Integer id : toRemove) {
                VBox card = activeCards.remove(id);
                ordersContainer.getChildren().remove(card);
            }

            if (!comande.isEmpty()) {
                ordersContainer.getChildren().removeIf(node -> node instanceof Label && ((Label)node).getText().startsWith("Nessuna"));
            }

            for (Comanda c : comande) {
                if (!activeCards.containsKey(c.getId())) {
                    VBox card = createComandaCard(c);
                    activeCards.put(c.getId(), card);
                    ordersContainer.getChildren().add(card);
                } else {
                    updateCardState(activeCards.get(c.getId()), c);
                }
            }

            if (activeCards.isEmpty() && ordersContainer.getChildren().isEmpty()) {
                 Label emptyLabel = new Label("Nessuna comanda in attesa");
                 emptyLabel.setStyle("-fx-text-fill: #34495e; -fx-font-size: 18px;");
                 ordersContainer.getChildren().add(emptyLabel);
            }
        } catch (SQLException e) {
            System.err.println("Errore durante il caricamento delle comande: " + e.getMessage());
        }
    }

    private void updateCardState(VBox card, Comanda c) {
        if (card.getChildren().size() > 3) {
            HBox footer = (HBox) card.getChildren().get(3);
            
            // Controlla se il bottone attuale corrisponde già allo stato desiderato
            String currentButtonText = "";
            if (!footer.getChildren().isEmpty() && footer.getChildren().get(0) instanceof Button) {
                currentButtonText = ((Button) footer.getChildren().get(0)).getText();
            }

            String expectedButtonText = "";
            if (Comanda.STATO_IN_ATTESA.equals(c.getStato())) {
                expectedButtonText = Comanda.AZIONE_INIZIA;
            } else if (Comanda.STATO_IN_PREPARAZIONE.equals(c.getStato())) {
                expectedButtonText = Comanda.STATO_PRONTO;
            }

            // Aggiorna solo se necessario per evitare di perdere il focus o il click
            if (!currentButtonText.equals(expectedButtonText)) {
                footer.getChildren().clear();
                
                if (Comanda.STATO_IN_ATTESA.equals(c.getStato())) {
                    Button btnPrepara = new Button(Comanda.AZIONE_INIZIA);
                    btnPrepara.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-cursor: hand;");
                    btnPrepara.setOnAction(e -> updateStato(c, Comanda.STATO_IN_PREPARAZIONE));
                    footer.getChildren().add(btnPrepara);
                } else if (Comanda.STATO_IN_PREPARAZIONE.equals(c.getStato())) {
                    Button btnPronto = new Button(Comanda.STATO_PRONTO);
                    btnPronto.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-cursor: hand;");
                    btnPronto.setOnAction(e -> updateStato(c, Comanda.STATO_PRONTO));
                    footer.getChildren().add(btnPronto);
                }
            }
        }
    }

    private void updateTimers() {
        for (VBox card : activeCards.values()) {
            updateCardTimer(card);
        }
    }

    private void updateCardTimer(VBox card) {
        Label lblTime = getTimerLabel(card);
        if (lblTime != null && lblTime.getUserData() instanceof java.time.LocalDateTime) {
            updateTimerLabel(lblTime, (java.time.LocalDateTime) lblTime.getUserData());
        }
    }

    private Label getTimerLabel(VBox card) {
        if (card.getChildren().isEmpty() || !(card.getChildren().get(0) instanceof HBox)) {
            return null;
        }
        HBox header = (HBox) card.getChildren().get(0);
        if (header.getChildren().isEmpty()) {
            return null;
        }
        javafx.scene.Node lastNode = header.getChildren().get(header.getChildren().size() - 1);
        return (lastNode instanceof Label) ? (Label) lastNode : null;
    }

    private void updateTimerLabel(Label lblTime, java.time.LocalDateTime orderTime) {
        java.time.Duration duration = java.time.Duration.between(orderTime, java.time.LocalDateTime.now());
        long minutes = duration.toMinutes();
        long seconds = duration.minusMinutes(minutes).getSeconds();
        lblTime.setText(String.format("%02d:%02d", minutes, seconds));

        if (minutes >= 15) {
            lblTime.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 14px;");
        } else {
            lblTime.setStyle("-fx-text-fill: #bdc3c7; -fx-font-size: 14px;");
        }
    }

    private VBox createComandaCard(Comanda c) {
        VBox card = new VBox(10);
        card.setPrefWidth(300);
        card.setStyle("-fx-background-color: #34495e; -fx-background-radius: 10; -fx-padding: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 5);");

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        int numeroTavolo = tavoloMap != null ? tavoloMap.getOrDefault(c.getIdTavolo(), 0) : c.getIdTavolo();
        Label lblTavolo = new Label("Tavolo " + numeroTavolo);
        lblTavolo.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 18px;");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label lblTime = new Label("00:00");
        lblTime.setStyle("-fx-text-fill: #bdc3c7;");
        lblTime.setUserData(c.getDataOra());

        header.getChildren().addAll(lblTavolo, spacer, lblTime);

        VBox body = new VBox(5);
        String[] prodotti = c.getProdotti().split(", ");
        for (String p : prodotti) {
            Label lblProd = new Label("• " + p);
            lblProd.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            lblProd.setWrapText(true);
            body.getChildren().add(lblProd);
        }

        if (c.getNote() != null && !c.getNote().isEmpty()) {
            Label lblNote = new Label("Note: " + c.getNote());
            lblNote.setStyle("-fx-text-fill: #f1c40f; -fx-font-style: italic;");
            lblNote.setWrapText(true);
            body.getChildren().add(lblNote);
        }

        HBox footer = new HBox(10);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(10, 0, 0, 0));

        if (Comanda.STATO_IN_ATTESA.equals(c.getStato())) {
            Button btnPrepara = new Button(Comanda.AZIONE_INIZIA);
            btnPrepara.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-cursor: hand;");
            btnPrepara.setOnAction(e -> updateStato(c, Comanda.STATO_IN_PREPARAZIONE));
            footer.getChildren().add(btnPrepara);
        } else if (Comanda.STATO_IN_PREPARAZIONE.equals(c.getStato())) {
            Button btnPronto = new Button(Comanda.STATO_PRONTO);
            btnPronto.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-cursor: hand;");
            btnPronto.setOnAction(e -> updateStato(c, Comanda.STATO_PRONTO));
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

    private void loadDrinks() {
        try {
            List<Piatto> allItems = menuDAO.getAllPiattiComplete();
            // Filter for drinks. Assuming categories.
            List<Piatto> drinks = allItems.stream()
                    .filter(p -> isDrink(p.getCategoria()))
                    .collect(Collectors.toList());

            allDrinks = FXCollections.observableArrayList(drinks);
            drinksList.setItems(allDrinks);
            drinksList.setCellFactory(param -> new DrinkCell());

        } catch (SQLException e) {
            System.err.println("Errore nel caricamento delle bevande: " + e.getMessage());
        }
    }

    private boolean isDrink(String category) {
        if (category == null) return false;
        String c = category.toLowerCase();
        return c.contains("bevande") || c.contains("vini") || c.contains("birre") || c.contains("caffè") || c.contains("bar") || c.contains("drink");
    }

    private void filterDrinks(String query) {
        if (query == null || query.isEmpty()) {
            drinksList.setItems(allDrinks);
            return;
        }

        FilteredList<Piatto> filtered = new FilteredList<>(allDrinks, p ->
                p.getNome().toLowerCase().contains(query.toLowerCase()));
        drinksList.setItems(filtered);
    }

    @FXML
    private void handleLogout() {
        stopPolling();
        try {
            FamigliaSaporiApplication.setRoot("HomeView");
        } catch (IOException e) {
            System.err.println("Errore nel ritorno alla HomeView: " + e.getMessage());
        }
    }

    private class DrinkCell extends ListCell<Piatto> {
        private HBox content;
        private Label name;
        private CheckBox availableBox;

        public DrinkCell() {
            content = new HBox(10);
            content.setAlignment(Pos.CENTER_LEFT);
            name = new Label();
            name.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            HBox.setHgrow(name, Priority.ALWAYS);

            availableBox = new CheckBox("Disponibile");
            availableBox.setStyle("-fx-text-fill: #bdc3c7;");

            content.getChildren().addAll(name, availableBox);

            availableBox.setOnAction(e -> {
                Piatto p = getItem();
                if (p != null) {
                    boolean newState = availableBox.isSelected();
                    try {
                        menuDAO.updateDisponibilita(p.getId(), newState);
                        p.setDisponibile(newState);
                    } catch (SQLException ex) {
                        System.err.println("Errore nell'aggiornamento della disponibilità: " + ex.getMessage());
                        availableBox.setSelected(!newState); // Revert
                    }
                }
            });
        }

        @Override
        protected void updateItem(Piatto item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
            } else {
                name.setText(item.getNome());
                availableBox.setSelected(item.isDisponibile());
                setGraphic(content);
            }
        }
    }
}
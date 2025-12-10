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
import java.time.format.DateTimeFormatter;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        comandaDAO = new ComandaDAO();
        tavoloDAO = new TavoloDAO();
        menuDAO = new MenuDAO();

        loadTavoliMap();
        loadComande();
        loadDrinks();
        startPolling();

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterDrinks(newValue);
        });
    }

    private void startPolling() {
        pollingTimeline = new Timeline(new KeyFrame(Duration.seconds(30), e -> {
            Platform.runLater(this::loadComande);
        }));
        pollingTimeline.setCycleCount(Timeline.INDEFINITE);
        pollingTimeline.play();
    }

    public void stopPolling() {
        if (pollingTimeline != null) {
            pollingTimeline.stop();
        }
    }

    private void loadTavoliMap() {
        try {
            List<Tavolo> tavoli = tavoloDAO.getAllTavoli();
            tavoloMap = tavoli.stream().collect(Collectors.toMap(Tavolo::getId, Tavolo::getNumero));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadComande() {
        try {
            List<Comanda> comande = comandaDAO.getComandeByStatoAndTipo("In Attesa", "Bar");
            comande.addAll(comandaDAO.getComandeByStatoAndTipo("In Preparazione", "Bar"));

            ordersContainer.getChildren().clear();

            if (comande.isEmpty()) {
                Label emptyLabel = new Label("Nessuna comanda in attesa");
                emptyLabel.setStyle("-fx-text-fill: #34495e; -fx-font-size: 18px;");
                ordersContainer.getChildren().add(emptyLabel);
                return;
            }

            for (Comanda c : comande) {
                ordersContainer.getChildren().add(createComandaCard(c));
            }
        } catch (SQLException e) {
            System.err.println("Errore durante il caricamento delle comande: " + e.getMessage());
            e.printStackTrace();
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

        String timeStr = c.getDataOra().format(DateTimeFormatter.ofPattern("HH:mm"));
        Label lblTime = new Label(timeStr);
        lblTime.setStyle("-fx-text-fill: #bdc3c7;");

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
            loadComande();
        } catch (SQLException e) {
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
                        ex.printStackTrace();
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
package famiglia.sapori.controller;

import famiglia.sapori.FamigliaSaporiApplication;
import famiglia.sapori.dao.GestoreDAO;
import famiglia.sapori.dao.MenuDAO;
import famiglia.sapori.dao.TavoloDAO;
import famiglia.sapori.dao.UtenteDAO;
import famiglia.sapori.dao.ComandaDAO;
import famiglia.sapori.dao.MagazzinoDAO;
import famiglia.sapori.dao.RicettaDAO;
import famiglia.sapori.model.Piatto;
import famiglia.sapori.model.ProdottoMagazzino;
import famiglia.sapori.model.Tavolo;
import famiglia.sapori.model.Utente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;

public class GestoreController implements Initializable {

    // --- Common ---
    @FXML
    private Label lblUtente;
    @FXML
    private TabPane tabPaneGestore;

    // --- Tab Menu ---
    @FXML
    private TableView<Piatto> tblMenu;
    @FXML
    private TableColumn<Piatto, String> colNomePiatto;
    @FXML
    private TableColumn<Piatto, String> colCategoriaPiatto;
    @FXML
    private TableColumn<Piatto, Double> colPrezzoPiatto;
    @FXML
    private TableColumn<Piatto, Boolean> colDispPiatto;

    @FXML
    private TextField txtNomePiatto;
    @FXML
    private ComboBox<String> comboCategoria;
    @FXML
    private TextField txtPrezzoPiatto;
    @FXML
    private TextArea txtDescrizionePiatto;
    @FXML
    private TextField txtAllergeni;
    @FXML
    private CheckBox chkDisponibile;

    // --- Tab Magazzino ---
    @FXML private TableView<ProdottoMagazzino> tblMagazzino;
    @FXML private TableColumn<ProdottoMagazzino, String> colProdotto;
    @FXML private TableColumn<ProdottoMagazzino, Double> colQuantita;
    @FXML private TableColumn<ProdottoMagazzino, String> colUnitaMisura;
    @FXML private TableColumn<ProdottoMagazzino, Double> colSoglia;

    @FXML private TextField txtRicercaMagazzino; // Search Bar
    @FXML private TextField txtProdottoMagazzino;
    @FXML private TextField txtQuantitaMagazzino;
    @FXML private TextField txtUnitaMisura;
    @FXML private TextField txtSogliaMinima;

    private ObservableList<ProdottoMagazzino> masterMagazzinoData = FXCollections.observableArrayList();

    // --- Tab Personale ---
    @FXML
    private TableView<Utente> tblUtenti;
    @FXML
    private TableColumn<Utente, String> colNomeUtente;
    @FXML
    private TableColumn<Utente, String> colUsername;
    @FXML
    private TableColumn<Utente, String> colRuolo;

    @FXML
    private TextField txtNomeUtente;
    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private ComboBox<String> comboRuolo;

    // --- Tab Tavoli ---
    @FXML
    private TableView<Tavolo> tblTavoli;
    @FXML
    private TableColumn<Tavolo, Integer> colNumeroTavolo;
    @FXML
    private TableColumn<Tavolo, Integer> colPostiTavolo;
    @FXML
    private TableColumn<Tavolo, String> colStatoTavolo;

    @FXML
    private TextField txtNumeroTavolo;
    @FXML
    private Spinner<Integer> spinPostiTavolo;
    @FXML
    private TextArea txtNoteTavolo;

    // --- Tab Statistiche ---
    @FXML
    private PieChart pieBestSellers;
    @FXML
    private Label lblIncassoTotale;

    // DAOs
    private MenuDAO menuDAO;
    private UtenteDAO utenteDAO;
    private TavoloDAO tavoloDAO;
    private GestoreDAO gestoreDAO;
    private MagazzinoDAO magazzinoDAO;
    private RicettaDAO ricettaDAO;

    // Selection tracking
    private Piatto selectedPiatto;
    private Utente selectedUtente;
    private Tavolo selectedTavolo;
    private ProdottoMagazzino selectedProdotto;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        menuDAO = new MenuDAO();
        utenteDAO = new UtenteDAO();
        tavoloDAO = new TavoloDAO();
        gestoreDAO = new GestoreDAO();
        magazzinoDAO = new MagazzinoDAO();
        ricettaDAO = new RicettaDAO();

        // Elimina automaticamente le comande del giorno precedente all'avvio
        try {
            ComandaDAO comandaDAO = new ComandaDAO();
            comandaDAO.deleteOldComande();
        } catch (SQLException e) {
            System.err.println("Errore nella pulizia delle comande vecchie: " + e.getMessage());
        }

        if (FamigliaSaporiApplication.getCurrentUser() != null) {
            lblUtente.setText("Gestore: " + FamigliaSaporiApplication.getCurrentUser().getNome());
        }

        initMenuTab();
        initMagazzinoTab();
        initPersonaleTab();
        initTavoliTab();
        initStatsTab();
    }

    // ==================== MENU TAB ====================
    private void initMenuTab() {
        colNomePiatto.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colCategoriaPiatto.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colPrezzoPiatto.setCellValueFactory(new PropertyValueFactory<>("prezzo"));
        setupPrezzoColumn();

        colDispPiatto.setCellValueFactory(new PropertyValueFactory<>("disponibile"));
        setupDisponibileColumn();

        comboCategoria.setItems(
                FXCollections.observableArrayList("Antipasti", "Primi", "Secondi", "Contorni", "Dolci", "Bevande"));

        setupMenuSelectionListener();

        loadMenuData();
    }

    private void setupPrezzoColumn() {
        colPrezzoPiatto.setCellFactory(column -> new TableCell<Piatto, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("\u20AC %.2f", item));
                }
            }
        });
    }

    private void setupDisponibileColumn() {
        colDispPiatto.setCellFactory(column -> new TableCell<Piatto, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    if (item) {
                        setText("Sì");
                        setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;"); // Verde
                    } else {
                        setText("No");
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;"); // Rosso
                    }
                }
            }
        });
    }

    private void setupMenuSelectionListener() {
        tblMenu.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedPiatto = newVal;
            if (newVal != null) {
                fillMenuFields(newVal);
            } else {
                clearMenuFields();
            }
        });
    }

    private void fillMenuFields(Piatto piatto) {
        txtNomePiatto.setText(piatto.getNome());
        comboCategoria.setValue(piatto.getCategoria());
        txtPrezzoPiatto.setText(String.valueOf(piatto.getPrezzo()));
        txtDescrizionePiatto.setText(piatto.getDescrizione());
        txtAllergeni.setText(piatto.getAllergeni());
        chkDisponibile.setSelected(piatto.isDisponibile());
    }

    private void loadMenuData() {
        try {
            tblMenu.setItems(FXCollections.observableArrayList(menuDAO.getAllPiattiComplete()));
            tblMenu.refresh();
        } catch (SQLException e) {
            showError("Errore caricamento menu: " + e.getMessage());
        }
    }

    private void clearMenuFields() {
        txtNomePiatto.clear();
        comboCategoria.setValue(null);
        txtPrezzoPiatto.clear();
        txtDescrizionePiatto.clear();
        txtAllergeni.clear();
        chkDisponibile.setSelected(true);
        selectedPiatto = null;
    }

    @FXML
    private void handleNuovoPiatto() {
        tblMenu.getSelectionModel().clearSelection();
        clearMenuFields();
    }

    @FXML
    private void handleSalvaPiatto() {
        try {
            String nome = txtNomePiatto.getText();
            String cat = comboCategoria.getValue();
            double prezzo = Double.parseDouble(txtPrezzoPiatto.getText());
            String desc = txtDescrizionePiatto.getText();
            String all = txtAllergeni.getText();
            boolean disp = chkDisponibile.isSelected();

            if (selectedPiatto == null) {
                // Insert
                Piatto p = new Piatto(0, nome, desc, prezzo, cat, disp, all);
                menuDAO.insertPiatto(p);
            } else {
                // Update
                Piatto p = new Piatto(selectedPiatto.getId(), nome, desc, prezzo, cat, disp, all);
                menuDAO.updatePiatto(p);
            }
            loadMenuData();
            handleNuovoPiatto();
        } catch (NumberFormatException e) {
            showError("Prezzo non valido");
        } catch (SQLException e) {
            showError("Errore salvataggio piatto: " + e.getMessage());
        }
    }

    @FXML
    private void handleGestisciIngredienti() {
        if (selectedPiatto == null) {
            showError("Seleziona un piatto prima di gestire gli ingredienti.");
            return;
        }

        // Create Dialog
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Ingredienti per: " + selectedPiatto.getNome());
        dialog.setHeaderText("Gestisci la ricetta del piatto (Dosi per 1 porzione)");

        // Add buttons
        ButtonType closeButtonType = new ButtonType("Chiudi", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(closeButtonType);

        // Layout
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        // Table Ingredients
        TableView<Map.Entry<ProdottoMagazzino, Double>> tblIngredienti = new TableView<>();
        TableColumn<Map.Entry<ProdottoMagazzino, Double>, String> colIngrName = new TableColumn<>("Ingrediente");
        colIngrName.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getKey().getProdotto()));
        
        TableColumn<Map.Entry<ProdottoMagazzino, Double>, Double> colIngrQta = new TableColumn<>("Qtà");
        colIngrQta.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getValue()));
        colIngrQta.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", item));
                }
            }
        });

        TableColumn<Map.Entry<ProdottoMagazzino, Double>, String> colIngrUM = new TableColumn<>("U.M.");
        colIngrUM.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getKey().getUnitaMisura()));

        tblIngredienti.getColumns().addAll(colIngrName, colIngrQta, colIngrUM);
        tblIngredienti.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // Load data helper
        Runnable loadData = () -> {
            try {
                Map<ProdottoMagazzino, Double> map = ricettaDAO.getIngredienti(selectedPiatto.getId());
                tblIngredienti.setItems(FXCollections.observableArrayList(map.entrySet()));
            } catch (SQLException e) {
                showError("Errore caricamento ingredienti: " + e.getMessage());
            }
        };
        loadData.run();

        // Add new ingredient section
        javafx.scene.layout.HBox addBox = new javafx.scene.layout.HBox(10);
        ComboBox<ProdottoMagazzino> cmbProdotti = new ComboBox<>();
        try {
            cmbProdotti.setItems(FXCollections.observableArrayList(magazzinoDAO.getAllProdotti()));
        } catch (SQLException e) { e.printStackTrace(); }
        
        TextField txtQta = new TextField();
        txtQta.setPromptText("Qtà");
        txtQta.setPrefWidth(80);
        
        Button btnAdd = new Button("Aggiungi / Aggiorna");
        btnAdd.setOnAction(e -> {
            ProdottoMagazzino p = cmbProdotti.getValue();
            if (p == null || txtQta.getText().isEmpty()) return;
            try {
                double q = Double.parseDouble(txtQta.getText());
                ricettaDAO.addIngrediente(selectedPiatto.getId(), p.getId(), q);
                loadData.run();
                txtQta.clear();
            } catch (Exception ex) {
                showError("Dati non validi");
            }
        });

        // Remove button
        Button btnRem = new Button("Rimuovi Selezionato");
        btnRem.setOnAction(e -> {
            var sel = tblIngredienti.getSelectionModel().getSelectedItem();
            if (sel != null) {
                 try {
                    ricettaDAO.removeIngrediente(selectedPiatto.getId(), sel.getKey().getId());
                    loadData.run();
                 } catch (SQLException ex) { showError(ex.getMessage()); }
            }
        });

        addBox.getChildren().addAll(cmbProdotti, txtQta, btnAdd);
        layout.getChildren().addAll(tblIngredienti, addBox, btnRem);
        
        dialog.getDialogPane().setContent(layout);
        dialog.showAndWait();
    }

    @FXML
    private void handleEliminaPiatto() {
        if (selectedPiatto != null) {
            try {
                menuDAO.deletePiatto(selectedPiatto.getId());
                loadMenuData();
                handleNuovoPiatto();
            } catch (SQLException e) {
                showError("Errore eliminazione piatto: " + e.getMessage());
            }
        }
    }

    // ==================== MAGAZZINO TAB ====================
    private void initMagazzinoTab() {
        colProdotto.setCellValueFactory(new PropertyValueFactory<>("prodotto"));
        colQuantita.setCellValueFactory(new PropertyValueFactory<>("quantita"));
        colUnitaMisura.setCellValueFactory(new PropertyValueFactory<>("unitaMisura"));
        colSoglia.setCellValueFactory(new PropertyValueFactory<>("sogliaMinima"));

        // Format decimal columns (limit to 2 decimal places)
        colQuantita.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", item));
                }
            }
        });
        colSoglia.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", item));
                }
            }
        });

        // Initialize FilteredList and SortedList for search functionality
        FilteredList<ProdottoMagazzino> filteredData = new FilteredList<>(masterMagazzinoData, p -> true);

        txtRicercaMagazzino.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(prodotto -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return prodotto.getProdotto().toLowerCase().contains(lowerCaseFilter);
            });
        });

        SortedList<ProdottoMagazzino> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tblMagazzino.comparatorProperty());
        tblMagazzino.setItems(sortedData);

        tblMagazzino.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedProdotto = newVal;
            if (newVal != null) {
                fillMagazzinoFields(newVal);
            } else {
                clearMagazzinoFields();
            }
        });

        loadMagazzinoData();
    }

    private void fillMagazzinoFields(ProdottoMagazzino p) {
        txtProdottoMagazzino.setText(p.getProdotto());
        txtQuantitaMagazzino.setText(String.valueOf(p.getQuantita()));
        txtUnitaMisura.setText(p.getUnitaMisura());
        txtSogliaMinima.setText(String.valueOf(p.getSogliaMinima()));
    }

    private void loadMagazzinoData() {
        try {
            masterMagazzinoData.setAll(magazzinoDAO.getAllProdotti());
            // Table items are already bound to SortedList wrapping FilteredList wrapping masterMagazzinoData
            tblMagazzino.refresh();
        } catch (SQLException e) {
            showError("Errore caricamento magazzino: " + e.getMessage());
        }
    }

    private void clearMagazzinoFields() {
        txtProdottoMagazzino.clear();
        txtQuantitaMagazzino.clear();
        txtUnitaMisura.clear();
        txtSogliaMinima.clear();
        selectedProdotto = null;
    }

    @FXML
    private void handleNuovoProdotto() {
        tblMagazzino.getSelectionModel().clearSelection();
        clearMagazzinoFields();
    }

    @FXML
    private void handleSalvaProdotto() {
        try {
            String prod = txtProdottoMagazzino.getText();
            double qta = Double.parseDouble(txtQuantitaMagazzino.getText());
            String um = txtUnitaMisura.getText();
            double soglia = Double.parseDouble(txtSogliaMinima.getText());

            if (selectedProdotto == null) {
                ProdottoMagazzino p = new ProdottoMagazzino(0, prod, qta, um, soglia);
                magazzinoDAO.insertProdotto(p);
            } else {
                ProdottoMagazzino p = new ProdottoMagazzino(selectedProdotto.getId(), prod, qta, um, soglia);
                magazzinoDAO.updateProdotto(p);
            }
            loadMagazzinoData();
            handleNuovoProdotto();
        } catch (NumberFormatException e) {
            showError("Valori numerici non validi");
        } catch (SQLException e) {
            showError("Errore salvataggio prodotto: " + e.getMessage());
        }
    }

    @FXML
    private void handleEliminaProdotto() {
        if (selectedProdotto != null) {
            try {
                magazzinoDAO.deleteProdotto(selectedProdotto.getId());
                loadMagazzinoData();
                handleNuovoProdotto();
            } catch (SQLException e) {
                showError("Errore eliminazione prodotto: " + e.getMessage());
            }
        }
    }

    // ==================== PERSONALE TAB ====================
    private void initPersonaleTab() {
        colNomeUtente.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colRuolo.setCellValueFactory(new PropertyValueFactory<>("ruolo"));

        comboRuolo.setItems(FXCollections.observableArrayList("Gestore", "Cameriere"));

        tblUtenti.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedUtente = newVal;
            if (newVal != null) {
                txtNomeUtente.setText(newVal.getNome());
                txtUsername.setText(newVal.getUsername());
                // Non mostrare la password (hash) in chiaro
                txtPassword.setText("");
                comboRuolo.setValue(newVal.getRuolo());
            } else {
                clearUtenteFields();
            }
        });

        loadUtentiData();
    }

    private void loadUtentiData() {
        try {
            tblUtenti.setItems(FXCollections.observableArrayList(utenteDAO.getAllUtenti()));
            tblUtenti.refresh();
        } catch (SQLException e) {
            showError("Errore caricamento utenti: " + e.getMessage());
        }
    }

    private void clearUtenteFields() {
        txtNomeUtente.clear();
        txtUsername.clear();
        txtPassword.clear();
        comboRuolo.setValue(null);
        selectedUtente = null;
    }

    @FXML
    private void handleNuovoUtente() {
        tblUtenti.getSelectionModel().clearSelection();
        clearUtenteFields();
    }

    @FXML
    private void handleSalvaUtente() {
        try {
            String nome = txtNomeUtente.getText();
            String user = txtUsername.getText();
            String pass = txtPassword.getText();
            String ruolo = comboRuolo.getValue();

            if (selectedUtente == null) {
                // Nuovo utente: la password è obbligatoria
                if (pass.isEmpty()) {
                    showError("La password è obbligatoria per i nuovi utenti");
                    return;
                }
                String hashedPassword = famiglia.sapori.util.PasswordUtil.hashPassword(pass);
                Utente u = new Utente(0, nome, user, hashedPassword, ruolo);
                utenteDAO.insertUtente(u);
            } else {
                // Aggiornamento: se la password è vuota, mantieni la vecchia (già hashata o in chiaro se legacy)
                // Se la password non è vuota, hashala
                String passwordToSave = pass.isEmpty() ? selectedUtente.getPassword() : famiglia.sapori.util.PasswordUtil.hashPassword(pass);
                
                Utente u = new Utente(selectedUtente.getId(), nome, user, passwordToSave, ruolo);
                utenteDAO.updateUtente(u);
            }
            loadUtentiData();
            handleNuovoUtente();
        } catch (SQLException e) {
            showError("Errore salvataggio utente: " + e.getMessage());
        }
    }

    @FXML
    private void handleEliminaUtente() {
        if (selectedUtente != null) {
            try {
                utenteDAO.deleteUtente(selectedUtente.getId());
                loadUtentiData();
                handleNuovoUtente();
            } catch (SQLException e) {
                showError("Errore eliminazione utente: " + e.getMessage());
            }
        }
    }

    // ==================== TAVOLI TAB ====================
    private void initTavoliTab() {
        colNumeroTavolo.setCellValueFactory(new PropertyValueFactory<>("numero"));
        colPostiTavolo.setCellValueFactory(new PropertyValueFactory<>("posti"));
        colStatoTavolo.setCellValueFactory(new PropertyValueFactory<>("stato"));

        // Formattazione Stato Tavolo
        colStatoTavolo.setCellFactory(column -> new TableCell<Tavolo, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (Tavolo.STATO_LIBERO.equalsIgnoreCase(item)) {
                        setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;"); // Verde
                    } else if (Tavolo.STATO_OCCUPATO.equalsIgnoreCase(item)) {
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;"); // Rosso
                    } else {
                        setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;"); // Arancione
                    }
                }
            }
        });

        spinPostiTavolo.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 4));

        tblTavoli.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedTavolo = newVal;
            if (newVal != null) {
                txtNumeroTavolo.setText(String.valueOf(newVal.getNumero()));
                spinPostiTavolo.getValueFactory().setValue(newVal.getPosti());
                txtNoteTavolo.setText(newVal.getNote());
            } else {
                clearTavoloFields();
            }
        });

        loadTavoliData();
    }

    private void loadTavoliData() {
        try {
            tblTavoli.setItems(FXCollections.observableArrayList(tavoloDAO.getAllTavoli()));
            tblTavoli.refresh();
        } catch (SQLException e) {
            showError("Errore caricamento tavoli: " + e.getMessage());
        }
    }

    private void clearTavoloFields() {
        txtNumeroTavolo.clear();
        spinPostiTavolo.getValueFactory().setValue(4);
        txtNoteTavolo.clear();
        selectedTavolo = null;
    }

    @FXML
    private void handleNuovoTavolo() {
        tblTavoli.getSelectionModel().clearSelection();
        clearTavoloFields();
    }

    @FXML
    private void handleSalvaTavolo() {
        try {
            int numero = Integer.parseInt(txtNumeroTavolo.getText());
            int posti = spinPostiTavolo.getValue();
            String note = txtNoteTavolo.getText();
            String stato = (selectedTavolo != null) ? selectedTavolo.getStato() : Tavolo.STATO_LIBERO;

            if (selectedTavolo == null) {
                Tavolo t = new Tavolo(0, numero, stato, posti, note);
                tavoloDAO.insertTavolo(t);
            } else {
                Tavolo t = new Tavolo(selectedTavolo.getId(), numero, stato, posti, note);
                tavoloDAO.updateTavolo(t);
            }
            loadTavoliData();
            handleNuovoTavolo();
        } catch (NumberFormatException e) {
            showError("Numero tavolo non valido");
        } catch (SQLException e) {
            showError("Errore salvataggio tavolo: " + e.getMessage());
        }
    }

    @FXML
    private void handleEliminaTavolo() {
        if (selectedTavolo != null) {
            try {
                tavoloDAO.deleteTavolo(selectedTavolo.getId());
                loadTavoliData();
                handleNuovoTavolo();
            } catch (SQLException e) {
                showError("Errore eliminazione tavolo: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleResetTavolo() {
        if (selectedTavolo != null) {
            try {
                tavoloDAO.updateStatoTavolo(selectedTavolo.getId(), Tavolo.STATO_LIBERO);
                loadTavoliData();
            } catch (SQLException e) {
                showError("Errore reset tavolo: " + e.getMessage());
            }
        }
    }

    // ==================== STATISTICHE TAB ====================
    private void initStatsTab() {
        handleRefreshStats();
    }

    @FXML
    private void handleRefreshStats() {
        try {
            // Best Sellers (ultima settimana)
            Map<String, Integer> bestSellers = gestoreDAO.getBestSellers();
            ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
            for (Map.Entry<String, Integer> entry : bestSellers.entrySet()) {
                pieData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
            }
            pieBestSellers.setData(pieData);

            // Incassi: Giornaliero e Settimanale
            double dailyIncome = gestoreDAO.calculateDailyIncome();
            double weeklyIncome = gestoreDAO.calculateWeeklyIncome();
            lblIncassoTotale
                    .setText(String.format("Oggi: \u20AC %.2f\nUltimi 7gg: \u20AC %.2f", dailyIncome, weeklyIncome));

        } catch (SQLException e) {
            showError("Errore caricamento statistiche: " + e.getMessage());
        }
    }

    // ==================== COMMON ====================
    @FXML
    private void handleLogout() {
        try {
            FamigliaSaporiApplication.setRoot("LoginView");
        } catch (IOException e) {
            System.err.println("Errore nel ritorno alla LoginView: " + e.getMessage());
        }
    }

    @FXML
    private void handleGestionePrenotazioni() {
        try {
            FamigliaSaporiApplication.setRoot("PrenotazioneView");
        } catch (IOException e) {
            System.err.println("Errore nel caricamento della vista prenotazioni: " + e.getMessage());
            showError("Impossibile caricare la vista prenotazioni: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
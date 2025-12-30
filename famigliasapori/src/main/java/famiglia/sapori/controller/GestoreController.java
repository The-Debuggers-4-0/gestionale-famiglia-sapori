package famiglia.sapori.controller;

import famiglia.sapori.FamigliaSaporiApplication;
import famiglia.sapori.dao.GestoreDAO;
import famiglia.sapori.dao.MenuDAO;
import famiglia.sapori.dao.TavoloDAO;
import famiglia.sapori.dao.UtenteDAO;
import famiglia.sapori.dao.ComandaDAO;
import famiglia.sapori.model.Piatto;
import famiglia.sapori.model.Tavolo;
import famiglia.sapori.model.Utente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Map;
import java.util.ResourceBundle;

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

    // Selection tracking
    private Piatto selectedPiatto;
    private Utente selectedUtente;
    private Tavolo selectedTavolo;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        menuDAO = new MenuDAO();
        utenteDAO = new UtenteDAO();
        tavoloDAO = new TavoloDAO();
        gestoreDAO = new GestoreDAO();

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
        initPersonaleTab();
        initTavoliTab();
        initStatsTab();
    }

    // ==================== MENU TAB ====================
    private void initMenuTab() {
        colNomePiatto.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colCategoriaPiatto.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colPrezzoPiatto.setCellValueFactory(new PropertyValueFactory<>("prezzo"));

        // Formattazione Prezzo
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

        colDispPiatto.setCellValueFactory(new PropertyValueFactory<>("disponibile"));
        // Formattazione Disponibilità (Colore)
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

        comboCategoria.setItems(
                FXCollections.observableArrayList("Antipasti", "Primi", "Secondi", "Contorni", "Dolci", "Bevande"));

        tblMenu.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedPiatto = newVal;
            if (newVal != null) {
                txtNomePiatto.setText(newVal.getNome());
                comboCategoria.setValue(newVal.getCategoria());
                txtPrezzoPiatto.setText(String.valueOf(newVal.getPrezzo()));
                txtDescrizionePiatto.setText(newVal.getDescrizione());
                txtAllergeni.setText(newVal.getAllergeni());
                chkDisponibile.setSelected(newVal.isDisponibile());
            } else {
                clearMenuFields();
            }
        });

        loadMenuData();
    }

    private void loadMenuData() {
        try {
            tblMenu.setItems(FXCollections.observableArrayList(menuDAO.getAllPiattiComplete()));
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
                txtPassword.setText(newVal.getPassword());
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
                Utente u = new Utente(0, nome, user, pass, ruolo);
                utenteDAO.insertUtente(u);
            } else {
                Utente u = new Utente(selectedUtente.getId(), nome, user, pass, ruolo);
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
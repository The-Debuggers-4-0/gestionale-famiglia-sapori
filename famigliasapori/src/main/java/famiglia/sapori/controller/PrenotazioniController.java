package famiglia.sapori.controller;

import famiglia.sapori.FamigliaSaporiApplication;
import famiglia.sapori.dao.PrenotazioneDAO;
import famiglia.sapori.dao.TavoloDAO;
import famiglia.sapori.model.Prenotazione;
import famiglia.sapori.model.Tavolo;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class PrenotazioniController implements Initializable {

    @FXML
    private TableView<Prenotazione> tablePrenotazioni;
    @FXML
    private TableColumn<Prenotazione, String> colOrario;
    @FXML
    private TableColumn<Prenotazione, String> colNome;
    @FXML
    private TableColumn<Prenotazione, String> colPax;
    @FXML
    private TableColumn<Prenotazione, String> colTavolo;
    @FXML
    private TableColumn<Prenotazione, String> colTel;
    @FXML
    private TableColumn<Prenotazione, String> colNote;

    @FXML
    private TextField txtNome;
    @FXML
    private TextField txtTelefono;
    @FXML
    private Spinner<Integer> spinPax;
    @FXML
    private DatePicker datePicker;
    @FXML
    private DatePicker filterDatePicker;
    @FXML
    private TextField txtOra; // Formato HH:mm
    @FXML
    private TextArea txtNote;
    @FXML
    private TextField txtCerca;
    @FXML
    private ComboBox<Tavolo> comboTavolo;
    @FXML
    private Button btnSalva;
    @FXML
    private Button btnAnnulla;

    private PrenotazioneDAO prenotazioneDAO;
    private TavoloDAO tavoloDAO;
    private ObservableList<Prenotazione> masterData = FXCollections.observableArrayList();
    private Map<Integer, Integer> tavoloMap = new HashMap<>();
    private Integer editingReservationId = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        prenotazioneDAO = new PrenotazioneDAO();
        tavoloDAO = new TavoloDAO();

        // Configurazione Colonne Tabella
        colNome.setCellValueFactory(new PropertyValueFactory<>("nomeCliente"));
        colPax.setCellValueFactory(new PropertyValueFactory<>("numeroPersone"));
        colTel.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colNote.setCellValueFactory(new PropertyValueFactory<>("note"));

        colTavolo.setCellValueFactory(cellData -> {
            Integer idTavolo = cellData.getValue().getIdTavolo();
            if (idTavolo == null)
                return new SimpleStringProperty("-");
            Integer num = tavoloMap.get(idTavolo);
            return new SimpleStringProperty(num != null ? "Tavolo " + num : "?");
        });

        // Formattazione data personalizzata per la colonna
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM HH:mm");
        colOrario.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getDataOra().format(formatter)));

        // Configurazione input default
        spinPax.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 2));
        datePicker.setValue(LocalDate.now());
        filterDatePicker.setValue(LocalDate.now());
        txtOra.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));

        loadTavoli();
        loadPrenotazioni();

        // Listener per la ricerca
        txtCerca.textProperty().addListener((observable, oldValue, newValue) -> updateFilter());

        // Listener per il filtro data
        filterDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> updateFilter());

        // Listener per il cambio data (form creazione)
        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadTavoli();
            }
        });

        // Listener selezione tabella per modifica
        tablePrenotazioni.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                populateForm(newVal);
            }
        });
    }

    private void populateForm(Prenotazione p) {
        editingReservationId = p.getId();
        txtNome.setText(p.getNomeCliente());
        txtTelefono.setText(p.getTelefono());
        spinPax.getValueFactory().setValue(p.getNumeroPersone());
        txtNote.setText(p.getNote());

        
        boolean dateChanged = !p.getDataOra().toLocalDate().equals(datePicker.getValue());
        datePicker.setValue(p.getDataOra().toLocalDate());
        
        // Se la data non è cambiata, il listener del datePicker non scatta,
        // quindi dobbiamo ricaricare i tavoli manualmente per includere quello della prenotazione corrente.
        if (!dateChanged) {
            loadTavoli();
        }

        txtOra.setText(p.getDataOra().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));

        // Seleziona tavolo
        comboTavolo.setValue(null); // Reset prima
        if (p.getIdTavolo() != null) {
            // Dobbiamo trovare l'oggetto Tavolo corrispondente nella combo
            // Nota: la combo potrebbe non contenere il tavolo se è filtrato via perché
            // "occupato",
            // ma se stiamo modificando QUESTA prenotazione, quel tavolo dovrebbe essere
            // valido per noi.
            // Per semplicità, proviamo a selezionarlo se c'è.
            for (Tavolo t : comboTavolo.getItems()) {
                if (t.getId() == p.getIdTavolo()) {
                    comboTavolo.setValue(t);
                    break;
                }
            }
        }

        btnSalva.setText("Aggiorna Prenotazione");
        btnAnnulla.setVisible(true);
        btnAnnulla.setManaged(true);
    }

    private void loadPrenotazioni() {
        try {
            masterData.setAll(prenotazioneDAO.getAllPrenotazioni());
            updateFilter();
        } catch (SQLException e) {
            System.err.println("Errore nel caricamento delle prenotazioni: " + e.getMessage());
        }
    }

    private void loadTavoli() {
        try {
            List<Tavolo> tavoli = tavoloDAO.getAllTavoli();
            tavoloMap.clear();
            for (Tavolo t : tavoli) {
                tavoloMap.put(t.getId(), t.getNumero());
            }

            // Filtra i tavoli già prenotati per la data selezionata
            LocalDate selectedDate = datePicker.getValue();
            if (selectedDate != null) {
                List<Integer> reservedIds = prenotazioneDAO.getReservedTableIdsForDate(selectedDate);
                
                // Se la data è oggi, consideriamo anche i tavoli attualmente occupati
                if (selectedDate.equals(LocalDate.now())) {
                    for (Tavolo t : tavoli) {
                        if ("Occupato".equalsIgnoreCase(t.getStato())) {
                            reservedIds.add(t.getId());
                        }
                    }
                }

                // Se stiamo modificando una prenotazione e la data coincide, 
                // rimuoviamo il tavolo attuale dalla lista dei "già prenotati" per permettere di mantenerlo.
                if (editingReservationId != null) {
                    Prenotazione current = masterData.stream()
                        .filter(p -> p.getId() == editingReservationId)
                        .findFirst()
                        .orElse(null);
                    
                    if (current != null && current.getIdTavolo() != null && current.getDataOra().toLocalDate().equals(selectedDate)) {
                        Integer idTavolo = current.getIdTavolo();
                        reservedIds.removeIf(id -> id.equals(idTavolo));
                    }
                }

                tavoli.removeIf(t -> reservedIds.contains(t.getId()));
            }

            comboTavolo.getItems().setAll(tavoli);
            comboTavolo.setConverter(new StringConverter<Tavolo>() {
                @Override
                public String toString(Tavolo t) {
                    if (t == null)
                        return null;
                    return "Tavolo " + t.getNumero() + " (" + t.getPosti() + " posti)";
                }

                @Override
                public Tavolo fromString(String string) {
                    return null;
                }
            });
        } catch (SQLException e) {
            System.err.println("Errore nel caricamento dei tavoli: " + e.getMessage());
        }
    }

    private void updateFilter() {
        String query = txtCerca.getText();
        LocalDate filterDate = filterDatePicker.getValue();

        FilteredList<Prenotazione> filtered = new FilteredList<>(masterData, p -> {
            boolean matchDate = true;
            if (filterDate != null) {
                matchDate = p.getDataOra().toLocalDate().equals(filterDate);
            }

            boolean matchText = true;
            if (query != null && !query.isEmpty()) {
                String lower = query.toLowerCase();
                matchText = p.getNomeCliente().toLowerCase().contains(lower);
            }

            return matchDate && matchText;
        });
        tablePrenotazioni.setItems(filtered);
    }

    private void filterList(String query) {
        // Deprecated, replaced by updateFilter
        updateFilter();
    }

    @FXML
    private void handleSalva() {
        try {
            String nome = txtNome.getText();
            if (nome.isEmpty()) {
                showAlert("Errore", "Il nome è obbligatorio");
                return;
            }

            // Parsing Data e Ora
            LocalDate date = datePicker.getValue();
            String[] timeParts = txtOra.getText().split(":");
            LocalTime time = LocalTime.of(Integer.parseInt(timeParts[0]), Integer.parseInt(timeParts[1]));
            LocalDateTime dataOra = LocalDateTime.of(date, time);

            Tavolo selectedTavolo = comboTavolo.getValue();

            if (selectedTavolo == null) {
                showAlert("Errore", "Devi selezionare un tavolo per salvare la prenotazione.");
                return;
            }

            if (selectedTavolo != null && spinPax.getValue() > selectedTavolo.getPosti()) {
                showAlert("Attenzione", "Il numero di persone (" + spinPax.getValue() + ") supera i posti del tavolo ("
                        + selectedTavolo.getPosti() + ").");
                return;
            }

            Integer idTavolo = selectedTavolo.getId();

            Prenotazione p = new Prenotazione(
                    editingReservationId != null ? editingReservationId : 0,
                    nome, txtTelefono.getText(), spinPax.getValue(), dataOra, txtNote.getText(), idTavolo);

            if (editingReservationId != null) {
                prenotazioneDAO.updatePrenotazione(p);
                showAlert("Successo", "Prenotazione aggiornata correttamente.");
            } else {
                prenotazioneDAO.insertPrenotazione(p);
                showAlert("Successo", "Prenotazione creata correttamente.");
            }

            if (selectedTavolo != null) {
                // Non aggiorniamo lo stato del tavolo nel DB, è calcolato dinamicamente
                // tavoloDAO.updateStatoTavolo(selectedTavolo.getId(), "Prenotato");
                loadTavoli(); // Ricarica stato tavoli
            }

            loadPrenotazioni();
            clearForm();
            tablePrenotazioni.getSelectionModel().clearSelection();

        } catch (Exception e) {
            showAlert("Errore", "Controlla i dati inseriti (es. orario HH:mm).");
            System.err.println("Errore nel salvataggio della prenotazione: " + e.getMessage());
        }
    }

    @FXML
    private void handleAnnulla() {
        clearForm();
        tablePrenotazioni.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleElimina() {
        Prenotazione selected = tablePrenotazioni.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                // Se la prenotazione ha un tavolo associato, lo liberiamo
                if (selected.getIdTavolo() != null) {
                    // Non aggiorniamo lo stato del tavolo nel DB
                    // tavoloDAO.updateStatoTavolo(selected.getIdTavolo(), "Libero");
                }

                prenotazioneDAO.deletePrenotazione(selected.getId());

                loadPrenotazioni();
                loadTavoli(); // Aggiorna lo stato dei tavoli nella UI
            } catch (SQLException e) {
                System.err.println("Errore nell'eliminazione della prenotazione: " + e.getMessage());
            }
        } else {
            showAlert("Attenzione", "Nessuna prenotazione selezionata");
        }
    }

    @FXML
    private void handleBack() {
        try {
            // Controlla il ruolo dell'utente corrente per decidere dove tornare
            if (FamigliaSaporiApplication.currentUser != null &&
                    "Gestore".equalsIgnoreCase(FamigliaSaporiApplication.currentUser.getRuolo())) {
                FamigliaSaporiApplication.setRoot("GestoreView");
            } else {
                FamigliaSaporiApplication.setRoot("SalaView");
            }
        } catch (IOException e) {
            System.err.println("Errore nel ritorno alla vista precedente: " + e.getMessage());
        }
    }

    private void clearForm() {
        editingReservationId = null;
        txtNome.clear();
        txtTelefono.clear();
        txtNote.clear();
        spinPax.getValueFactory().setValue(2);
        comboTavolo.setValue(null);

        btnSalva.setText("Registra Prenotazione");
        btnAnnulla.setVisible(false);
        btnAnnulla.setManaged(false);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
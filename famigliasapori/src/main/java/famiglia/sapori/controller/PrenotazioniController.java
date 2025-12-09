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
import java.util.List;
import java.util.ResourceBundle;
 
public class PrenotazioniController implements Initializable {
 
    @FXML private TableView<Prenotazione> tablePrenotazioni;
    @FXML private TableColumn<Prenotazione, String> colOrario;
    @FXML private TableColumn<Prenotazione, String> colNome;
    @FXML private TableColumn<Prenotazione, String> colPax;
    @FXML private TableColumn<Prenotazione, String> colTel;
    @FXML private TableColumn<Prenotazione, String> colNote;
 
    @FXML private TextField txtNome;
    @FXML private TextField txtTelefono;
    @FXML private Spinner<Integer> spinPax;
    @FXML private DatePicker datePicker;
    @FXML private TextField txtOra; // Formato HH:mm
    @FXML private TextArea txtNote;
    @FXML private TextField txtCerca;
    @FXML private ComboBox<Tavolo> comboTavolo;
 
    private PrenotazioneDAO prenotazioneDAO;
    private TavoloDAO tavoloDAO;
    private ObservableList<Prenotazione> masterData = FXCollections.observableArrayList();
 
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        prenotazioneDAO = new PrenotazioneDAO();
        tavoloDAO = new TavoloDAO();
       
        // Configurazione Colonne Tabella
        colNome.setCellValueFactory(new PropertyValueFactory<>("nomeCliente"));
        colPax.setCellValueFactory(new PropertyValueFactory<>("numeroPersone"));
        colTel.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colNote.setCellValueFactory(new PropertyValueFactory<>("note"));
       
        // Formattazione data personalizzata per la colonna
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM HH:mm");
        colOrario.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getDataOra().format(formatter)));
 
        // Configurazione input default
        spinPax.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 2));
        datePicker.setValue(LocalDate.now());
        txtOra.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
 
        loadPrenotazioni();
        loadTavoli();
 
        // Listener per la ricerca
        txtCerca.textProperty().addListener((observable, oldValue, newValue) -> filterList(newValue));
    }
 
    private void loadPrenotazioni() {
        try {
            masterData.setAll(prenotazioneDAO.getAllPrenotazioni());
            tablePrenotazioni.setItems(masterData);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
 
    private void loadTavoli() {
        try {
            List<Tavolo> tavoli = tavoloDAO.getAllTavoli();
            comboTavolo.getItems().setAll(tavoli);
            comboTavolo.setConverter(new StringConverter<Tavolo>() {
                @Override
                public String toString(Tavolo t) {
                    if (t == null) return null;
                    return "Tavolo " + t.getNumero() + " (" + t.getPosti() + " posti) - " + t.getStato();
                }
                @Override
                public Tavolo fromString(String string) {
                    return null;
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
 
    private void filterList(String query) {
        FilteredList<Prenotazione> filtered = new FilteredList<>(masterData, p -> {
            if (query == null || query.isEmpty()) return true;
            String lower = query.toLowerCase();
            return p.getNomeCliente().toLowerCase().contains(lower);
        });
        tablePrenotazioni.setItems(filtered);
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
            Integer idTavolo = selectedTavolo != null ? selectedTavolo.getId() : null;
 
            Prenotazione p = new Prenotazione(0, nome, txtTelefono.getText(), spinPax.getValue(), dataOra, txtNote.getText(), idTavolo);
           
            prenotazioneDAO.insertPrenotazione(p);
           
            if (selectedTavolo != null) {
                tavoloDAO.updateStatoTavolo(selectedTavolo.getId(), "Prenotato");
                loadTavoli(); // Ricarica stato tavoli
            }
 
            loadPrenotazioni();
            clearForm();
           
        } catch (Exception e) {
            showAlert("Errore", "Controlla i dati inseriti (es. orario HH:mm).");
            e.printStackTrace();
        }
    }
 
    @FXML
    private void handleElimina() {
        Prenotazione selected = tablePrenotazioni.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                prenotazioneDAO.deletePrenotazione(selected.getId());
                loadPrenotazioni();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
   
    @FXML
    private void handleBack() {
        try {
            FamigliaSaporiApplication.setRoot("SalaView");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 
    private void clearForm() {
        txtNome.clear();
        txtTelefono.clear();
        txtNote.clear();
        spinPax.getValueFactory().setValue(2);
    }
 
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
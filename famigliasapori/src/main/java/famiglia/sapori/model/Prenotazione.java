package famiglia.sapori.model;
 
import java.time.LocalDateTime;
 
public class Prenotazione {
    private int id;
    private String nomeCliente;
    private String telefono;
    private int numeroPersone;
    private LocalDateTime dataOra;
    private String note;
    private Integer idTavolo; // Può essere null
 
    public Prenotazione(int id, String nomeCliente, String telefono, int numeroPersone, LocalDateTime dataOra, String note, Integer idTavolo) {
        this.id = id;
        this.nomeCliente = nomeCliente;
        this.telefono = telefono;
        this.numeroPersone = numeroPersone;
        this.dataOra = dataOra;
        this.note = note;
        this.idTavolo = idTavolo;
    }
 
    // Costruttore per compatibilità (senza tavolo)
    public Prenotazione(int id, String nomeCliente, String telefono, int numeroPersone, LocalDateTime dataOra, String note) {
        this(id, nomeCliente, telefono, numeroPersone, dataOra, note, null);
    }
 
    // Getters e Setters
    public int getId() { return id; }
    public String getNomeCliente() { return nomeCliente; }
    public String getTelefono() { return telefono; }
    public int getNumeroPersone() { return numeroPersone; }
    public LocalDateTime getDataOra() { return dataOra; }
    public String getNote() { return note; }
    public Integer getIdTavolo() { return idTavolo; }
    public void setIdTavolo(Integer idTavolo) { this.idTavolo = idTavolo; }
 
    @Override
    public String toString() {
        return nomeCliente + " (" + numeroPersone + "p) - " + dataOra.toLocalTime();
    }
}

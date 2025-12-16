package famiglia.sapori.model;
 
import java.time.LocalDateTime;
 
public class Comanda {
    private int id;
    private int idTavolo;
    private String prodotti;
    private double totale;
    private String tipo;
    private String stato;
    private LocalDateTime dataOra;
    private String note;
    private int idCameriere;
 
    public Comanda(int id, int idTavolo, String prodotti, double totale, String tipo, String stato, LocalDateTime dataOra, String note, int idCameriere) {
        this.id = id;
        this.idTavolo = idTavolo;
        this.prodotti = prodotti;
        this.totale = totale;
        this.tipo = tipo;
        this.stato = stato;
        this.dataOra = dataOra;
        this.note = note;
        this.idCameriere = idCameriere;
    }
 
    public int getId() { return id; }
    public int getIdTavolo() { return idTavolo; }
    public String getProdotti() { return prodotti; }
    public double getTotale() { return totale; }
    public String getTipo() { return tipo; }
    public String getStato() { return stato; }
    public void setStato(String stato) { this.stato = stato; }
    public LocalDateTime getDataOra() { return dataOra; }
    public String getNote() { return note; }
    public int getIdCameriere() { return idCameriere; }
}
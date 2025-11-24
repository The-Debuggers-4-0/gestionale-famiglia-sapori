package famiglia.sapori.model;
 
public class Piatto {
    private int id;
    private String nome;
    private String descrizione;
    private double prezzo;
    private String categoria;
    private boolean disponibile;
    private String allergeni;
 
    public Piatto(int id, String nome, String descrizione, double prezzo, String categoria, boolean disponibile, String allergeni) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.prezzo = prezzo;
        this.categoria = categoria;
        this.disponibile = disponibile;
        this.allergeni = allergeni;
    }
 
    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getDescrizione() { return descrizione; }
    public double getPrezzo() { return prezzo; }
    public String getCategoria() { return categoria; }
    public boolean isDisponibile() { return disponibile; }
    public String getAllergeni() { return allergeni; }
   
    @Override
    public String toString() {
        return nome;
    }
}
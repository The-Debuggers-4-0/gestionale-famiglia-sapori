package famiglia.sapori.model;

public class Tavolo {
    private int id;
    private int numero;
    private String stato; // 'Libero', 'Occupato'
    private int posti;
    private String note;

    public Tavolo(int id, int numero, String stato, int posti, String note) {
        this.id = id;
        this.numero = numero;
        this.stato = stato;
        this.posti = posti;
        this.note = note;
    }

    public int getId() { return id; }
    public int getNumero() { return numero; }
    public String getStato() { return stato; }
    public void setStato(String stato) { this.stato = stato; }
    public int getPosti() { return posti; }
    public String getNote() { return note; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Tavolo tavolo = (Tavolo) obj;
        return id == tavolo.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return "Tavolo " + numero;
    }
}
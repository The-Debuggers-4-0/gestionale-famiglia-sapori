package famiglia.sapori.model;

public class ProdottoMagazzino {
    private int id;
    private String prodotto;
    private double quantita;
    private String unitaMisura;
    private double sogliaMinima;

    public ProdottoMagazzino(int id, String prodotto, double quantita, String unitaMisura, double sogliaMinima) {
        this.id = id;
        this.prodotto = prodotto;
        this.quantita = quantita;
        this.unitaMisura = unitaMisura;
        this.sogliaMinima = sogliaMinima;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getProdotto() { return prodotto; }
    public void setProdotto(String prodotto) { this.prodotto = prodotto; }

    public double getQuantita() { return quantita; }
    public void setQuantita(double quantita) { this.quantita = quantita; }

    public String getUnitaMisura() { return unitaMisura; }
    public void setUnitaMisura(String unitaMisura) { this.unitaMisura = unitaMisura; }

    public double getSogliaMinima() { return sogliaMinima; }
    public void setSogliaMinima(double sogliaMinima) { this.sogliaMinima = sogliaMinima; }

    @Override
    public String toString() {
        return prodotto + " (" + quantita + " " + unitaMisura + ")";
    }
}

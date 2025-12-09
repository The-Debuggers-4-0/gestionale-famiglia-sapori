package famiglia.sapori.model;

public class Utente {
    private int id;
    private String nome;
    private String username;
    private String password;
    private String ruolo;

    public Utente(int id, String nome, String username, String password, String ruolo) {
        this.id = id;
        this.nome = nome;
        this.username = username;
        this.password = password;
        this.ruolo = ruolo;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRuolo() { return ruolo; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Utente utente = (Utente) obj;
        return id == utente.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return nome;
    }
}
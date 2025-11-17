package modelo;

public class Passageiro {

    private int id;
    private String nome;

    private final String documento;

    private String email;

    public Passageiro(String nome, String documento, String email) {
        this.nome = nome;
        this.documento = documento;
        this.email = email;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDocumento() {
        return documento;
    }

    @Override
    public String toString() {
        return "Passageiro [id=" + id +
                ", nome=" + nome +
                ", documento=" + documento +
                ", email=" + email + "]";
    }
}

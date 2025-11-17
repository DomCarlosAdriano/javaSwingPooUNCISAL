package modelo;
import java.time.LocalDateTime;

public class Voo {

    private int id;
    private String numeroVoo;
    private String origem;
    private String destino;
    private LocalDateTime dataHoraPartida;
    private int assentosDisponiveis;
    private double preco;

    public Voo() {
    }

    public Voo(String numeroVoo, String origem, String destino, LocalDateTime dataHoraPartida, int assentosDisponiveis, double preco) {
        this.numeroVoo = numeroVoo;
        this.origem = origem;
        this.destino = destino;
        this.dataHoraPartida = dataHoraPartida;
        this.assentosDisponiveis = assentosDisponiveis;
        this.preco = preco;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumeroVoo() {
        return numeroVoo;
    }

    public void setNumeroVoo(String numeroVoo) {
        this.numeroVoo = numeroVoo;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public LocalDateTime getDataHoraPartida() {
        return dataHoraPartida;
    }

    public void setDataHoraPartida(LocalDateTime dataHoraPartida) {
        this.dataHoraPartida = dataHoraPartida;
    }

    public int getAssentosDisponiveis() {
        return assentosDisponiveis;
    }

    public void setAssentosDisponiveis(int assentosDisponiveis) {
        this.assentosDisponiveis = assentosDisponiveis;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    @Override
    public String toString() {
        return "Voo [id=" + id +
                ", numeroVoo=" + numeroVoo +
                ", origem=" + origem +
                ", destino=" + destino +
                ", dataHoraPartida=" + dataHoraPartida +
                ", assentosDisponiveis=" + assentosDisponiveis +
                ", preco=" + preco + "]";
    }
}

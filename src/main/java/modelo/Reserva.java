package modelo;

import java.time.LocalDateTime;

public class Reserva extends EntidadeBase{


    private final int idVoo;
    private final int idPassageiro;

    private String numeroAssento;
    private final LocalDateTime dataCompra;

    public Reserva(int idVoo, int idPassageiro, String numeroAssento) {
        this.idVoo = idVoo;
        this.idPassageiro = idPassageiro;
        this.numeroAssento = numeroAssento;
        this.dataCompra = LocalDateTime.now();
    }

    public Reserva(int id, int idVoo, int idPassageiro, String numeroAssento, LocalDateTime dataCompra) {
        this.id = id;
        this.idVoo = idVoo;
        this.idPassageiro = idPassageiro;
        this.numeroAssento = numeroAssento;
        this.dataCompra = dataCompra;
    }

    // --- Getters e Setters ---


    public String getNumeroAssento() {
        return numeroAssento;
    }

    public void setNumeroAssento(String numeroAssento) {
        this.numeroAssento = numeroAssento;
    }

    public int getIdVoo() {
        return idVoo;
    }

    public int getIdPassageiro() {
        return idPassageiro;
    }

    public LocalDateTime getDataCompra() {
        return dataCompra;
    }

    @Override
    public String toString() {
        return "Reserva [id=" + id +
                ", idVoo=" + idVoo +
                ", idPassageiro=" + idPassageiro +
                ", numeroAssento=" + numeroAssento +
                ", dataCompra=" + dataCompra + "]";
    }
}
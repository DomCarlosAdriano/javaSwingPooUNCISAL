package dao;

import modelo.Reserva;
import java.sql.SQLException;
import java.util.List;

public interface IReservaDAO {

    Reserva salvar(Reserva reserva) throws SQLException;

    boolean excluir(int idReserva) throws SQLException;

    List<Reserva> buscarPorPassageiro(int idPassageiro) throws SQLException;

    List<Reserva> buscarPorVoo(int idVoo) throws SQLException;
}
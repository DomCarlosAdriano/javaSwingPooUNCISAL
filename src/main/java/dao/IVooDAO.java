package dao;

import modelo.Voo;
import java.sql.SQLException;
import java.util.List;

public interface IVooDAO {

    Voo salvar(Voo voo) throws SQLException;

    boolean atualizar(Voo voo) throws SQLException;

    boolean excluir(int idVoo) throws SQLException;

    Voo buscarPorId(int idVoo) throws SQLException;

    List<Voo> buscarTodos() throws SQLException;

    List<Voo> buscarPorOrigemDestino(String origem, String destino) throws SQLException;
}
package dao;

import modelo.Passageiro;
import java.sql.SQLException;
import java.util.List;

public interface IPassageiroDAO {

    Passageiro salvar(Passageiro passageiro) throws SQLException;

    boolean atualizar(Passageiro passageiro) throws SQLException;

    boolean excluir(int idPassageiro) throws SQLException;

    Passageiro buscarPorId(int idPassageiro) throws SQLException;

    Passageiro buscarPorDocumento(String documento) throws SQLException;

    List<Passageiro> buscarTodos() throws SQLException;
}
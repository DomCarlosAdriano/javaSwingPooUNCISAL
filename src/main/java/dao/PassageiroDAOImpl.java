package dao;

import modelo.Passageiro;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PassageiroDAOImpl implements IPassageiroDAO {

    @Override
    public Passageiro salvar(Passageiro passageiro) throws SQLException {
        String sql = "INSERT INTO passageiros (nome, documento, email) VALUES (?, ?, ?)";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, passageiro.getNome());
            stmt.setString(2, passageiro.getDocumento());
            stmt.setString(3, passageiro.getEmail());

            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        passageiro.setId(rs.getInt(1));
                    }
                }
                return passageiro;
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed: passageiros.documento")) {
                throw new SQLException("Erro: Já existe um passageiro com este documento.");
            }
            throw e;
        }
        return null;
    }


    @Override
    public boolean atualizar(Passageiro passageiro) throws SQLException {
        String sql = "UPDATE passageiros SET nome = ?, email = ? WHERE id = ?";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, passageiro.getNome());
            stmt.setString(2, passageiro.getEmail());
            stmt.setInt(3, passageiro.getId());

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        }
    }

    @Override
    public boolean excluir(int idPassageiro) throws SQLException {
        String sql = "DELETE FROM passageiros WHERE id = ?";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPassageiro);
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            if (e.getMessage().contains("FOREIGN KEY constraint failed")) {
                throw new SQLException("Erro: Este passageiro não pode ser excluído pois possui reservas ativas.");
            }
            throw e;
        }
    }

    @Override
    public Passageiro buscarPorId(int idPassageiro) throws SQLException {
        String sql = "SELECT * FROM passageiros WHERE id = ?";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPassageiro);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extrairPassageiroDoResultSet(rs);
                }
            }
        }
        return null;
    }

    @Override
    public Passageiro buscarPorDocumento(String documento) throws SQLException {
        String sql = "SELECT * FROM passageiros WHERE documento = ?";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, documento);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extrairPassageiroDoResultSet(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Passageiro> buscarTodos() throws SQLException {
        List<Passageiro> passageiros = new ArrayList<>();
        String sql = "SELECT * FROM passageiros";

        try (Connection conn = ConexaoBD.getConexao();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                passageiros.add(extrairPassageiroDoResultSet(rs));
            }
        }
        return passageiros;
    }

    private Passageiro extrairPassageiroDoResultSet(ResultSet rs) throws SQLException {
        String nome = rs.getString("nome");
        String documento = rs.getString("documento");
        String email = rs.getString("email");

        Passageiro passageiro = new Passageiro(nome, documento, email);
        passageiro.setId(rs.getInt("id"));

        return passageiro;
    }
}
package dao;

import modelo.Voo;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class VooDAOImpl implements IVooDAO {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public Voo salvar(Voo voo) throws SQLException {
        String sql = "INSERT INTO voos (numero_voo, origem, destino, data_hora_partida, assentos_disponiveis, preco) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, voo.getNumeroVoo());
            stmt.setString(2, voo.getOrigem());
            stmt.setString(3, voo.getDestino());
            // Converte LocalDateTime para String no formato ISO
            stmt.setString(4, voo.getDataHoraPartida().format(FORMATTER));
            stmt.setInt(5, voo.getAssentosDisponiveis());
            stmt.setDouble(6, voo.getPreco());

            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        voo.setId(rs.getInt(1));
                    }
                }
                return voo;
            }
        }
        return null;
    }

    @Override
    public boolean atualizar(Voo voo) throws SQLException {
        String sql = "UPDATE voos SET numero_voo = ?, origem = ?, destino = ?, " +
                "data_hora_partida = ?, assentos_disponiveis = ?, preco = ? " +
                "WHERE id = ?";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, voo.getNumeroVoo());
            stmt.setString(2, voo.getOrigem());
            stmt.setString(3, voo.getDestino());
            stmt.setString(4, voo.getDataHoraPartida().format(FORMATTER));
            stmt.setInt(5, voo.getAssentosDisponiveis());
            stmt.setDouble(6, voo.getPreco());
            stmt.setInt(7, voo.getId());

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0; // Retorna true se atualizou pelo menos 1 linha
        }
    }

    @Override
    public boolean excluir(int idVoo) throws SQLException {
        String sql = "DELETE FROM voos WHERE id = ?";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idVoo);
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        }
    }

    @Override
    public Voo buscarPorId(int idVoo) throws SQLException {
        String sql = "SELECT * FROM voos WHERE id = ?";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idVoo);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extrairVooDoResultSet(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Voo> buscarTodos() throws SQLException {
        List<Voo> voos = new ArrayList<>();
        String sql = "SELECT * FROM voos";

        try (Connection conn = ConexaoBD.getConexao();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                voos.add(extrairVooDoResultSet(rs));
            }
        }
        return voos;
    }

    @Override
    public List<Voo> buscarPorOrigemDestino(String origem, String destino) throws SQLException {

        List<Voo> voos = new ArrayList<>();

        String sql = "SELECT * FROM voos WHERE origem LIKE ? AND destino LIKE ?";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + origem + "%");
            stmt.setString(2, "%" + destino + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    voos.add(extrairVooDoResultSet(rs));
                }
            }
        }
        return voos;
    }

    private Voo extrairVooDoResultSet(ResultSet rs) throws SQLException {
        Voo voo = new Voo();
        voo.setId(rs.getInt("id"));
        voo.setNumeroVoo(rs.getString("numero_voo"));
        voo.setOrigem(rs.getString("origem"));
        voo.setDestino(rs.getString("destino"));
        // Converte a String do banco de volta para LocalDateTime
        voo.setDataHoraPartida(LocalDateTime.parse(rs.getString("data_hora_partida"), FORMATTER));
        voo.setAssentosDisponiveis(rs.getInt("assentos_disponiveis"));
        voo.setPreco(rs.getDouble("preco"));
        return voo;
    }
}
package dao;

import modelo.Reserva;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAOImpl implements IReservaDAO {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public Reserva salvar(Reserva reserva) throws SQLException {
        String sql = "INSERT INTO reservas (id_voo, id_passageiro, numero_assento, data_compra) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, reserva.getIdVoo());
            stmt.setInt(2, reserva.getIdPassageiro());
            stmt.setString(3, reserva.getNumeroAssento());
            stmt.setString(4, reserva.getDataCompra().format(FORMATTER)); // Salva a data formatada

            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        reserva.setId(rs.getInt(1));
                    }
                }
                return reserva;
            }
        }
        return null;
    }

    @Override
    public boolean excluir(int idReserva) throws SQLException {

        String sql = "DELETE FROM reservas WHERE id = ?";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idReserva);
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        }
    }

    @Override
    public List<Reserva> buscarPorPassageiro(int idPassageiro) throws SQLException {
        List<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT * FROM reservas WHERE id_passageiro = ?";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPassageiro);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reservas.add(extrairReservaDoResultSet(rs));
                }
            }
        }
        return reservas;
    }

    @Override
    public List<Reserva> buscarPorVoo(int idVoo) throws SQLException {
        List<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT * FROM reservas WHERE id_voo = ?";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idVoo);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reservas.add(extrairReservaDoResultSet(rs));
                }
            }
        }
        return reservas;
    }


    private Reserva extrairReservaDoResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int idVoo = rs.getInt("id_voo");
        int idPassageiro = rs.getInt("id_passageiro");
        String numeroAssento = rs.getString("numero_assento");

        LocalDateTime dataCompra = LocalDateTime.parse(rs.getString("data_compra"), FORMATTER);

        return new Reserva(id, idVoo, idPassageiro, numeroAssento, dataCompra);
    }
}
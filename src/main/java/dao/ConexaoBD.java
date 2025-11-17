package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class ConexaoBD {

    private static final String URL = "jdbc:sqlite:passagens.db";
    public static Connection getConexao() throws SQLException {

        // O DriverManager tentará carregar o driver do SQLite
        return DriverManager.getConnection(URL);
    }


    public static void criarTabelas() {
        // SQL para criar a tabela de voos
        String sqlVoo = "CREATE TABLE IF NOT EXISTS voos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "numero_voo TEXT NOT NULL," +
                "origem TEXT NOT NULL," +
                "destino TEXT NOT NULL," +
                "data_hora_partida TEXT NOT NULL," + // Salvaremos como ISO-8601 String
                "assentos_disponiveis INTEGER NOT NULL," +
                "preco REAL NOT NULL" +
                ");";

        // SQL para criar a tabela de passageiros
        String sqlPassageiro = "CREATE TABLE IF NOT EXISTS passageiros (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nome TEXT NOT NULL," +
                "documento TEXT NOT NULL UNIQUE," + // UNIQUE impede documentos duplicados
                "email TEXT" +
                ");";

        String sqlReserva = "CREATE TABLE IF NOT EXISTS reservas (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "id_voo INTEGER NOT NULL," +
                "id_passageiro INTEGER NOT NULL," +
                "numero_assento TEXT NOT NULL," +
                "data_compra TEXT NOT NULL," + // Salvaremos como ISO-8601 String
                "FOREIGN KEY(id_voo) REFERENCES voos(id)," +
                "FOREIGN KEY(id_passageiro) REFERENCES passageiros(id)" +
                ");";

        try (Connection conn = getConexao();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sqlVoo);
            stmt.execute(sqlPassageiro);
            stmt.execute(sqlReserva);

            System.out.println("Tabelas verificadas/criadas com sucesso.");

            inserirDadosIniciais(conn);

        } catch (SQLException e) {
            System.err.println("Erro ao criar tabelas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void inserirDadosIniciais(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            // Verifica se a tabela voos está vazia
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM voos");
            if (rs.next() && rs.getInt("total") == 0) {
                System.out.println("Populando tabela 'voos' com dados iniciais...");

                // (Requisito 7: Operação de Inclusão)
                // Insere 3 voos de exemplo
                stmt.execute("INSERT INTO voos (numero_voo, origem, destino, data_hora_partida, assentos_disponiveis, preco) " +
                        "VALUES ('G3-1000', 'São Paulo (GRU)', 'Recife (REC)', '2025-12-20T10:30:00', 50, 1200.00)");

                stmt.execute("INSERT INTO voos (numero_voo, origem, destino, data_hora_partida, assentos_disponiveis, preco) " +
                        "VALUES ('AD-2020', 'Rio de Janeiro (GIG)', 'Salvador (SSA)', '2025-12-22T14:00:00', 30, 950.50)");

                stmt.execute("INSERT INTO voos (numero_voo, origem, destino, data_hora_partida, assentos_disponiveis, preco) " +
                        "VALUES ('LA-3000', 'São Paulo (GRU)', 'Porto Alegre (POA)', '2025-12-21T08:15:00', 1, 800.00)"); // Apenas 1 assento

                System.out.println("Dados iniciais inseridos com sucesso.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao inserir dados iniciais: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
package view;

// 1. Imports corrigidos para 'modelo' e lógica de reserva
import dao.IPassageiroDAO;
import dao.IReservaDAO;
import dao.IVooDAO;
import dao.PassageiroDAOImpl;
import dao.ReservaDAOImpl;
import dao.VooDAOImpl;
import modelo.Passageiro; // <-- CORRIGIDO
import modelo.Reserva;     // <-- CORRIGIDO
import modelo.Voo;         // <-- CORRIGIDO
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PainelBuscaVoo extends JPanel {

    private JTextField txtOrigem;
    private JTextField txtDestino;
    private JButton btnBuscar;
    private JTable tabelaVoos;
    private DefaultTableModel modeloTabela;

    private JTextField txtDocumentoPassageiro;
    private JTextField txtAssento;
    private JButton btnReservar;

    private IVooDAO vooDAO;
    private IPassageiroDAO passageiroDAO;
    private IReservaDAO reservaDAO;

    private DateTimeFormatter formatadorTabela = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public PainelBuscaVoo() {
        this.vooDAO = new VooDAOImpl();
        this.passageiroDAO = new PassageiroDAOImpl();
        this.reservaDAO = new ReservaDAOImpl();

        setLayout(new BorderLayout(5, 5));

        JPanel painelFormulario = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelFormulario.add(new JLabel("Origem:"));
        txtOrigem = new JTextField(15);
        painelFormulario.add(txtOrigem);
        painelFormulario.add(new JLabel("Destino:"));
        txtDestino = new JTextField(15);
        painelFormulario.add(txtDestino);
        btnBuscar = new JButton("Buscar Voos");
        painelFormulario.add(btnBuscar);
        add(painelFormulario, BorderLayout.NORTH);

        String[] colunas = {"ID", "Nº Voo", "Origem", "Destino", "Data/Hora Partida", "Assentos", "Preço"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaVoos = new JTable(modeloTabela);
        tabelaVoos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tabelaVoos);
        add(scrollPane, BorderLayout.CENTER);

        JPanel painelReserva = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelReserva.add(new JLabel("Documento do Passageiro:"));
        txtDocumentoPassageiro = new JTextField(15);
        painelReserva.add(txtDocumentoPassageiro);

        painelReserva.add(new JLabel("Assento (ex: 22A):"));
        txtAssento = new JTextField(5);
        painelReserva.add(txtAssento);

        btnReservar = new JButton("Reservar Voo Selecionado");
        painelReserva.add(btnReservar);

        add(painelReserva, BorderLayout.SOUTH);

        btnBuscar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscarVoos();
            }
        });

        btnReservar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                realizarReserva();
            }
        });
    }


    private void buscarVoos() {
        String origem = txtOrigem.getText();
        String destino = txtDestino.getText();
        try {
            List<Voo> voos = vooDAO.buscarPorOrigemDestino(origem, destino);
            modeloTabela.setRowCount(0);
            for (Voo voo : voos) {
                Object[] linha = {
                        voo.getId(),
                        voo.getNumeroVoo(),
                        voo.getOrigem(),
                        voo.getDestino(),
                        voo.getDataHoraPartida().format(formatadorTabela),
                        voo.getAssentosDisponiveis(),
                        String.format("R$ %.2f", voo.getPreco())
                };
                modeloTabela.addRow(linha);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao buscar voos: " + e.getMessage(),
                    "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }


    private void realizarReserva() {
        // 1. Pega o Voo selecionado na tabela
        int linhaSelecionada = tabelaVoos.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um voo na tabela.",
                    "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int idVoo = (int) modeloTabela.getValueAt(linhaSelecionada, 0);

        // 2. Pega os dados do formulário de reserva
        String documento = txtDocumentoPassageiro.getText();
        String assento = txtAssento.getText();

        if (documento.isEmpty() || assento.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Documento e Assento são obrigatórios.",
                    "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // (Requisito 4: Tratamento de Exceções)
        try {
            // 3. Busca o Passageiro
            Passageiro passageiro = passageiroDAO.buscarPorDocumento(documento);
            if (passageiro == null) {
                JOptionPane.showMessageDialog(this,
                        "Passageiro não encontrado. Cadastre-o na aba 'Cadastrar Passageiro'.",
                        "Erro de Reserva", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 4. Busca o Voo (para verificar assentos)
            Voo voo = vooDAO.buscarPorId(idVoo);
            if (voo.getAssentosDisponiveis() <= 0) {
                JOptionPane.showMessageDialog(this, "Voo lotado! Não há mais assentos disponíveis.",
                        "Erro de Reserva", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 5. Cria a Reserva
            Reserva reserva = new Reserva(voo.getId(), passageiro.getId(), assento);
            reservaDAO.salvar(reserva);

            // 6. Atualiza o Voo (diminui um assento)
            voo.setAssentosDisponiveis(voo.getAssentosDisponiveis() - 1);
            vooDAO.atualizar(voo);

            // 7. Sucesso!
            JOptionPane.showMessageDialog(this,
                    "Reserva realizada com sucesso para " + passageiro.getNome() + "!\n" +
                            "Voo: " + voo.getNumeroVoo() + ", Assento: " + assento,
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            txtDocumentoPassageiro.setText("");
            txtAssento.setText("");

            // 8. Atualiza a tabela
            buscarVoos();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao processar a reserva: " + e.getMessage(),
                    "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
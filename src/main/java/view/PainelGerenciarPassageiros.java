package view;

import dao.IPassageiroDAO;
import dao.IReservaDAO;
import dao.IVooDAO;
import dao.PassageiroDAOImpl;
import dao.ReservaDAOImpl;
import dao.VooDAOImpl;
import modelo.Passageiro;
import modelo.Reserva;
import modelo.Voo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PainelGerenciarPassageiros extends JPanel {

    private IPassageiroDAO passageiroDAO;
    private IReservaDAO reservaDAO;
    private IVooDAO vooDAO;


    private JTable tabelaPassageiros;
    private DefaultTableModel modeloTabelaPassageiros;

    private JTextField txtId;
    private JTextField txtNome;
    private JTextField txtDocumento;
    private JTextField txtEmail;
    private JButton btnSalvar;
    private JButton btnAtualizar;
    private JButton btnExcluir;
    private JButton btnLimpar;

    private JTable tabelaHistorico;
    private DefaultTableModel modeloTabelaHistorico;
    private DateTimeFormatter formatadorDataHora = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public PainelGerenciarPassageiros() {
        this.passageiroDAO = new PassageiroDAOImpl();
        this.reservaDAO = new ReservaDAOImpl();
        this.vooDAO = new VooDAOImpl();

        setLayout(new BorderLayout(10, 10));

        String[] colunasPassageiros = {"ID", "Nome", "Documento", "Email"};
        modeloTabelaPassageiros = new DefaultTableModel(colunasPassageiros, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaPassageiros = new JTable(modeloTabelaPassageiros);
        tabelaPassageiros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPanePassageiros = new JScrollPane(tabelaPassageiros);

        JTabbedPane painelInferiorComAbas = new JTabbedPane();

        JPanel painelFormulario = criarPainelFormulario();
        painelInferiorComAbas.addTab("Editar/Criar Passageiro", painelFormulario);

        JPanel painelHistorico = criarPainelHistorico();
        painelInferiorComAbas.addTab("Histórico de Reservas", painelHistorico);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                scrollPanePassageiros,
                painelInferiorComAbas);
        splitPane.setResizeWeight(0.5);
        add(splitPane, BorderLayout.CENTER);

        carregarPassageirosNaTabela();

        tabelaPassageiros.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int linhaSelecionada = tabelaPassageiros.getSelectedRow();
                if (linhaSelecionada == -1) return;

                int idPassageiro = (int) modeloTabelaPassageiros.getValueAt(linhaSelecionada, 0);

                preencherFormularioComLinhaSelecionada(linhaSelecionada);
                carregarHistoricoDoPassageiro(idPassageiro);
            }
        });

        btnLimpar.addActionListener(e -> limparCampos());
        btnSalvar.addActionListener(e -> salvarNovoPassageiro());
        btnAtualizar.addActionListener(e -> atualizarPassageiroSelecionado());
        btnExcluir.addActionListener(e -> excluirPassageiroSelecionado());
    }


    private JPanel criarPainelFormulario() {
        JPanel painelFormulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; painelFormulario.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1; txtId = new JTextField(10); txtId.setEditable(false); painelFormulario.add(txtId, gbc);
        gbc.gridx = 2; painelFormulario.add(new JLabel("Documento:"), gbc);
        gbc.gridx = 3; gbc.gridwidth = 3; txtDocumento = new JTextField(20); painelFormulario.add(txtDocumento, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; painelFormulario.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 5; txtNome = new JTextField(30); painelFormulario.add(txtNome, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1; painelFormulario.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 5; txtEmail = new JTextField(30); painelFormulario.add(txtEmail, gbc);

        JPanel painelBotoes = new JPanel();
        btnSalvar = new JButton("Salvar Novo");
        btnAtualizar = new JButton("Atualizar Selecionado");
        btnExcluir = new JButton("Excluir Selecionado");
        btnLimpar = new JButton("Limpar");
        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnAtualizar);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnLimpar);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 6;
        gbc.fill = GridBagConstraints.NONE;
        painelFormulario.add(painelBotoes, gbc);

        return painelFormulario;
    }

    private JPanel criarPainelHistorico() {
        JPanel painelHistorico = new JPanel(new BorderLayout(5, 5));
        painelHistorico.add(new JLabel("Reservas do passageiro selecionado:"), BorderLayout.NORTH);

        String[] colunasHistorico = {"Nº Voo", "Origem", "Destino", "Data Partida", "Assento", "Data Compra"};
        modeloTabelaHistorico = new DefaultTableModel(colunasHistorico, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaHistorico = new JTable(modeloTabelaHistorico);
        JScrollPane scrollPaneHistorico = new JScrollPane(tabelaHistorico);
        painelHistorico.add(scrollPaneHistorico, BorderLayout.CENTER);

        return painelHistorico;
    }

    private void carregarPassageirosNaTabela() {
        modeloTabelaPassageiros.setRowCount(0);
        try {
            List<Passageiro> passageiros = passageiroDAO.buscarTodos();
            for (Passageiro p : passageiros) {
                modeloTabelaPassageiros.addRow(new Object[]{
                        p.getId(),
                        p.getNome(),
                        p.getDocumento(),
                        p.getEmail()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar passageiros: " + e.getMessage(),
                    "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }


    private void carregarHistoricoDoPassageiro(int idPassageiro) {
        modeloTabelaHistorico.setRowCount(0);
        try {
            List<Reserva> reservas = reservaDAO.buscarPorPassageiro(idPassageiro);

            for (Reserva reserva : reservas) {
                Voo voo = vooDAO.buscarPorId(reserva.getIdVoo());
                if (voo != null) {
                    modeloTabelaHistorico.addRow(new Object[]{
                            voo.getNumeroVoo(),
                            voo.getOrigem(),
                            voo.getDestino(),
                            voo.getDataHoraPartida().format(formatadorDataHora),
                            reserva.getNumeroAssento(),
                            reserva.getDataCompra().format(formatadorDataHora)
                    });
                } else {
                    modeloTabelaHistorico.addRow(new Object[]{
                            "(Voo " + reserva.getIdVoo() + " excluído)",
                            "N/A", "N/A", "N/A",
                            reserva.getNumeroAssento(),
                            reserva.getDataCompra().format(formatadorDataHora)
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar histórico do passageiro: " + e.getMessage(),
                    "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void preencherFormularioComLinhaSelecionada(int linhaSelecionada) {
        // Pega os valores da tabela
        String id = modeloTabelaPassageiros.getValueAt(linhaSelecionada, 0).toString();
        String nome = modeloTabelaPassageiros.getValueAt(linhaSelecionada, 1).toString();
        String documento = modeloTabelaPassageiros.getValueAt(linhaSelecionada, 2).toString();
        String email = modeloTabelaPassageiros.getValueAt(linhaSelecionada, 3).toString();

        txtId.setText(id);
        txtNome.setText(nome);
        txtDocumento.setText(documento);
        txtEmail.setText(email);

        txtDocumento.setEditable(false);
    }

    private void limparCampos() {
        txtId.setText("");
        txtNome.setText("");
        txtDocumento.setText("");
        txtEmail.setText("");
        txtDocumento.setEditable(true);
        tabelaPassageiros.clearSelection();
        modeloTabelaHistorico.setRowCount(0); // (NOVO) Limpa o histórico
    }


    private void salvarNovoPassageiro() {
        String nome = txtNome.getText();
        String documento = txtDocumento.getText();
        String email = txtEmail.getText();

        if (nome.isEmpty() || documento.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome e Documento são obrigatórios.",
                    "Erro de Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Limpe o formulário antes de salvar um novo passageiro.",
                    "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Passageiro passageiro = new Passageiro(nome, documento, email);

        try {
            passageiroDAO.salvar(passageiro);
            JOptionPane.showMessageDialog(this, "Passageiro salvo com sucesso! (ID: " + passageiro.getId() + ")",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            carregarPassageirosNaTabela();
            limparCampos();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao salvar passageiro: " + e.getMessage(),
                    "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void atualizarPassageiroSelecionado() {
        String idStr = txtId.getText();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione um passageiro na tabela para atualizar.",
                    "Erro de Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nome = txtNome.getText();
        String documento = txtDocumento.getText();
        String email = txtEmail.getText();

        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome é obrigatório.",
                    "Erro de Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            Passageiro passageiro = new Passageiro(nome, documento, email);
            passageiro.setId(id);

            boolean sucesso = passageiroDAO.atualizar(passageiro);

            if (sucesso) {
                JOptionPane.showMessageDialog(this, "Passageiro atualizado com sucesso!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarPassageirosNaTabela();
                limparCampos();
            } else {
                JOptionPane.showMessageDialog(this, "Falha ao atualizar passageiro (não encontrado?).",
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao atualizar passageiro: " + e.getMessage(),
                    "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void excluirPassageiroSelecionado() {
        String idStr = txtId.getText();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione um passageiro na tabela para excluir.",
                    "Erro de Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int resposta = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir o passageiro: " + txtNome.getText() + "?\n" +
                        "Isso só funcionará se ele não tiver reservas ativas.",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            boolean sucesso = passageiroDAO.excluir(id);

            if (sucesso) {
                JOptionPane.showMessageDialog(this, "Passageiro excluído com sucesso!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarPassageirosNaTabela();
                limparCampos();
            } else {
                JOptionPane.showMessageDialog(this, "Falha ao excluir passageiro (não encontrado?).",
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao excluir passageiro: " + e.getMessage(),
                    "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
        }
    }
}
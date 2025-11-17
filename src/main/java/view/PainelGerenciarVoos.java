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
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;


public class PainelGerenciarVoos extends JPanel {

    private IVooDAO vooDAO;
    private IReservaDAO reservaDAO;
    private IPassageiroDAO passageiroDAO;

    private JTable tabelaVoos;
    private DefaultTableModel modeloTabelaVoos;
    private JButton btnRecarregarVoos;
    private JButton btnCancelarVoo;

    private JTabbedPane abasAcoesVoo;
    private JTextField txtNovoNumVoo;
    private JTextField txtNovoOrigem;
    private JTextField txtNovoDestino;
    private JTextField txtNovoDataHora;
    private JTextField txtNovoAssentos;
    private JTextField txtNovoPreco;
    private JButton btnSalvarNovoVoo;

    private JTable tabelaPassageiros;
    private DefaultTableModel modeloTabelaPassageiros;
    private JButton btnCancelarReserva;

    private JTextField txtDocumentoPassageiro;
    private JTextField txtAssento;
    private JButton btnFazerReserva;

    private DateTimeFormatter formatadorDataHora = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public PainelGerenciarVoos() {
        // --- 1. Instanciar DAOs ---
        this.vooDAO = new VooDAOImpl();
        this.reservaDAO = new ReservaDAOImpl();
        this.passageiroDAO = new PassageiroDAOImpl();

        // --- 2. Layout Principal ---
        setLayout(new BorderLayout(10, 10));

        // --- 3. Painel Superior (Lista de Voos) ---
        JPanel painelVoos = criarPainelListaVoos();

        // --- 4. Painel Inferior (Detalhes e Passageiros) ---
        JPanel painelDetalhes = criarPainelDetalhes();

        // --- 5. JSplitPane (Painel Divisor) ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                painelVoos,
                painelDetalhes);
        splitPane.setResizeWeight(0.4); // 40% para a lista de voos, 60% para detalhes
        add(splitPane, BorderLayout.CENTER);

        carregarVoosNaTabela();

        configurarListeners();
    }


    private JPanel criarPainelListaVoos() {
        JPanel painel = new JPanel(new BorderLayout(5, 5));
        painel.setBorder(BorderFactory.createTitledBorder("Voos Disponíveis"));

        // Tabela
        String[] colunasVoos = {"ID", "Nº Voo", "Origem", "Destino", "Partida", "Assentos", "Preço"};
        modeloTabelaVoos = new DefaultTableModel(colunasVoos, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaVoos = new JTable(modeloTabelaVoos);
        tabelaVoos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        painel.add(new JScrollPane(tabelaVoos), BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnRecarregarVoos = new JButton("Recarregar Lista de Voos");
        btnCancelarVoo = new JButton("Cancelar Voo Inteiro (Excluir)");
        painelBotoes.add(btnRecarregarVoos);
        painelBotoes.add(btnCancelarVoo);

        JPanel painelCriarVoo = criarPainelNovoVoo();

        abasAcoesVoo = new JTabbedPane();
        abasAcoesVoo.addTab("Ações do Voo", painelBotoes);
        abasAcoesVoo.addTab("Criar Novo Voo", painelCriarVoo);

        painel.add(abasAcoesVoo, BorderLayout.SOUTH);

        return painel;
    }

    private JPanel criarPainelNovoVoo() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        painel.add(new JLabel("Nº Voo:"), gbc);
        gbc.gridx = 1;
        txtNovoNumVoo = new JTextField(8);
        painel.add(txtNovoNumVoo, gbc);

        gbc.gridx = 2;
        painel.add(new JLabel("Assentos:"), gbc);
        gbc.gridx = 3;
        txtNovoAssentos = new JTextField(4);
        painel.add(txtNovoAssentos, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        painel.add(new JLabel("Origem:"), gbc);
        gbc.gridx = 1;
        txtNovoOrigem = new JTextField(15);
        painel.add(txtNovoOrigem, gbc);

        gbc.gridx = 2;
        painel.add(new JLabel("Destino:"), gbc);
        gbc.gridx = 3;
        txtNovoDestino = new JTextField(15);
        painel.add(txtNovoDestino, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        painel.add(new JLabel("<html>Partida:<br>(AAAA-MM-DDTHH:MM)</html>"), gbc);
        gbc.gridx = 1;
        txtNovoDataHora = new JTextField(16);
        painel.add(txtNovoDataHora, gbc);

        gbc.gridx = 2;
        painel.add(new JLabel("Preço (R$):"), gbc);
        gbc.gridx = 3;
        txtNovoPreco = new JTextField(8);
        painel.add(txtNovoPreco, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        btnSalvarNovoVoo = new JButton("Salvar Novo Voo");
        painel.add(btnSalvarNovoVoo, gbc);

        return painel;
    }


    private JPanel criarPainelDetalhes() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createTitledBorder("Detalhes do Voo Selecionado"));

        String[] colunasPassageiros = {"ID Reserva", "Nome", "Documento", "Assento"};
        modeloTabelaPassageiros = new DefaultTableModel(colunasPassageiros, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaPassageiros = new JTable(modeloTabelaPassageiros);
        tabelaPassageiros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        painel.add(new JScrollPane(tabelaPassageiros), BorderLayout.CENTER);

        JPanel painelAcoesSul = new JPanel(new BorderLayout());

        JPanel painelBotaoCancelar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnCancelarReserva = new JButton("Cancelar Reserva do Passageiro Selecionado");
        painelBotaoCancelar.add(btnCancelarReserva);
        painelAcoesSul.add(painelBotaoCancelar, BorderLayout.NORTH);

        JPanel painelNovaReserva = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelNovaReserva.setBorder(BorderFactory.createTitledBorder("Fazer Nova Reserva para este Voo"));
        painelNovaReserva.add(new JLabel("Documento Passageiro:"));
        txtDocumentoPassageiro = new JTextField(15);
        painelNovaReserva.add(txtDocumentoPassageiro);
        painelNovaReserva.add(new JLabel("Assento (ex: 10B):"));
        txtAssento = new JTextField(5);
        painelNovaReserva.add(txtAssento);
        btnFazerReserva = new JButton("Confirmar Reserva");
        painelNovaReserva.add(btnFazerReserva);
        painelAcoesSul.add(painelNovaReserva, BorderLayout.SOUTH);

        painel.add(painelAcoesSul, BorderLayout.SOUTH);
        return painel;
    }

    private void configurarListeners() {
        btnRecarregarVoos.addActionListener(e -> carregarVoosNaTabela());

        tabelaVoos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                carregarPassageirosDoVooSelecionado();
            }
        });

        btnCancelarVoo.addActionListener(e -> cancelarVooInteiro());

        btnCancelarReserva.addActionListener(e -> cancelarReservaSelecionada());

        btnFazerReserva.addActionListener(e -> fazerNovaReserva());

        btnSalvarNovoVoo.addActionListener(e -> salvarNovoVoo());
    }


    private void carregarVoosNaTabela() {
        modeloTabelaVoos.setRowCount(0);
        modeloTabelaPassageiros.setRowCount(0); // Limpa a tabela de passageiros
        try {
            List<Voo> voos = vooDAO.buscarTodos();
            for (Voo voo : voos) {
                modeloTabelaVoos.addRow(new Object[]{
                        voo.getId(),
                        voo.getNumeroVoo(),
                        voo.getOrigem(),
                        voo.getDestino(),
                        voo.getDataHoraPartida().format(formatadorDataHora),
                        voo.getAssentosDisponiveis(),
                        String.format("R$ %.2f", voo.getPreco())
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar voos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void salvarNovoVoo() {
        String numVoo = txtNovoNumVoo.getText();
        String origem = txtNovoOrigem.getText();
        String destino = txtNovoDestino.getText();
        String dataHoraStr = txtNovoDataHora.getText();
        String assentosStr = txtNovoAssentos.getText();
        String precoStr = txtNovoPreco.getText();

        if (numVoo.isEmpty() || origem.isEmpty() || destino.isEmpty() ||
                dataHoraStr.isEmpty() || assentosStr.isEmpty() || precoStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos os campos são obrigatórios.",
                    "Erro de Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDateTime dataHoraPartida;
        int assentosDisponiveis;
        double preco;

        try {
            dataHoraPartida = LocalDateTime.parse(dataHoraStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                    "Formato de Data/Hora inválido.\n" +
                            "Use o formato: AAAA-MM-DDTHH:MM (ex: 2025-10-20T09:00)",
                    "Erro de Formato", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            assentosDisponiveis = Integer.parseInt(assentosStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Número de assentos inválido. Use apenas números.",
                    "Erro de Formato", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            preco = Double.parseDouble(precoStr.replace(",", "."));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Preço inválido. Use apenas números (ex: 850.50).",
                    "Erro de Formato", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Voo novoVoo = new Voo(numVoo, origem, destino, dataHoraPartida, assentosDisponiveis, preco);

        try {
            vooDAO.salvar(novoVoo);

            JOptionPane.showMessageDialog(this,
                    "Voo " + numVoo + " salvo com sucesso! (ID: " + novoVoo.getId() + ")",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);


            limparFormularioNovoVoo();
            carregarVoosNaTabela();
            abasAcoesVoo.setSelectedIndex(0);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar o voo no banco de dados: " + e.getMessage(),
                    "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }


    private void limparFormularioNovoVoo() {
        txtNovoNumVoo.setText("");
        txtNovoOrigem.setText("");
        txtNovoDestino.setText("");
        txtNovoDataHora.setText("");
        txtNovoAssentos.setText("");
        txtNovoPreco.setText("");
    }

    private void carregarPassageirosDoVooSelecionado() {
        modeloTabelaPassageiros.setRowCount(0);
        int linhaVoo = tabelaVoos.getSelectedRow();
        if (linhaVoo == -1) return;

        int idVoo = (int) modeloTabelaVoos.getValueAt(linhaVoo, 0);

        try {
            List<Reserva> reservas = reservaDAO.buscarPorVoo(idVoo);
            for (Reserva reserva : reservas) {
                Passageiro p = passageiroDAO.buscarPorId(reserva.getIdPassageiro());
                if (p != null) {
                    modeloTabelaPassageiros.addRow(new Object[]{
                            reserva.getId(), // ID da Reserva (importante para excluir)
                            p.getNome(),
                            p.getDocumento(),
                            reserva.getNumeroAssento()
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar passageiros do voo: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void cancelarVooInteiro() {
        int linhaVoo = tabelaVoos.getSelectedRow();
        if (linhaVoo == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um voo da lista.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idVoo = (int) modeloTabelaVoos.getValueAt(linhaVoo, 0);
        String numVoo = (String) modeloTabelaVoos.getValueAt(linhaVoo, 1);

        int resposta = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja cancelar (excluir) o voo " + numVoo + "?\n" +
                        "Isso só será possível se o voo não tiver NENHUM passageiro.",
                "Confirmar Cancelamento",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (resposta != JOptionPane.YES_OPTION) return;

        try {
            boolean sucesso = vooDAO.excluir(idVoo);
            if (sucesso) {
                JOptionPane.showMessageDialog(this, "Voo cancelado com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarVoosNaTabela(); // Atualiza a lista
            } else {
                JOptionPane.showMessageDialog(this, "Não foi possível cancelar o voo.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("FOREIGN KEY constraint failed")) {
                JOptionPane.showMessageDialog(this, "Não é possível cancelar este voo, pois ele possui reservas ativas.\n" +
                        "Cancele todas as reservas deste voo primeiro.", "Erro de Restrição", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Erro de banco de dados: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void cancelarReservaSelecionada() {
        int linhaPassageiro = tabelaPassageiros.getSelectedRow();
        if (linhaPassageiro == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um passageiro na lista de reservas.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int linhaVoo = tabelaVoos.getSelectedRow();
        if (linhaVoo == -1) {
            JOptionPane.showMessageDialog(this, "Voo não está selecionado.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int idReserva = (int) modeloTabelaPassageiros.getValueAt(linhaPassageiro, 0);
        int idVoo = (int) modeloTabelaVoos.getValueAt(linhaVoo, 0);
        String nomePassageiro = (String) modeloTabelaPassageiros.getValueAt(linhaPassageiro, 1);

        int resposta = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja cancelar a reserva de " + nomePassageiro + "?",
                "Confirmar Cancelamento",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (resposta != JOptionPane.YES_OPTION) return;

        try {
            boolean reservaExcluida = reservaDAO.excluir(idReserva);

            if (reservaExcluida) {
                Voo voo = vooDAO.buscarPorId(idVoo);
                if (voo != null) {
                    voo.setAssentosDisponiveis(voo.getAssentosDisponiveis() + 1);
                    vooDAO.atualizar(voo);
                }

                JOptionPane.showMessageDialog(this, "Reserva de " + nomePassageiro + " cancelada.\n" +
                        "Um assento foi devolvido ao voo.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

                carregarVoosNaTabela();
                tabelaVoos.setRowSelectionInterval(linhaVoo, linhaVoo);
                carregarPassageirosDoVooSelecionado();
            } else {
                JOptionPane.showMessageDialog(this, "Falha ao excluir a reserva.", "Erro", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro de banco de dados ao cancelar reserva: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void fazerNovaReserva() {
        int linhaVoo = tabelaVoos.getSelectedRow();
        if (linhaVoo == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um voo na tabela superior primeiro.",
                    "Erro de Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idVoo = (int) modeloTabelaVoos.getValueAt(linhaVoo, 0);
        String documento = txtDocumentoPassageiro.getText();
        String assento = txtAssento.getText();

        if (documento.isEmpty() || assento.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Documento e Assento são obrigatórios.",
                    "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Passageiro passageiro = passageiroDAO.buscarPorDocumento(documento);
            if (passageiro == null) {
                JOptionPane.showMessageDialog(this,
                        "Passageiro não encontrado. Cadastre-o na aba 'Gerenciar Passageiros'.",
                        "Erro de Reserva", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Voo voo = vooDAO.buscarPorId(idVoo);
            if (voo.getAssentosDisponiveis() <= 0) {
                JOptionPane.showMessageDialog(this, "Voo lotado! Não há mais assentos disponíveis.",
                        "Erro de Reserva", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Reserva reserva = new Reserva(voo.getId(), passageiro.getId(), assento);
            reservaDAO.salvar(reserva);

            voo.setAssentosDisponiveis(voo.getAssentosDisponiveis() - 1);
            vooDAO.atualizar(voo);

            JOptionPane.showMessageDialog(this,
                    "Reserva realizada com sucesso para " + passageiro.getNome() + "!",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            txtDocumentoPassageiro.setText("");
            txtAssento.setText("");

            carregarVoosNaTabela();
            tabelaVoos.setRowSelectionInterval(linhaVoo, linhaVoo);
            carregarPassageirosDoVooSelecionado();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao processar a reserva: " + e.getMessage(),
                    "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
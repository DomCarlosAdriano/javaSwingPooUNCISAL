package view;

import dao.IPassageiroDAO;
import dao.PassageiroDAOImpl;
import modelo.Passageiro; // Use 'model' ou 'modelo'

import javax.swing.*;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class PainelPassageiro extends JPanel {

    private JTextField txtNome;
    private JTextField txtDocumento;
    private JTextField txtEmail;
    private JButton btnSalvar;

    private IPassageiroDAO passageiroDAO;

    public PainelPassageiro() {
        this.passageiroDAO = new PassageiroDAOImpl();

        // Configura o layout para GridBagLayout (bom para formulários)
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Espaçamento
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Linha 0: Nome ---
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Nome:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2; // Ocupa 2 colunas
        txtNome = new JTextField(30);
        add(txtNome, gbc);

        // --- Linha 1: Documento ---
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1; // Reseta para 1 coluna
        add(new JLabel("Documento (CPF/RG):"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        txtDocumento = new JTextField(30);
        add(txtDocumento, gbc);

        // --- Linha 2: Email ---
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        txtEmail = new JTextField(30);
        add(txtEmail, gbc);

        // --- Linha 3: Botão Salvar ---
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE; // Botão não estica
        gbc.anchor = GridBagConstraints.CENTER; // Centraliza
        btnSalvar = new JButton("Salvar Passageiro");
        add(btnSalvar, gbc);

        btnSalvar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                salvarPassageiro();
            }
        });
    }


    private void salvarPassageiro() {
        String nome = txtNome.getText();
        String documento = txtDocumento.getText();
        String email = txtEmail.getText();

        if (nome.isEmpty() || documento.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Nome e Documento são obrigatórios.",
                    "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Passageiro passageiro = new Passageiro(nome, documento, email);

        try {
            passageiroDAO.salvar(passageiro);

            JOptionPane.showMessageDialog(this,
                    "Passageiro salvo com sucesso! (ID: " + passageiro.getId() + ")",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            txtNome.setText("");
            txtDocumento.setText("");
            txtEmail.setText("");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao salvar passageiro: " + e.getMessage(),
                    "Erro de Banco de Dados",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
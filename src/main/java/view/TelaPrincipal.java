package view;

import javax.swing.*;
import java.awt.BorderLayout;

public class TelaPrincipal extends JFrame {

    private JTabbedPane abas;

    public TelaPrincipal() {
        setTitle("Sistema de Compra de Passagens Aéreas");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        abas = new JTabbedPane();

        JPanel painelBusca = new PainelBuscaVoo();
        abas.addTab("Buscar Voos", painelBusca);

        JPanel painelPassageiro = new PainelPassageiro();
        abas.addTab("Cadastrar Passageiro", painelPassageiro);

        JPanel painelGerenciarPassageiros = new PainelGerenciarPassageiros();
        abas.addTab("Gerenciar Passageiros (Admin)", painelGerenciarPassageiros);

        JPanel painelGerenciarVoos = new PainelGerenciarVoos();
        abas.addTab("Gerenciar Voos (Admin)", painelGerenciarVoos);

        add(abas, BorderLayout.CENTER);
    }

    public void iniciar() {
        setVisible(true);
    }
}
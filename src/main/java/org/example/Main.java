package org.example;

import dao.ConexaoBD;
import view.TelaPrincipal; // 1. Importa a nova tela
import javax.swing.SwingUtilities; // 2. Importa o SwingUtilities

/**
 * Classe principal para inicializar o banco de dados
 * e iniciar a aplicação Swing.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("Iniciando aplicação...");


        ConexaoBD.criarTabelas();

        System.out.println("Banco de dados pronto.");

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TelaPrincipal tela = new TelaPrincipal();
                tela.iniciar();
            }
        });

        System.out.println("Interface gráfica iniciada.");
    }
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import banco.BancoDados;
import banco.BancoUsuarios;
import com.formdev.flatlaf.FlatLightLaf;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import ui.LoginUI;
import ui.UpdaterUI;
import static ui.UpdaterUI.URL_VERSAO;
import static ui.UpdaterUI.VERSAO_ATUAL;

/**
 *
 * @author Maxwell
 */
public class StartApp {

    public static void main(String[] args) throws Exception {
        
        

        FlatLightLaf.setup();
        UIManager.put("Button.arc", 999);
        UIManager.put("ProgressBar.arc", 999);
        UIManager.put("Component.arrowType", "chevron");
        UIManager.put("Component.focusWidth", 1);
        UIManager.put("TextComponent.arc", 999);

//        LoginUI login = new LoginUI(); Habilitar depois
//        login.setVisible(true);
        verificarNaInicializacao();
//         Opção 1: Abrir a janela de atualização
//        SwingUtilities.invokeLater(() -> {
//            new Updater().setVisible(true);
//        });

        BancoUsuarios bancoUsuarios = new BancoUsuarios();
        BancoDados bancoDados = new BancoDados();
        
        LoginUI login = new LoginUI(bancoUsuarios, bancoDados);
        login.setVisible(true);
//        GeradorLicencaUI gl = new GeradorLicencaUI();
//        gl.setVisible(true);
    }//main  

    /**
     * ALTERNATIVA: Verificar silenciosamente ao iniciar o programa Chame este
     * método no main() do seu programa principal
     */
    public static void verificarNaInicializacao() {
        new Thread(() -> {
            try {
                URL url = new URL(URL_VERSAO);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(3000);
                conn.setReadTimeout(3000);

                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()))) {
                    String versaoRemota = reader.readLine().trim();

                    // Compara versões
                    String[] partsA = versaoRemota.split("\\.");
                    String[] partsB = VERSAO_ATUAL.split("\\.");
                    boolean temAtualizacao = false;

                    for (int i = 0; i < Math.max(partsA.length, partsB.length); i++) {
                        int a = (i < partsA.length) ? Integer.parseInt(partsA[i]) : 0;
                        int b = (i < partsB.length) ? Integer.parseInt(partsB[i]) : 0;
                        if (a > b) {
                            temAtualizacao = true;
                            break;
                        }
                        if (a < b) {
                            break;
                        }
                    }

                    if (temAtualizacao) {
                        final boolean[] result = {false};
                        SwingUtilities.invokeAndWait(() -> {
                            int resposta = JOptionPane.showConfirmDialog(null, "NOVA VERSÃO " + versaoRemota + " DISPONÍVEL!\nDESEJA ATUALIZAR AGORA?",
                                    "ATUALIZAÇÃO",
                                    JOptionPane.YES_NO_OPTION);
                            result[0] = (resposta == JOptionPane.YES_OPTION);
                        });

                        if (result[0]) {
                            SwingUtilities.invokeLater(() -> new UpdaterUI().setVisible(true));
                        }
                    }
                }
            } catch (IOException | InterruptedException | NumberFormatException | InvocationTargetException e) {
                // Silencioso: sem internet ou servidor fora do ar = não faz nada
                System.out.println("Verificação de atualização falhou: " + e.getMessage());
            }
        }).start();
    }//VerificarNaInicializacao - inserido construtor

}//StartApp - fim de classe

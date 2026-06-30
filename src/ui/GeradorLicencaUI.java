/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import banco.BancoDados;
import banco.BancoUsuarios;
import java.io.File;
import java.security.PrivateKey;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import util.RSAUtil;

/**
 *
 * @author Maxwell
 */
public class GeradorLicencaUI extends javax.swing.JFrame {

    private BancoUsuarios bancoUsuarios;
    private BancoDados bancoDados;
    private PrivateKey chavePrivada;
    private LoginUI login;

    /**
     * Creates new form GeradorLicencaUI
     * @param bancoUsuarios
     * @param bancoDados
     * @param login
     */
    public GeradorLicencaUI(BancoUsuarios bancoUsuarios, BancoDados bancoDados, LoginUI login) {
        initComponents();
        this.bancoUsuarios = bancoUsuarios;
        this.bancoDados = bancoDados;
        this.login = login;
        verificarChaves();
        //Imagem do software
        ImageIcon iconUCKG = new ImageIcon(this.getClass().getClassLoader().getResource("img/ultracipa_27x27.png"));
        this.setIconImage(iconUCKG.getImage());

        try {
            bancoDados = new BancoDados();
            // Carrega chave privada (deve existir na pasta "chaves/")
            chavePrivada = RSAUtil.carregarPrivada("chaves/chave_privada.key");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "ERRO AO INICIAR: " + e.getMessage()
                    + "\nCRIE AS CHAVES PRIMEIRO (menu Ferramentas).");
        }

    }//Construtor

    private void verificarChaves() {
        java.io.File chavePrivada = new java.io.File("chaves/chave_privada.key");
        java.io.File chavePublica = new java.io.File("chaves/chave_publica.key");

        if (chavePrivada.exists() && chavePublica.exists()) {
            menuGerarChaves.setEnabled(false); // ← desativa o submenu
        }
    }

// Ação do submenu "Gerar Chaves RSA"
    private void menuGerarChaves() {

        // Verifica se as chaves já existem
        java.io.File chavePrivada = new java.io.File("chaves/chave_privada.key");
        java.io.File chavePublica = new java.io.File("chaves/chave_publica.key");

        if (chavePrivada.exists() && chavePublica.exists()) {
            // Avisa que o item está desativado e o motivo
            JOptionPane.showMessageDialog(null,
                    "ESTE ITEM ESTÁ DESATIVADO!\n\n"
                    + "AS CHAVES RSA JÁ FORAM GERADAS ANTERIORMENTE.\n"
                    + "POR SEGURANÇA, NÃO É PERMITIVO GERAR NOVAS CHAVES,\n"
                    + "POIS TODAS AS LICENÇAS EMITIDAS DEIXARIAM DE FUNCIONAR.",
                    "Item desativado",
                    JOptionPane.WARNING_MESSAGE);

            menuGerarChaves.setEnabled(false); // garante que fica desativado
            return;
        }

        // Só chega aqui se as chaves ainda não existem
        try {
            new java.io.File("chaves").mkdirs();
            RSAUtil.gerarChaves("chaves");

            JOptionPane.showMessageDialog(null,
                    "CHAVES GERADAS COM SUCESSO!\n"
                    + "Pasta: chaves/\n"
                    + "- chave_privada.key\n"
                    + "- chave_publica.key\n\n"
                    + "O menu será desativado por segurança.",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);

            menuGerarChaves.setEnabled(false); // ← desativa após gerar
            
            

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "ERRO AO TENTAR GERAR AS CHAVES: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }//menuGerarChaves

    // Dentro da sua classe principal (ex: GeradorLicencaUI ou MainFrame)
// Método auxiliar para abrir qualquer JInternalFrame
    private void abrirInternalFrame(JInternalFrame novoFrame) {
        //                                          ↑ novoFrame aqui

        // 1. Verifica se já existe um frame do mesmo tipo aberto
        for (JInternalFrame frame : this.desktop.getAllFrames()) {
            //                 ↑ frame aqui — nome diferente, sem conflito
            if (frame.getClass() == novoFrame.getClass()) {
                //                   ↑ compara com novoFrame
                try {
                    frame.setSelected(true);
                    frame.setIcon(false);
                } catch (java.beans.PropertyVetoException e) {
                    e.printStackTrace();
                }
                return;
            }
        }

        // 2. Fecha todos os frames abertos antes de abrir o novo
        for (JInternalFrame frame : this.desktop.getAllFrames()) {
            frame.dispose(); // ← fecha e libera da memória
        }

        // 2. Configurações padrão para todos os frames
        novoFrame.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        novoFrame.setVisible(true);

        // 3. Adiciona ao desktop
        this.desktop.add(novoFrame);

        // 4. Traz para frente e centraliza
        try {
            novoFrame.setSelected(true);
            if (novoFrame.getLocation().x == 0 && novoFrame.getLocation().y == 0) {
                novoFrame.setLocation(
                        (this.desktop.getWidth() - novoFrame.getWidth()) / 2,
                        (this.desktop.getHeight() - novoFrame.getHeight()) / 2
                );
            }
        } catch (java.beans.PropertyVetoException e) {
            e.printStackTrace();
        }
    }//abrirInternalFrame


    private void gerarChaves() {
        int resp = JOptionPane.showConfirmDialog(this,
                "ATENÇÃO: SE JÁ TEM CHAVES GERADAS, ISSO VAI SOBRESCREVÊ-LAS!\n"
                + "LICENÇAS ANTIGAS DEIXARÃO DE FUNCIONAR.\n\nDESEJA CONTINUAR?",
                "Gerar novas chaves RSA", JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (resp == JOptionPane.YES_OPTION) {
            try {
                new File("chaves").mkdirs();
                RSAUtil.gerarChaves("chaves");
                JOptionPane.showMessageDialog(this,
                        "CHAVES GERADAS COM SUCESSO!\nCOPIE 'chaves_publica.key' PARA O PROJEOT DO CLIENTE.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "ERRO AO TENTAR GERAR A CHAVE: " + ex.getMessage());
            }
        }
    }//gerarChaves

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        desktop = new javax.swing.JDesktopPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        menuGerarChaves = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("UltraCIPAKeyGen - Gerador de licenças");
        setBackground(new java.awt.Color(255, 255, 255));
        setResizable(false);
        getContentPane().setLayout(new javax.swing.OverlayLayout(getContentPane()));

        desktop.setPreferredSize(new java.awt.Dimension(1200, 800));
        desktop.setLayout(new javax.swing.OverlayLayout(desktop));
        getContentPane().add(desktop);

        jMenu1.setText("Arquivo");

        jMenuItem4.setText("Sair");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem4);

        jMenuItem1.setText("Voltar ao Login");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Ferramenta");

        menuGerarChaves.setText("Gerar chaves RSA");
        menuGerarChaves.setToolTipText("UMA VEZ QUE AS CHAVES FORAM GERADAS, ESTE ITEM É DESABILITADO POR SEGURANÇA.");
        menuGerarChaves.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuGerarChavesActionPerformed(evt);
            }
        });
        jMenu2.add(menuGerarChaves);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("Licenças");

        jMenuItem2.setText("Gerar licença");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem2);

        jMenuItem3.setText("Consultar licenças");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem3);

        jMenuBar1.add(jMenu3);

        jMenu4.setText("Ajuda");
        jMenuBar1.add(jMenu4);

        setJMenuBar(jMenuBar1);

        setSize(new java.awt.Dimension(1018, 713));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        LicencaInternalFrame lif = new LicencaInternalFrame(bancoDados, chavePrivada);
        abrirInternalFrame(lif);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        ListarInternalFrame list = new ListarInternalFrame();
        abrirInternalFrame(list);
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void menuGerarChavesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuGerarChavesActionPerformed
       menuGerarChaves();
    }//GEN-LAST:event_menuGerarChavesActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        this.setVisible(false);
        login.setVisible(true);
    }//GEN-LAST:event_jMenuItem1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDesktopPane desktop;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem menuGerarChaves;
    // End of variables declaration//GEN-END:variables
}

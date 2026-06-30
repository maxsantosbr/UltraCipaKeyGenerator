/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import java.awt.Color;
import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/**
 *
 * @author Maxwell
 */
public class UpdaterUI extends javax.swing.JFrame {

    // =============================================
    // CONFIGURAÇÕES - ALTERE AQUI
    // =============================================
    // Versão atual do seu programa (ex: "1.0", "1.1", "2.0")
    public static final String VERSAO_ATUAL = "1.2.0";

    // URL do arquivo de texto com a versão mais recente
    // Exemplo com GitHub Raw:
    // https://raw.githubusercontent.com/SEU_USUARIO/SEU_REPO/main/version.txt
    public static final String URL_VERSAO = "https://raw.githubusercontent.com/maxsantosbr/UltraCipaKeyGenerator/main/version.txt";

    // URL para baixar o novo .jar
    // Exemplo com GitHub Releases:
    // https://github.com/SEU_USUARIO/SEU_REPO/releases/latest/download/MeuPrograma.jar
    private static final String URL_DOWNLOAD_JAR = "https://github.com/maxsantosbr/UltraCipaKeyGenerator/releases/latest/download/UltraCipaKeyGenerator.jar";

    // Nome do arquivo jar que será baixado
    private static final String NOME_JAR = "UltraCipaKeyGenerator.jar";

    /**
     * Creates new form VerificarAtualizacoes
     */
    public UpdaterUI() {
        initComponents();
        lblVersao.setText(VERSAO_ATUAL);
        lblStatus.setVisible(false);
    }//Construtor

    /**
     * PASSO 1: Verifica se existe versão mais nova
     */
    private void verificarAtualizacao() {
        btnVerificar.setEnabled(false);
        lblStatus.setVisible(true);
        lblStatus.setText("Mantenha o software aberto. Estamos verificando...");
        lblStatus.setForeground(Color.BLUE);

        // Usa SwingWorker para não travar a interface
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                return lerVersaoRemota();
            }

            @Override
            protected void done() {
                try {
                    String versaoRemota = get();
                    compararVersoes(versaoRemota);
                } catch (InterruptedException | ExecutionException ex) {
                    lblStatus.setText("Erro ao verificar: " + ex.getMessage());
                    lblStatus.setForeground(Color.RED);
                    btnVerificar.setEnabled(true);
                }
            }
        };
        worker.execute();
    }//verificarAtualizacao

    /**
     * PASSO 2: Lê o arquivo version.txt do servidor O arquivo version.txt deve
     * conter APENAS o número da versão, ex: 1.1
     */
    private String lerVersaoRemota() throws Exception {
        URL url = new URL(URL_VERSAO);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000); // timeout de 5 segundos
        conn.setReadTimeout(5000);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {
            return reader.readLine().trim();
        }
    }//lerVersaoRemota

    /**
     * PASSO 3: Compara versão local com remota
     */
    private void compararVersoes(String versaoRemota) {
        if (versaoRemota == null || versaoRemota.isEmpty()) {
            lblStatus.setVisible(true);
            lblStatus.setText("Não foi possível obter a versão remota.");
            lblStatus.setForeground(Color.RED);
            btnVerificar.setEnabled(true);
            return;
        }
        lblStatus.setVisible(true);
        lblStatus.setText("Versão disponível no servidor: " + versaoRemota);

        // Compara como números (ex: 1.0 < 1.1 < 2.0)
        if (versaoMaiorQue(versaoRemota, VERSAO_ATUAL)) {
            lblStatus.setVisible(true);
            // Há atualização disponível!
            lblStatus.setForeground(new Color(0, 128, 0)); // verde
            int resposta = JOptionPane.showConfirmDialog(this,
                    "NOVA VERSÃO DISPONÍVEL: " + versaoRemota + "\n"
                    + "DESEJA BAIXAR E INSTALAR AGORA?",
                    "ATUALIZAÇÃO DISPONÍVEL",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (resposta == JOptionPane.YES_OPTION) {
                baixarAtualizacao();
            } else {
                btnVerificar.setEnabled(true);
            }
        } else {
            lblStatus.setVisible(true);
            lblStatus.setText("Você já tem a versão mais recente! (" + VERSAO_ATUAL + ")");
            lblStatus.setForeground(new Color(0, 128, 0));
            btnVerificar.setEnabled(true);
        }
    }//compararVersoes

    /**
     * Verifica se versão A é maior que versão B Funciona com versões como
     * "1.0.0", "1.10.0", "2.5.1"
     */
    private boolean versaoMaiorQue(String versaoA, String versaoB) {
        String[] partsA = versaoA.split("\\.");
        String[] partsB = versaoB.split("\\.");
        int maxLen = Math.max(partsA.length, partsB.length);

        for (int i = 0; i < maxLen; i++) {
            int a = (i < partsA.length) ? Integer.parseInt(partsA[i]) : 0;
            int b = (i < partsB.length) ? Integer.parseInt(partsB[i]) : 0;
            if (a > b) {
                return true;
            }
            if (a < b) {
                return false;
            }
        }
        return false;
    }//versaoMaiorQue

    /**
     * PASSO 4: Baixa o novo .jar
     */
    private void baixarAtualizacao() {
        lblStatus.setVisible(true);
        lblStatus.setText("Baixando atualização...");
        barraProgresso.setValue(0);
        barraProgresso.setString("0%");
        btnVerificar.setEnabled(false);

        SwingWorker<Boolean, Integer> worker = new SwingWorker<Boolean, Integer>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                URL url = new URL(URL_DOWNLOAD_JAR);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                int tamanhoTotal = conn.getContentLength();

                File arquivoDestino = new File(NOME_JAR + ".novo");

                try (InputStream in = conn.getInputStream();
                        FileOutputStream out = new FileOutputStream(arquivoDestino)) {

                    byte[] buffer = new byte[4096];
                    int bytesLidos;
                    long totalBaixado = 0;

                    while ((bytesLidos = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesLidos);
                        totalBaixado += bytesLidos;

                        if (tamanhoTotal > 0) {
                            int progresso = (int) ((totalBaixado * 100) / tamanhoTotal);
                            publish(progresso);
                        }
                    }
                }
                return true;
            }

            @Override
            protected void process(java.util.List<Integer> chunks) {
                int ultimo = chunks.get(chunks.size() - 1);
                barraProgresso.setValue(ultimo);
                barraProgresso.setString(ultimo + "%");
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        instalarAtualizacao();
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    lblStatus.setVisible(true);
                    lblStatus.setText("Erro no download: " + ex.getMessage());
                    lblStatus.setForeground(Color.RED);
                    btnVerificar.setEnabled(true);
                }
            }
        };
        worker.execute();
    }//baixarAtualizacao

    /**
     * PASSO 5: Substitui o jar antigo pelo novo e reinicia Estratégia: -
     * Renomeia o jar atual para .bak - Renomeia o novo jar para o nome correto
     * - Abre o novo jar com java -jar - Fecha este processo
     */
    private void instalarAtualizacao() {
        try {
            File jarNovo = new File(NOME_JAR + ".novo");
            File jarAtual = new File(NOME_JAR);
            File jarBackup = new File(NOME_JAR + ".bak");

            // Remove backup antigo se existir
            if (jarBackup.exists()) {
                jarBackup.delete();
            }

            // Faz backup do jar atual
            if (jarAtual.exists()) {
                jarAtual.renameTo(jarBackup);
            }

            // Renomeia o novo para o nome correto
            jarNovo.renameTo(jarAtual);

            JOptionPane.showMessageDialog(this,
                    "ATUALIZAÇÃO CONCLUÍDA COM SUCESSO!\nO PROGRAMA SERÁ REINICIADO.",
                    "SUCESSO",
                    JOptionPane.INFORMATION_MESSAGE);

            // Inicia o novo jar
            String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
            ProcessBuilder pb = new ProcessBuilder(javaBin, "-jar", NOME_JAR);
            pb.start();

            // Fecha o programa atual
            System.exit(0);

        } catch (HeadlessException | IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "ERRO AO TENTAR INSTALAR: " + ex.getMessage()
                    + "\nO ARQUIVO FOI BAIXADO COMO: " + NOME_JAR + ".novo",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            lblStatus.setVisible(true);
            lblStatus.setForeground(Color.RED);
            btnVerificar.setEnabled(true);
        }
    }//instalarAtualizacao

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelPrincipal = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        lblVersao = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        barraProgresso = new javax.swing.JProgressBar();
        jButton1 = new javax.swing.JButton();
        btnVerificar = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Verificador de atualizações");
        setUndecorated(true);
        setResizable(false);
        getContentPane().setLayout(new javax.swing.OverlayLayout(getContentPane()));

        panelPrincipal.setBackground(new java.awt.Color(255, 255, 255));
        panelPrincipal.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("VERIFICADOR DE ATUALIZAÇÕES");
        panelPrincipal.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 30, 320, -1));

        lblVersao.setFont(new java.awt.Font("Tahoma", 2, 18)); // NOI18N
        lblVersao.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblVersao.setText("numero da versão");
        panelPrincipal.add(lblVersao, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 80, 150, 27));

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Versão instalada:");
        panelPrincipal.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 80, 140, 27));

        barraProgresso.setBackground(new java.awt.Color(204, 204, 204));
        barraProgresso.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        barraProgresso.setForeground(new java.awt.Color(0, 176, 80));
        barraProgresso.setToolTipText("");
        barraProgresso.setBorderPainted(false);
        barraProgresso.setOpaque(true);
        barraProgresso.setStringPainted(true);
        panelPrincipal.add(barraProgresso, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 180, 500, 27));

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-cancelar-21.png"))); // NOI18N
        jButton1.setText("FECHAR JANELA");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        panelPrincipal.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 240, 200, 36));

        btnVerificar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-atualizar-21.png"))); // NOI18N
        btnVerificar.setText("VERIFICAR ATUALIZAÇÕES");
        btnVerificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerificarActionPerformed(evt);
            }
        });
        panelPrincipal.add(btnVerificar, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 240, 270, 36));

        lblStatus.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        lblStatus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStatus.setText("status");
        panelPrincipal.add(lblStatus, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 140, 500, -1));

        getContentPane().add(panelPrincipal);

        setSize(new java.awt.Dimension(600, 300));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
       
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnVerificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerificarActionPerformed
//        new Thread(() -> {
//            try {
//                barraProgresso.setValue(0);
//                barraProgresso.setString("Verificando...");
//                Thread.sleep(1000);
//
//                barraProgresso.setValue(30);
//                barraProgresso.setString("Baixando dados...");
//                Thread.sleep(1000);
//
//                barraProgresso.setValue(70);
//                barraProgresso.setString("Processando...");
//                Thread.sleep(1000);
//
//                barraProgresso.setValue(100);
//                barraProgresso.setString("Concluído");
//
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }).start();
        verificarAtualizacao();
    }//GEN-LAST:event_btnVerificarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(UpdaterUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(UpdaterUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(UpdaterUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UpdaterUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new UpdaterUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar barraProgresso;
    private javax.swing.JButton btnVerificar;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblVersao;
    private javax.swing.JPanel panelPrincipal;
    // End of variables declaration//GEN-END:variables
}

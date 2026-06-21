/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import dao.LoginDAO;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.Usuario;
import util.SessaoUsuario;

/**
 *
 * @author Maxwell
 */
public class CadastroUsuario extends javax.swing.JFrame {

    private LoginDAO dao = new LoginDAO();
    private int idSelecionado = -1;

    /**
     * Creates new form CadastroUsuario
     */
    public CadastroUsuario() {
        initComponents();
        configurarTabela();
        carregarTabela();
        //Imagem do software
        ImageIcon iconUCKG = new ImageIcon(this.getClass().getClassLoader().getResource("img/UltraCipaKEY.png"));
        this.setIconImage(iconUCKG.getImage());
        // Somente admin pode cadastrar usuários
        if (!SessaoUsuario.isAdmin()) {
            JOptionPane.showMessageDialog(this, "ACESSO NEGADO!");
            this.dispose();
        }

    }//Construtor

    // ===== SALVAR (INSERT ou UPDATE) =====
    private void btnSalvar() {
        char[] senha = txtSenhaCadastro.getPassword();
        String campoSenha = new String(senha);
        char[] repetirSenha = txtRepetirSenhaCadastro.getPassword();
        String campoRepetirSenha = new String(repetirSenha);
        int combo = comboPerfil.getSelectedIndex();

        if (txtNomeCadastro.getText().isEmpty() || txtEmailCadastro.getText().isEmpty() || campoSenha.isEmpty() || campoRepetirSenha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "PREENCHA TODOS OS CAMPOS!");
            return;
        }

        if (!campoSenha.equals(campoRepetirSenha)) {
            JOptionPane.showMessageDialog(this, "OS CAMPOS SENHA E REPETIR SENHA NÃO SÃO IGUAIS!");
            txtSenhaCadastro.requestFocus();
            return;
        }

        if (combo == 0) {
            JOptionPane.showMessageDialog(this, "SELECIONE O PERFIL DO USUÁRIO!");
            comboPerfil.requestFocus();
            return;
        }

        Usuario u = new Usuario();
        u.setNomeCompleto(txtNomeCadastro.getText().trim());
        u.setEmail(txtEmailCadastro.getText().trim());
        u.setSenha(new String(txtSenhaCadastro.getPassword()));
        u.setPerfil(comboPerfil.getSelectedItem().toString());
        u.setAtivo(checkAtivo.isSelected() ? 1 : 0);

        if (idSelecionado == -1) {
            // INSERT
            if (dao.inserir(u)) {
                JOptionPane.showMessageDialog(this, "USUÁRIO CADASTRADO COM SUCESSO!");
                limparCampos();
                carregarTabela();
            }
        } else {
            // UPDATE
            u.setId(idSelecionado);
            if (dao.atualizar(u)) {
                JOptionPane.showMessageDialog(this, "USUÁRIO ATUALIZADO COM SUCESSO!");
                limparCampos();
                carregarTabela();
            }
        }

    }

    private void configurarTabela() {
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Nome", "E-mail", "Perfil", "Status"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // impede edição direta na tabela
            }
        };
        tblUsuarios.setModel(model);

        // Oculta a coluna ID (mantém o valor mas não mostra)
        tblUsuarios.getColumnModel().getColumn(0).setMinWidth(0);
        tblUsuarios.getColumnModel().getColumn(0).setMaxWidth(0);
        tblUsuarios.getColumnModel().getColumn(0).setWidth(0);
    }

    // ===== CARREGAR TABELA =====
    private void carregarTabela() {
        DefaultTableModel model = (DefaultTableModel) tblUsuarios.getModel();
        model.setRowCount(0); // limpa a tabela
        dao.listarTodos().forEach((u) -> {
            model.addRow(new Object[]{
                u.getId(),
                u.getNomeCompleto(),
                u.getEmail(),
                u.getPerfil(),
                u.getAtivo() == 1 ? "Ativo" : "Bloqueado"
            });
        });
    }

    // ===== CLIQUE NA TABELA (carregar para edição) =====
    private void tblUsuariosMouseClicked() {
        btnSalvarUsuario.setEnabled(false);
        int linha = tblUsuarios.getSelectedRow();
        if (linha >= 0) {
            idSelecionado = (int) tblUsuarios.getValueAt(linha, 0);
            Usuario u = dao.buscarPorId(idSelecionado);
            if (u != null) {
                txtNomeCadastro.setText(u.getNomeCompleto());
                txtEmailCadastro.setText(u.getEmail());
                txtSenhaCadastro.setText(""); // não carrega a senha por segurança
                comboPerfil.setSelectedItem(u.getPerfil());
                checkAtivo.setSelected(u.getAtivo() == 1);
            }
        }
    }

    // ===== EXCLUIR =====
    private void btnExcluir() {
        if (idSelecionado == -1) {
            JOptionPane.showMessageDialog(this, "SELECIONE UM USUÁRIO NA TABELA (SE HOUVER)!");
            return;
        }
        int op = JOptionPane.showConfirmDialog(this,
                "DESEJA EXCLUIR ESTE USUÁRIO?", "EXCLUIR", JOptionPane.YES_NO_OPTION);
        if (op == JOptionPane.YES_OPTION) {
            if (dao.deletar(idSelecionado)) {
                JOptionPane.showMessageDialog(this, "USUÁRIO EXCLUÍDO!");
                limparCampos();
                carregarTabela();
            }
        }
    }

    // ===== NOVO =====
    private void btnNovoActionPerformed(ActionEvent evt) {
        limparCampos();
    }

    private void limparCampos() {
        idSelecionado = -1;
        txtNomeCadastro.setText("");
        txtEmailCadastro.setText("");
        txtSenhaCadastro.setText("");
        txtRepetirSenhaCadastro.setText("");
        comboPerfil.setSelectedIndex(0);
        checkAtivo.setSelected(false);
        tblUsuarios.clearSelection();
        btnSalvarUsuario.setEnabled(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelPrincipal = new javax.swing.JPanel();
        txtNomeCadastro = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        txtRepetirSenhaCadastro = new javax.swing.JPasswordField();
        jLabel2 = new javax.swing.JLabel();
        txtSenhaCadastro = new javax.swing.JPasswordField();
        jLabel3 = new javax.swing.JLabel();
        comboPerfil = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        checkAtivo = new javax.swing.JCheckBox();
        btnExcluirUsuario = new javax.swing.JButton();
        btnLimparCamposCadastroUsuarios = new javax.swing.JButton();
        btnNovoUsuario = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblUsuarios = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        txtEmailCadastro = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        btnSalvarUsuario = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Cadastro e gerenciamentos de usuários");
        getContentPane().setLayout(new javax.swing.OverlayLayout(getContentPane()));

        panelPrincipal.setBackground(new java.awt.Color(255, 255, 255));
        panelPrincipal.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtNomeCadastro.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtNomeCadastro.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtNomeCadastro.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtNomeCadastroKeyPressed(evt);
            }
        });
        panelPrincipal.add(txtNomeCadastro, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 60, 420, 35));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setText("Status");
        panelPrincipal.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 410, -1, -1));

        txtRepetirSenhaCadastro.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtRepetirSenhaCadastro.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtRepetirSenhaCadastro.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtRepetirSenhaCadastroKeyPressed(evt);
            }
        });
        panelPrincipal.add(txtRepetirSenhaCadastro, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 350, 420, 35));

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel2.setText("Usuários cadastrados");
        panelPrincipal.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 30, -1, -1));

        txtSenhaCadastro.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtSenhaCadastro.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtSenhaCadastro.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSenhaCadastroKeyPressed(evt);
            }
        });
        panelPrincipal.add(txtSenhaCadastro, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 250, 420, 35));

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setText("Senha");
        panelPrincipal.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 210, -1, -1));

        comboPerfil.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        comboPerfil.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Selecione", "Admin", "Gerente", "Operador" }));
        panelPrincipal.add(comboPerfil, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 450, 160, 30));

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel4.setText("Repetir senha");
        panelPrincipal.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 310, -1, -1));

        checkAtivo.setBackground(new java.awt.Color(255, 255, 255));
        checkAtivo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        checkAtivo.setText("ATIVO");
        panelPrincipal.add(checkAtivo, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 450, -1, -1));

        btnExcluirUsuario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-lixeira-21.png"))); // NOI18N
        btnExcluirUsuario.setText("EXCLUIR");
        btnExcluirUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirUsuarioActionPerformed(evt);
            }
        });
        panelPrincipal.add(btnExcluirUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 520, 140, 40));

        btnLimparCamposCadastroUsuarios.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-limpar-aqruivo-21.png"))); // NOI18N
        btnLimparCamposCadastroUsuarios.setText("LIMPAR");
        btnLimparCamposCadastroUsuarios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimparCamposCadastroUsuariosActionPerformed(evt);
            }
        });
        panelPrincipal.add(btnLimparCamposCadastroUsuarios, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 520, 140, 40));

        btnNovoUsuario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-cadastro-21.png"))); // NOI18N
        btnNovoUsuario.setText("NOVO");
        btnNovoUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNovoUsuarioActionPerformed(evt);
            }
        });
        panelPrincipal.add(btnNovoUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 520, 140, 40));

        tblUsuarios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblUsuarios.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblUsuariosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblUsuarios);

        panelPrincipal.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 60, 610, 390));

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setText("E-mail");
        panelPrincipal.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 120, -1, -1));

        txtEmailCadastro.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtEmailCadastro.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtEmailCadastro.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtEmailCadastroKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtEmailCadastroKeyReleased(evt);
            }
        });
        panelPrincipal.add(txtEmailCadastro, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 150, 420, 35));

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel6.setText("Nome");
        panelPrincipal.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 30, -1, -1));

        btnSalvarUsuario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-salvar-21.png"))); // NOI18N
        btnSalvarUsuario.setText("SALVAR");
        btnSalvarUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarUsuarioActionPerformed(evt);
            }
        });
        panelPrincipal.add(btnSalvarUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 520, 140, 40));

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel7.setText("Perfil");
        panelPrincipal.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 410, -1, -1));

        getContentPane().add(panelPrincipal);

        setSize(new java.awt.Dimension(1218, 647));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnExcluirUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirUsuarioActionPerformed
        btnExcluir();
    }//GEN-LAST:event_btnExcluirUsuarioActionPerformed

    private void btnLimparCamposCadastroUsuariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimparCamposCadastroUsuariosActionPerformed
        limparCampos();
    }//GEN-LAST:event_btnLimparCamposCadastroUsuariosActionPerformed

    private void btnNovoUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNovoUsuarioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnNovoUsuarioActionPerformed

    private void btnSalvarUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarUsuarioActionPerformed
        btnSalvar();
    }//GEN-LAST:event_btnSalvarUsuarioActionPerformed

    private void tblUsuariosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblUsuariosMouseClicked
        tblUsuariosMouseClicked();
    }//GEN-LAST:event_tblUsuariosMouseClicked

    private void txtNomeCadastroKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNomeCadastroKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtEmailCadastro.requestFocus();
        }
    }//GEN-LAST:event_txtNomeCadastroKeyPressed

    private void txtEmailCadastroKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtEmailCadastroKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtSenhaCadastro.requestFocus();
        }
    }//GEN-LAST:event_txtEmailCadastroKeyPressed

    private void txtSenhaCadastroKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSenhaCadastroKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtRepetirSenhaCadastro.requestFocus();
        }
    }//GEN-LAST:event_txtSenhaCadastroKeyPressed

    private void txtRepetirSenhaCadastroKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtRepetirSenhaCadastroKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            comboPerfil.requestFocus();
        }
    }//GEN-LAST:event_txtRepetirSenhaCadastroKeyPressed

    private void txtEmailCadastroKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtEmailCadastroKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEmailCadastroKeyReleased

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
            java.util.logging.Logger.getLogger(CadastroUsuario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CadastroUsuario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CadastroUsuario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CadastroUsuario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CadastroUsuario().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExcluirUsuario;
    private javax.swing.JButton btnLimparCamposCadastroUsuarios;
    private javax.swing.JButton btnNovoUsuario;
    private javax.swing.JButton btnSalvarUsuario;
    private javax.swing.JCheckBox checkAtivo;
    private javax.swing.JComboBox<String> comboPerfil;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel panelPrincipal;
    private javax.swing.JTable tblUsuarios;
    private javax.swing.JTextField txtEmailCadastro;
    private javax.swing.JTextField txtNomeCadastro;
    private javax.swing.JPasswordField txtRepetirSenhaCadastro;
    private javax.swing.JPasswordField txtSenhaCadastro;
    // End of variables declaration//GEN-END:variables
}

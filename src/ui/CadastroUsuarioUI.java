/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import banco.BancoUsuarios;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import net.proteanit.sql.DbUtils;

/**
 *
 * @author Maxwell
 */
public class CadastroUsuarioUI extends javax.swing.JFrame {

    private int idSelecionado = -1;
    private int idSelected;

    public LoginUI login;

    private BancoUsuarios bancoUsuarios;

//    private static final String URL = "jdbc:sqlite:usuarios.db";
//    private Connection conn;
    /**
     * Creates new form CadastroUsuario
     *
     * @param login
     * @param bancoUsuarios
     */
    public CadastroUsuarioUI(LoginUI login, BancoUsuarios bancoUsuarios) {
        initComponents();
        this.login = login;
        this.bancoUsuarios = bancoUsuarios;
        //Imagem do software
        ImageIcon iconUCKG = new ImageIcon(this.getClass().getClassLoader().getResource("img/ultracipa_27x27.png"));
        this.setIconImage(iconUCKG.getImage());
        lerTabela();

    }//Construtor

    private void atualizarUsuario() {
        int linhaSelecionada = tblUsuarios.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "SELECIONE UM USUÁRIO NA TABELA PARA ATUALIZAR SEUS DADOS!");
            return;
        }
        char[] senha = txtSenhaCadastro.getPassword();
        String campoSenha = new String(senha);
        char[] repetirSenha = txtRepetirSenhaCadastro.getPassword();
        String campoRepetirSenha = new String(repetirSenha);
        String combo = comboPerfil.getSelectedItem().toString();
        String nome = txtNomeCadastro.getText().trim();
        String email = txtEmailCadastro.getText().trim().toLowerCase();
        int status = checkAtivo.isSelected() ? 1 : 0;
        int id = Integer.parseInt(tblUsuarios.getValueAt(linhaSelecionada, 0).toString());

        if (!campoSenha.isEmpty() && campoRepetirSenha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "PREENCHA O CAMPO REPETIR SENHA!");
            txtRepetirSenhaCadastro.requestFocus();
            return;
        }

        if (!campoRepetirSenha.isEmpty() && campoSenha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "PREENCHA O CAMPO SENHA!");
            txtSenhaCadastro.requestFocus();
            return;
        }

        if (!campoSenha.equals(campoRepetirSenha)) {
            JOptionPane.showMessageDialog(this, "OS CAMPOS SENHA E REPETIR SENHA NÃO SÃO IGUAIS!");
            txtSenhaCadastro.requestFocus();

        } else {
            bancoUsuarios.atualizarUsuario(id, nome, email, campoSenha, combo, status);
            limparCampos();
            lerTabela();
        }
    }//atualizarUsuario

    private void salvarUsuario() {
        char[] senha = txtSenhaCadastro.getPassword();
        String campoSenha = new String(senha);
        char[] repetirSenha = txtRepetirSenhaCadastro.getPassword();
        String campoRepetirSenha = new String(repetirSenha);
        String combo = comboPerfil.getSelectedItem().toString();
        String nome = txtNomeCadastro.getText().trim();
        String email = txtEmailCadastro.getText().trim();
        int status = checkAtivo.isSelected() ? 1 : 0;

        if (txtNomeCadastro.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "PREENCHA O CAMPO NOME!");
            txtNomeCadastro.requestFocus();
            return;
        }

        if (txtEmailCadastro.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "PREENCHA O CAMPO E-MAIL!");
            txtEmailCadastro.requestFocus();
            return;
        }

        if (campoSenha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "PREENCHA O CAMPO SENHA!");
            txtSenhaCadastro.requestFocus();
            return;
        }

        if (campoRepetirSenha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "PREENCHA O CAMPO REPETIR SENHA!");
            txtRepetirSenhaCadastro.requestFocus();
            return;
        }

        if (!campoSenha.equals(campoRepetirSenha)) {
            JOptionPane.showMessageDialog(this, "OS CAMPOS SENHA E REPETIR SENHA NÃO SÃO IGUAIS!");
            txtSenhaCadastro.requestFocus();
            return;
        }

        if (combo.equalsIgnoreCase("Selecione")) {
            JOptionPane.showMessageDialog(this, "SELECIONE O PERFIL DO USUÁRIO!");
            comboPerfil.requestFocus();
        } else {
            bancoUsuarios.inserirUsuario(nome, email, campoSenha, combo, status);
            lerTabela();
        }
    }//salvarUsuario

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
        btnDesativarUsuario.setEnabled(true);
    }//limparCampos

    private void lerTabela() {
        try {
            ResultSet rs = bancoUsuarios.listarUsuarios();
            tblUsuarios.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "ERRO AO LER TABELA: " + e.getMessage());
        }
    }//lerTabela

    private void setarTabela() {
        int setar = tblUsuarios.getSelectedRow();

        if (setar >= 0) {
            txtNomeCadastro.setText(tblUsuarios.getModel().getValueAt(setar, 1).toString());
            txtEmailCadastro.setText(tblUsuarios.getModel().getValueAt(setar, 2).toString());
            comboPerfil.setSelectedItem(tblUsuarios.getModel().getValueAt(setar, 3).toString());
            Object status = tblUsuarios.getValueAt(setar, 4);
            boolean ativo = status.toString().equals("1");
            checkAtivo.setSelected(ativo);
        }//if
        btnSalvarUsuario.setEnabled(false);

        if (txtEmailCadastro.getText().equals("mwllsantos@gmail.com")) {
            btnDesativarUsuario.setEnabled(false);
        } else {
            btnDesativarUsuario.setEnabled(true);
        }
    }//setarTabela

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
        btnDesativarUsuario = new javax.swing.JButton();
        btnLimparCamposCadastroUsuarios = new javax.swing.JButton();
        btnAtualizarUsuario = new javax.swing.JButton();
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

        btnDesativarUsuario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-desconectar-usuario-27.png"))); // NOI18N
        btnDesativarUsuario.setText("DESATIVAR");
        btnDesativarUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDesativarUsuarioActionPerformed(evt);
            }
        });
        panelPrincipal.add(btnDesativarUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 520, 170, 40));

        btnLimparCamposCadastroUsuarios.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-limpar-aqruivo-21.png"))); // NOI18N
        btnLimparCamposCadastroUsuarios.setText("LIMPAR");
        btnLimparCamposCadastroUsuarios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimparCamposCadastroUsuariosActionPerformed(evt);
            }
        });
        panelPrincipal.add(btnLimparCamposCadastroUsuarios, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 520, 170, 40));

        btnAtualizarUsuario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-atualizar-21.png"))); // NOI18N
        btnAtualizarUsuario.setText("ATUALIZAR");
        btnAtualizarUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAtualizarUsuarioActionPerformed(evt);
            }
        });
        panelPrincipal.add(btnAtualizarUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 520, 170, 40));

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
        panelPrincipal.add(btnSalvarUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 520, 170, 40));

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel7.setText("Perfil");
        panelPrincipal.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 410, -1, -1));

        getContentPane().add(panelPrincipal);

        setSize(new java.awt.Dimension(1218, 647));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnDesativarUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDesativarUsuarioActionPerformed
        String email = txtEmailCadastro.getText().trim();
        bancoUsuarios.desativarUsuario(email);
        limparCampos();
        lerTabela();
    }//GEN-LAST:event_btnDesativarUsuarioActionPerformed

    private void btnLimparCamposCadastroUsuariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimparCamposCadastroUsuariosActionPerformed
        limparCampos();
    }//GEN-LAST:event_btnLimparCamposCadastroUsuariosActionPerformed

    private void btnAtualizarUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAtualizarUsuarioActionPerformed
        atualizarUsuario();
    }//GEN-LAST:event_btnAtualizarUsuarioActionPerformed

    private void btnSalvarUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarUsuarioActionPerformed
        salvarUsuario();
    }//GEN-LAST:event_btnSalvarUsuarioActionPerformed

    private void tblUsuariosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblUsuariosMouseClicked
        setarTabela();
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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAtualizarUsuario;
    private javax.swing.JButton btnDesativarUsuario;
    private javax.swing.JButton btnLimparCamposCadastroUsuarios;
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

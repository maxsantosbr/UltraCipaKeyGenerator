package banco;

import java.sql.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class BancoUsuarios {

    private static final String URL = "jdbc:sqlite:usuarios.db";
    private Connection conn;

//    CadastroUsuarioUI cuu = new CadastroUsuarioUI();
    // 🔥 INICIALIZAÇÃO AUTOMÁTICA AO CRIAR OBJETO
    public BancoUsuarios() {

        try {
            Class.forName("org.sqlite.JDBC");

            // garante execução no diretório correto (Java 8)
            Files.createDirectories(Paths.get("."));

            conn = DriverManager.getConnection(URL);

            criarTabelaUsuario();
            inserirUsuarioAdmin();

            System.out.println("BANCO INICIALIZADO COM SUCESSO");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERRO AO INICIALIZAR BANCO: " + e.getMessage());
        }

    }

    // =========================
    // CRIAR TABELA
    // =========================
    private void criarTabelaUsuario() throws Exception {

        String sql
                = "CREATE TABLE IF NOT EXISTS tbl_usuarios ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "nome TEXT NOT NULL,"
                + "email TEXT NOT NULL UNIQUE,"
                + "senha TEXT NOT NULL,"
                + "tipo TEXT NOT NULL,"
                + "ativa INTEGER DEFAULT 1"
                + ")";

        conn.createStatement().execute(sql);
    }

    // =========================
    // INSERIR ADMIN (SÓ 1 VEZ)
    // =========================
    private void inserirUsuarioAdmin() {

        String sql
                = "INSERT INTO tbl_usuarios (nome, email, senha, tipo, ativa) "
                + "SELECT ?, ?, ?, ?, ? "
                + "WHERE NOT EXISTS ("
                + "SELECT 1 FROM tbl_usuarios WHERE email = ?"
                + ");";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "Maxwell");
            ps.setString(2, "mwllsantos@gmail.com");
            ps.setString(3, "369");
            ps.setString(4, "Admin");
            ps.setInt(5, 1);
            ps.setString(6, "mwllsantos@gmail.com");

            ps.executeUpdate();

            System.out.println("ADMIN VERIFICADO/INSERIDO");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "ERRO AO INSERIR ADMIN: " + e.getMessage());
        }
    }

    // =========================
    // INSERIR USUÁRIO
    // =========================
    public void inserirUsuario(String nome, String email, String senha, String tipo, int ativa) {

        String sql = "INSERT INTO tbl_usuarios (nome, email, senha, tipo, ativa) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nome);
            ps.setString(2, email);
            ps.setString(3, senha);
            ps.setString(4, tipo);
            ps.setInt(5, ativa);

            ps.executeUpdate();

            System.out.println("USUÁRIO INSERIDO COM SUCESSO.");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "ERRO AO INSERIR USUÁRIO: " + e.getMessage());
        }
    }

    // =========================
    // DESATIVAR USUÁRIO
    // =========================
    public void desativarUsuario(String email) {
        String sql = "UPDATE tbl_usuarios SET ativa = 0 WHERE email = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            int linhas = ps.executeUpdate();

            if (linhas > 0) {
                JOptionPane.showMessageDialog(null,
                        "USUÁRIO DESATIVADO COM SUCESSO.");
            } else {
                JOptionPane.showMessageDialog(null, "USUÁRIO NÃO ENCONTRADO.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "ERRO AO TENTAR DESATIVAR USUÁRIO: " + e.getMessage());
        }
    }

    // =========================
    // ATUALIZAR USUÁRIO
    // =========================
//    public void atualizarUsuario(int id, String nome, String email, String senha, String tipo, int ativa) {
//
//        String sql = "UPDATE tbl_usuarios SET nome = ?, email = ?, senha = ?, tipo = ?, ativa = ? WHERE id = ?";
//
//        try (PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setString(1, nome);
//            ps.setString(2, email);
//            ps.setString(3, senha);
//            ps.setString(4, tipo);
//            ps.setInt(5, ativa);
//            ps.setInt(6, id);
//
//            int linhas = ps.executeUpdate();
//
//            if (linhas > 0) {
//                System.out.println("USUÁRIO ATUALIZADO COM SUCESSO.");
//            } else {
//                System.out.println("USUÁRIO NÃO ENCONTRADO.");
//            }
//
//        } catch (SQLException e) {
//            JOptionPane.showMessageDialog(null,
//                    "ERRO AO ATUALIZAR USUÁRIO: " + e.getMessage());
//        }
//    }//atualizarUsuario
    //NOVO ATUALIZAR USUÁRIO
    public void atualizarUsuario(int id, String nome, String email, String senha, String tipo, Integer ativa) {
        StringBuilder sql = new StringBuilder("UPDATE tbl_usuarios SET ");
        List<Object> parametros = new ArrayList<>();

        if (nome != null && !nome.trim().isEmpty()) {
            sql.append("nome = ?, ");
            parametros.add(nome.trim());
        }
        if (email != null && !email.trim().isEmpty()) {
            sql.append("email = ?, ");
            parametros.add(email.trim());
        }
        if (senha != null && !senha.trim().isEmpty()) {
            sql.append("senha = ?, ");
            parametros.add(senha.trim());
        }
        if (tipo != null && !tipo.trim().isEmpty() && !tipo.equalsIgnoreCase("Selecione")) {
            sql.append("tipo = ?, ");
            parametros.add(tipo.trim());
        }
        if (ativa != null) {
            sql.append("ativa = ?, ");
            parametros.add(ativa);
        }

        if (parametros.isEmpty()) {
            JOptionPane.showMessageDialog(null, "NENHUM CAMPO FOI PREENCHIDO PARA ATUALIZAÇÃO!");
            return;
        }

        // remove a última vírgula e espaço
        sql.setLength(sql.length() - 2);
        sql.append(" WHERE id = ?");
        parametros.add(id);

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < parametros.size(); i++) {
                ps.setObject(i + 1, parametros.get(i));
            }
            int linhas = ps.executeUpdate();
            if (linhas > 0) {
                JOptionPane.showMessageDialog(null, "USUÁRIO ATUALIZADO COM SUCESSO.");
            } else {
                JOptionPane.showMessageDialog(null, "USUÁRIO NÃO ENCONTRADO.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "ERRO AO ATUALIZAR USUÁRIO: " + e.getMessage());
        }
    }//atualizarUsuario

    public boolean buscarUsuarioPorEmail(String email, String senha) {
        String sql = "SELECT email, senha, tipo FROM tbl_usuarios WHERE email = ? AND senha = ? AND ativa = 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, senha);

            try (ResultSet rs = ps.executeQuery()) {

                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("ERRO AO FAZER LOGIN: " + e.getMessage());
        }
        return false;
    }//buscarUsuarioPorEmail

    public boolean acessarTelaRestrita(String email, String senha) {

        String sql = "SELECT * FROM tbl_usuarios WHERE email = ? AND senha = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, senha);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println("ERRO AO FAZER LOGIN: " + e.getMessage());
        }
        return false;
    }//acessarTelaRestrita

    public ResultSet listarUsuarios() throws SQLException {

        String sql = "SELECT id AS 'ID', "
                + "nome AS 'Nome', "
                + "email AS 'E-mail', "
                + "tipo AS 'Tipo de acesso', "
                + "ativa AS 'Status' "
                + "FROM tbl_usuarios";

        PreparedStatement ps = conn.prepareStatement(sql);

        return ps.executeQuery();
    }

    // =========================
    // FECHAR CONEXÃO
    // =========================
    public void fechar() {

        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("ERRO AO FECHAR CONEXÃO: " + e.getMessage());
        }
    }
}

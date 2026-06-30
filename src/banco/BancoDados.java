package banco;

import java.sql.*;
import model.LicencaModel;

public class BancoDados {

    private static final String URL = "jdbc:sqlite:licencas.db";
    private Connection conn;

    public BancoDados() throws Exception {
        Class.forName("org.sqlite.JDBC"); // carrega o driver
        conn = DriverManager.getConnection(URL);
        criarTabela(); // cria se nao existir
    }

    private void criarTabela() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS licencas (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "nome TEXT NOT NULL," +
            "email TEXT NOT NULL," +
            "hardware_id TEXT NOT NULL," +
            "produto TEXT NOT NULL," +
            "validade TEXT NOT NULL," +
            "gerada_em TEXT NOT NULL," +
            "assinatura TEXT NOT NULL," +
            "ativa INTEGER DEFAULT 1)";
        conn.createStatement().execute(sql);
    }

    public void salvarLicenca(LicencaModel lic) throws Exception {
        String sql = "INSERT INTO licencas " +
            "(nome, email, hardware_id, produto, validade, gerada_em, assinatura)" +
            " VALUES (?,?,?,?,?,?,?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, lic.getNome());
        ps.setString(2, lic.getEmail());
        ps.setString(3, lic.getHardwareId());
        ps.setString(4, lic.getProduto());
        ps.setString(5, lic.getValidade());
        ps.setString(6, lic.getGeradaEm());
        ps.setString(7, lic.getAssinatura());
        ps.executeUpdate();
    }

    public ResultSet listarLicencas() throws Exception {
        return conn.createStatement()
            .executeQuery("SELECT * FROM licencas ORDER BY id DESC");
    }

    public ResultSet buscarPorEmail(String email) throws Exception {
        PreparedStatement ps = conn.prepareStatement(
            "SELECT * FROM licencas WHERE email LIKE ?");
        ps.setString(1, "%" + email + "%");
        return ps.executeQuery();
    }

    public void fechar() {
        try { if (conn != null) conn.close(); } catch (SQLException e) {}
    }
}
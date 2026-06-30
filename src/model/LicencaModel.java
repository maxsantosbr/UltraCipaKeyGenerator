/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

public class LicencaModel {

    private String nome, email, hardwareId, produto, validade, geradaEm, assinatura;

    // Construtores, getters e setters
    public LicencaModel() {}

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getHardwareId() { return hardwareId; }
    public void setHardwareId(String hardwareId) { this.hardwareId = hardwareId; }

    public String getProduto() { return produto; }
    public void setProduto(String produto) { this.produto = produto; }

    public String getValidade() { return validade; }
    public void setValidade(String validade) { this.validade = validade; }

    public String getGeradaEm() { return geradaEm; }
    public void setGeradaEm(String geradaEm) { this.geradaEm = geradaEm; }

    public String getAssinatura() { return assinatura; }
    public void setAssinatura(String assinatura) { this.assinatura = assinatura; }

    // Monta o conteudo que sera assinado (sem a assinatura!)
    public String getConteudoParaAssinar() {
        return "NOME=" + nome + "\n" +
               "EMAIL=" + email + "\n" +
               "HARDWARE_ID=" + hardwareId + "\n" +
               "PRODUTO=" + produto + "\n" +
               "VALIDADE=" + validade + "\n" +
               "GERADA_EM=" + geradaEm;
    }

    // Monta o arquivo completo de licenca
    public String getConteudoArquivo() {
        return getConteudoParaAssinar() + "\n" +
               "ASSINATURA=" + assinatura;
    }
}

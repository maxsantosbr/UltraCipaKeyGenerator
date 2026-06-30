package util;

import java.security.*;
import java.security.spec.*;
import java.util.Base64;
import java.nio.file.*;

public class RSAUtil {

    // Gera o par de chaves RSA (faça isso UMA VEZ só!)
    public static void gerarChaves(String pastaDestino) throws Exception {
        KeyPairGenerator kg = KeyPairGenerator.getInstance("RSA");
        kg.initialize(2048); // 2048 bits = seguro
        KeyPair par = kg.generateKeyPair();

        // Salva chave privada
        byte[] privBytes = par.getPrivate().getEncoded();
        String privB64 = Base64.getEncoder().encodeToString(privBytes);
        Files.write(Paths.get(pastaDestino + "/chave_privada.key"),
                    privB64.getBytes());

        // Salva chave publica
        byte[] pubBytes = par.getPublic().getEncoded();
        String pubB64 = Base64.getEncoder().encodeToString(pubBytes);
        Files.write(Paths.get(pastaDestino + "/chave_publica.key"),
                    pubB64.getBytes());

        System.out.println("Chaves geradas com sucesso!");
    }

    // Carrega a chave privada do arquivo
    public static PrivateKey carregarPrivada(String caminho) throws Exception {
        String b64 = new String(Files.readAllBytes(Paths.get(caminho))).trim();
        byte[] bytes = Base64.getDecoder().decode(b64);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bytes);
        return KeyFactory.getInstance("RSA").generatePrivate(spec);
    }

    // Carrega a chave publica do arquivo
    public static PublicKey carregarPublica(String caminho) throws Exception {
        String b64 = new String(Files.readAllBytes(Paths.get(caminho))).trim();
        byte[] bytes = Base64.getDecoder().decode(b64);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
        return KeyFactory.getInstance("RSA").generatePublic(spec);
    }

    // Assina os dados com a chave privada
    public static String assinar(String dados, PrivateKey chavePriv) throws Exception {
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(chavePriv);
        sig.update(dados.getBytes("UTF-8"));
        byte[] assinatura = sig.sign();
        return Base64.getEncoder().encodeToString(assinatura);
    }

    // Verifica a assinatura com a chave publica
    public static boolean verificar(String dados, String assinaturaB64,
                                     PublicKey chavePub) throws Exception {
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initVerify(chavePub);
        sig.update(dados.getBytes("UTF-8"));
        byte[] assinatura = Base64.getDecoder().decode(assinaturaB64);
        return sig.verify(assinatura);
    }
}

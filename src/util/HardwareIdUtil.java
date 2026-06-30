package util;

import java.net.*;
import java.security.*;
import java.util.*;

//USE ESSE CÓDIGO NOS 2 (DOIS) SOFTWARES: O GERADOR (O SEU) E NA APLICAÇÃO (DO CLIENTE).
public class HardwareIdUtil {

    public static String obterHardwareId() {
        try {
            StringBuilder sb = new StringBuilder();

            // 1. Nome do computador
            sb.append(InetAddress.getLocalHost().getHostName());

            // 2. Enderecos MAC de todas as interfaces de rede
            Enumeration<NetworkInterface> redes =
                NetworkInterface.getNetworkInterfaces();
            while (redes != null && redes.hasMoreElements()) {
                NetworkInterface ni = redes.nextElement();
                byte[] mac = ni.getHardwareAddress();
                if (mac != null) {
                    for (byte b : mac) {
                        sb.append(String.format("%02X", b));
                    }
                }
            }

            // 3. Converte tudo em hash SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(sb.toString().getBytes("UTF-8"));

            // 4. Converte o hash para hexadecimal
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02X", b));
            }
            // Retorna apenas os primeiros 32 caracteres
            return hex.toString().substring(0, 32);

        } catch (Exception e) {
            return "ERRO_HW_ID";
        }
    }
}
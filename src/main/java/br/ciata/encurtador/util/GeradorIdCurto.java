package br.ciata.encurtador.util;

import java.security.SecureRandom;

public class GeradorIdCurto {

    private static final String CARACTERES = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int TAMANHO = 6;
    private static final SecureRandom RANDOM = new SecureRandom();

    private GeradorIdCurto() {
    }

    public static String gerar() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < TAMANHO; i++) {
            sb.append(CARACTERES.charAt(RANDOM.nextInt(CARACTERES.length())));
        }

        return sb.toString();
    }
}
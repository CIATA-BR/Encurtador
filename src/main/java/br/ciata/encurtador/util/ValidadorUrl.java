package br.ciata.encurtador.util;

import br.ciata.encurtador.erros.UrlInvalidaException;

import java.net.URI;
import java.net.URISyntaxException;

public class ValidadorUrl {

    private ValidadorUrl() {
    }

    public static void validar(String url) {
        if (url == null || url.isBlank()) {
            throw new UrlInvalidaException("erro.url.invalida");
        }

        try {
            URI uri = new URI(url);
            String esquema = uri.getScheme();

            if (esquema == null || (!esquema.equalsIgnoreCase("http") && !esquema.equalsIgnoreCase("https"))) {
                throw new UrlInvalidaException("erro.url.invalida");
            }

            if (uri.getHost() == null || uri.getHost().isBlank()) {
                throw new UrlInvalidaException("erro.url.invalida");
            }

        } catch (URISyntaxException e) {
            throw new UrlInvalidaException("erro.url.invalida");
        }
    }
}
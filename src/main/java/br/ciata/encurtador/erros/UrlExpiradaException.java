package br.ciata.encurtador.erros;

public class UrlExpiradaException extends RuntimeException {

    public UrlExpiradaException(String mensagem) {
        super(mensagem);
    }
}
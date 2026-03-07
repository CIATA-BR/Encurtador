package br.ciata.encurtador.erros;

public class UrlInvalidaException extends RuntimeException {

    public UrlInvalidaException(String mensagem) {
        super(mensagem);
    }
}
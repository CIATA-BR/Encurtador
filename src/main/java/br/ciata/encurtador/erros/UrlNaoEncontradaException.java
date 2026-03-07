package br.ciata.encurtador.erros;

public class UrlNaoEncontradaException extends RuntimeException {

    public UrlNaoEncontradaException(String mensagem) {
        super(mensagem);
    }
}
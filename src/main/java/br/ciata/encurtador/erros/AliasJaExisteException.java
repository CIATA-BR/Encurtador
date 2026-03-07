package br.ciata.encurtador.erros;

public class AliasJaExisteException extends RuntimeException {

    public AliasJaExisteException(String mensagem) {
        super(mensagem);
    }
}
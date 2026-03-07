package br.ciata.encurtador.dto;

import java.time.Instant;

public class ErroResponse {

    private String codigo;
    private String mensagem;
    private Instant timestamp;
    private String caminho;

    public ErroResponse(String codigo, String mensagem, String caminho) {
        this.codigo = codigo;
        this.mensagem = mensagem;
        this.timestamp = Instant.now();
        this.caminho = caminho;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getCaminho() {
        return caminho;
    }
}
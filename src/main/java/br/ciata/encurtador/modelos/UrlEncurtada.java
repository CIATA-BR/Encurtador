package br.ciata.encurtador.modelos;

import java.time.Instant;

public class UrlEncurtada {

    private String id;
    private String urlOriginal;
    private Instant criadoEm;
    private Instant expiraEm;
    private long contagemCliques;

    public UrlEncurtada(String id, String urlOriginal, Instant criadoEm, Instant expiraEm) {
        this.id = id;
        this.urlOriginal = urlOriginal;
        this.criadoEm = criadoEm;
        this.expiraEm = expiraEm;
        this.contagemCliques = 0;
    }

    public String getId() {
        return id;
    }

    public String getUrlOriginal() {
        return urlOriginal;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public Instant getExpiraEm() {
        return expiraEm;
    }

    public long getContagemCliques() {
        return contagemCliques;
    }

    public void incrementarCliques() {
        this.contagemCliques++;
    }

    public boolean estaExpirada() {
        return expiraEm != null && Instant.now().isAfter(expiraEm);
    }
}
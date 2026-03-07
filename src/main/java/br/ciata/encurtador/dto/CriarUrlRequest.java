package br.ciata.encurtador.dto;

import jakarta.validation.constraints.NotBlank;

public class CriarUrlRequest {

    @NotBlank(message = "A URL original é obrigatória")
    private String originalUrl;

    private String expirationDate;
    private String aliasCustomizado;

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getAliasCustomizado() {
        return aliasCustomizado;
    }

    public void setAliasCustomizado(String aliasCustomizado) {
        this.aliasCustomizado = aliasCustomizado;
    }
}
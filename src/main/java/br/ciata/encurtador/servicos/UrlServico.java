package br.ciata.encurtador.servicos;

import br.ciata.encurtador.config.ConfiguracaoAplicacao;
import br.ciata.encurtador.dto.CriarUrlRequest;
import br.ciata.encurtador.dto.UrlResponse;
import br.ciata.encurtador.erros.AliasJaExisteException;
import br.ciata.encurtador.erros.UrlExpiradaException;
import br.ciata.encurtador.erros.UrlNaoEncontradaException;
import br.ciata.encurtador.modelos.UrlEncurtada;
import br.ciata.encurtador.repositorios.UrlRepositorio;
import br.ciata.encurtador.util.GeradorIdCurto;
import br.ciata.encurtador.util.ValidadorUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.format.DateTimeParseException;

@Service
public class UrlServico {

    private static final Logger logger = LoggerFactory.getLogger(UrlServico.class);

    private final UrlRepositorio repositorio;
    private final ConfiguracaoAplicacao config;

    public UrlServico(UrlRepositorio repositorio, ConfiguracaoAplicacao config) {
        this.repositorio = repositorio;
        this.config = config;
    }

    public UrlResponse criar(CriarUrlRequest request) {
        ValidadorUrl.validar(request.getOriginalUrl());

        String id = definirId(request.getAliasCustomizado());
        Instant criadoEm = Instant.now();
        Instant expiraEm = converterDataExpiracao(request.getExpirationDate());

        UrlEncurtada url = new UrlEncurtada(
                id,
                request.getOriginalUrl(),
                criadoEm,
                expiraEm
        );

        repositorio.salvar(url);
        logger.info("URL encurtada criada com id {}", id);

        return converterParaResponse(url);
    }

    public String resolverUrlOriginal(String id) {
        UrlEncurtada url = repositorio.buscarPorId(id)
                .orElseThrow(() -> {
                    logger.warn("Tentativa de acesso a id inexistente: {}", id);
                    return new UrlNaoEncontradaException("erro.url.nao_encontrada");
                });

        if (url.estaExpirada()) {
            logger.warn("Tentativa de acesso a URL expirada: {}", id);
            throw new UrlExpiradaException("erro.url.expirada");
        }

        url.incrementarCliques();
        repositorio.salvar(url);
        logger.info("Redirecionamento realizado para id {} com total de {} cliques", id, url.getContagemCliques());

        return url.getUrlOriginal();
    }

    public UrlResponse detalhar(String id) {
        UrlEncurtada url = repositorio.buscarPorId(id)
                .orElseThrow(() -> new UrlNaoEncontradaException("erro.url.nao_encontrada"));

        return converterParaResponse(url);
    }

    private String definirId(String aliasCustomizado) {
        if (aliasCustomizado != null && !aliasCustomizado.isBlank()) {
            validarAlias(aliasCustomizado);

            if (repositorio.existePorId(aliasCustomizado)) {
                throw new AliasJaExisteException("erro.alias.existente");
            }

            return aliasCustomizado;
        }

        String novoId;
        do {
            novoId = GeradorIdCurto.gerar();
        } while (repositorio.existePorId(novoId));

        return novoId;
    }

    private void validarAlias(String aliasCustomizado) {
        if (!aliasCustomizado.matches("^[a-zA-Z0-9_-]+$")) {
            throw new IllegalArgumentException("erro.alias.invalido");
        }
    }

    private Instant converterDataExpiracao(String expirationDate) {
        if (expirationDate == null || expirationDate.isBlank()) {
            return null;
        }

        try {
            Instant data = Instant.parse(expirationDate);

            if (data.isBefore(Instant.now())) {
                throw new IllegalArgumentException("erro.data.expiracao.passado");
            }

            return data;
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("erro.data.expiracao.invalida");
        }
    }

    private UrlResponse converterParaResponse(UrlEncurtada url) {
        return new UrlResponse(
                url.getId(),
                config.getUrlBase() + "/" + url.getId(),
                url.getUrlOriginal(),
                url.getCriadoEm(),
                url.getExpiraEm(),
                url.getContagemCliques()
        );
    }
}
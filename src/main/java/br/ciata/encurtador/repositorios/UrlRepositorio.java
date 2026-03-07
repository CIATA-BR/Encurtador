package br.ciata.encurtador.repositorios;

import br.ciata.encurtador.modelos.UrlEncurtada;

import java.util.Optional;

public interface UrlRepositorio {

    UrlEncurtada salvar(UrlEncurtada url);

    Optional<UrlEncurtada> buscarPorId(String id);

    boolean existePorId(String id);
}
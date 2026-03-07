package br.ciata.encurtador.repositorios;

import br.ciata.encurtador.modelos.UrlEncurtada;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class UrlRepositorioMemoria implements UrlRepositorio {

    private final ConcurrentHashMap<String, UrlEncurtada> banco = new ConcurrentHashMap<>();

    @Override
    public UrlEncurtada salvar(UrlEncurtada url) {
        banco.put(url.getId(), url);
        return url;
    }

    @Override
    public Optional<UrlEncurtada> buscarPorId(String id) {
        return Optional.ofNullable(banco.get(id));
    }

    @Override
    public boolean existePorId(String id) {
        return banco.containsKey(id);
    }
}
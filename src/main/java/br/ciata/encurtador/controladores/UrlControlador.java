package br.ciata.encurtador.controladores;

import br.ciata.encurtador.dto.CriarUrlRequest;
import br.ciata.encurtador.dto.UrlResponse;
import br.ciata.encurtador.servicos.UrlServico;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@Tag(name = "URLs", description = "Operações de encurtamento e redirecionamento de URLs")
public class UrlControlador {

    private final UrlServico servico;

    public UrlControlador(UrlServico servico) {
        this.servico = servico;
    }

    @PostMapping("/v1/urls")
    @Operation(summary = "Criar URL encurtada")
    public ResponseEntity<UrlResponse> criar(@Valid @RequestBody CriarUrlRequest request) {
        UrlResponse response = servico.criar(request);
        return ResponseEntity.created(URI.create(response.getShortUrl())).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Redirecionar para a URL original")
    public ResponseEntity<Void> redirecionar(@PathVariable String id) {
        String destino = servico.resolverUrlOriginal(id);

        return ResponseEntity.status(302)
                .location(URI.create(destino))
                .build();
    }

    @GetMapping("/v1/urls/{id}")
    @Operation(summary = "Consultar detalhes da URL encurtada")
    public ResponseEntity<UrlResponse> detalhar(@PathVariable String id) {
        return ResponseEntity.ok(servico.detalhar(id));
    }
}
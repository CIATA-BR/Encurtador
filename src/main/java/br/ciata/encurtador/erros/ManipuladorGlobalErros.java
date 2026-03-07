package br.ciata.encurtador.erros;

import br.ciata.encurtador.dto.ErroResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;

@RestControllerAdvice
public class ManipuladorGlobalErros {

    private static final Logger logger = LoggerFactory.getLogger(ManipuladorGlobalErros.class);

    private final MessageSource messageSource;

    public ManipuladorGlobalErros(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(UrlNaoEncontradaException.class)
    public ResponseEntity<ErroResponse> tratarNaoEncontrada(
            UrlNaoEncontradaException ex,
            HttpServletRequest request) {

        logger.warn("URL não encontrada: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErroResponse(
                        "URL_NAO_ENCONTRADA",
                        traduzir(ex.getMessage(), request),
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(UrlExpiradaException.class)
    public ResponseEntity<ErroResponse> tratarExpirada(
            UrlExpiradaException ex,
            HttpServletRequest request) {

        logger.warn("URL expirada: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.GONE)
                .body(new ErroResponse(
                        "URL_EXPIRADA",
                        traduzir(ex.getMessage(), request),
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(UrlInvalidaException.class)
    public ResponseEntity<ErroResponse> tratarInvalida(
            UrlInvalidaException ex,
            HttpServletRequest request) {

        logger.warn("URL inválida: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErroResponse(
                        "URL_INVALIDA",
                        traduzir(ex.getMessage(), request),
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(AliasJaExisteException.class)
    public ResponseEntity<ErroResponse> tratarAliasDuplicado(
            AliasJaExisteException ex,
            HttpServletRequest request) {

        logger.warn("Alias duplicado: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErroResponse(
                        "ALIAS_JA_EXISTE",
                        traduzir(ex.getMessage(), request),
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> tratarValidacao(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String mensagem = traduzir("erro.validacao", request);

        FieldError erro = ex.getBindingResult().getFieldError();
        if (erro != null && erro.getDefaultMessage() != null && !erro.getDefaultMessage().isBlank()) {
            mensagem = erro.getDefaultMessage();
        }

        logger.warn("Erro de validação: {}", mensagem);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErroResponse(
                        "VALIDACAO",
                        mensagem,
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErroResponse> tratarIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        logger.warn("Argumento inválido: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErroResponse(
                        "DADO_INVALIDO",
                        traduzir(ex.getMessage(), request),
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> tratarGenerico(
            Exception ex,
            HttpServletRequest request) {

        logger.error("Erro interno não tratado", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErroResponse(
                        "ERRO_INTERNO",
                        traduzir("erro.interno", request),
                        request.getRequestURI()
                ));
    }

    private String traduzir(String chave, HttpServletRequest request) {
        Locale locale = request.getLocale();

        try {
            String texto = messageSource.getMessage(chave, null, locale);

            if (texto == null || texto.isBlank()) {
                return fallbackMensagem(chave);
            }

            if (texto.equals(chave)) {
                return fallbackMensagem(chave);
            }

            return texto;
        } catch (Exception e) {
            return fallbackMensagem(chave);
        }
    }

    private String fallbackMensagem(String chave) {
        return switch (chave) {
            case "erro.url.nao_encontrada" -> "URL não encontrada";
            case "erro.url.expirada" -> "A URL encurtada expirou";
            case "erro.url.invalida" -> "A URL informada é inválida";
            case "erro.alias.existente" -> "O alias informado já está em uso";
            case "erro.validacao" -> "Dados inválidos";
            case "erro.data.expiracao.invalida" ->
                    "A data de expiração deve estar no formato ISO-8601, por exemplo: 2026-12-31T23:59:59Z";
            case "erro.data.expiracao.passado" -> "A data de expiração deve estar no futuro";
            case "erro.alias.invalido" ->
                    "O alias customizado deve conter apenas letras, números, hífen e underscore";
            case "erro.interno" -> "Ocorreu um erro interno no servidor";
            default -> chave;
        };
    }
}
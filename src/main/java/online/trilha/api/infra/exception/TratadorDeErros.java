package online.trilha.api.infra.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class TratadorDeErros {

    private static final Logger log = LoggerFactory.getLogger(TratadorDeErros.class);

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroDto> naoEncontrado(RecursoNaoEncontradoException e, HttpServletRequest req) {
        return resposta(HttpStatus.NOT_FOUND, e.getMessage(), req);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErroDto> rotaInexistente(NoResourceFoundException e, HttpServletRequest req) {
        return resposta(HttpStatus.NOT_FOUND, "Rota não encontrada.", req);
    }

    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<ErroDto> regraDeNegocio(RegraDeNegocioException e, HttpServletRequest req) {
        return resposta(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage(), req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroDto> validacao(MethodArgumentNotValidException e, HttpServletRequest req) {
        var campos = e.getFieldErrors().stream().map(ErroDto.CampoInvalidoDto::new).toList();
        return ResponseEntity.badRequest()
                .body(ErroDto.deValidacao("Há campos inválidos.", req, campos));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroDto> corpoIlegivel(HttpMessageNotReadableException e, HttpServletRequest req) {
        return resposta(HttpStatus.BAD_REQUEST, "O corpo da requisição está mal formado.", req);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErroDto> metodoNaoPermitido(HttpRequestMethodNotSupportedException e, HttpServletRequest req) {
        return resposta(HttpStatus.METHOD_NOT_ALLOWED, "Método " + e.getMethod() + " não aceito nesta rota.", req);
    }

    @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
    public ResponseEntity<ErroDto> semPermissao(RuntimeException e, HttpServletRequest req) {
        return resposta(HttpStatus.FORBIDDEN, "Você não tem permissão para isso.", req);
    }

    /**
     * Qualquer erro não previsto. O detalhe vai para o log do servidor, nunca para
     * a resposta: mensagem de exceção pode carregar SQL, nome de tabela ou dado de cliente.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroDto> inesperado(Exception e, HttpServletRequest req) {
        log.error("Erro inesperado em {} {}", req.getMethod(), req.getRequestURI(), e);
        return resposta(HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno. O registro do problema ficou no servidor.", req);
    }

    private ResponseEntity<ErroDto> resposta(HttpStatus status, String mensagem, HttpServletRequest req) {
        return ResponseEntity.status(status).body(ErroDto.de(status, mensagem, req));
    }
}

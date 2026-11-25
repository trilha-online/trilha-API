package online.trilha.api.infra.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;

import java.time.Instant;
import java.util.List;

/** Formato único de erro da API: o front sempre sabe onde ler a mensagem. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErroDto(
        String momento,
        int status,
        String erro,
        String mensagem,
        String caminho,
        List<CampoInvalidoDto> campos
) {

    public record CampoInvalidoDto(String campo, String mensagem) {
        public CampoInvalidoDto(FieldError fieldError) {
            this(fieldError.getField(), fieldError.getDefaultMessage());
        }
    }

    public static ErroDto de(HttpStatus status, String mensagem, HttpServletRequest request) {
        return new ErroDto(Instant.now().toString(), status.value(), status.getReasonPhrase(),
                mensagem, request.getRequestURI(), null);
    }

    public static ErroDto deValidacao(String mensagem, HttpServletRequest request, List<CampoInvalidoDto> campos) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return new ErroDto(Instant.now().toString(), status.value(), status.getReasonPhrase(),
                mensagem, request.getRequestURI(), campos);
    }
}

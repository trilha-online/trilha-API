package online.trilha.api.infra.exception;

/** O registro não existe. Diferente de "existe mas você não pode ver" (403). */
public class RecursoNaoEncontradoException extends RuntimeException {
    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}

package online.trilha.api.infra.exception;

/** O pedido é válido no formato, mas a regra da Trilha não permite. */
public class RegraDeNegocioException extends RuntimeException {
    public RegraDeNegocioException(String mensagem) {
        super(mensagem);
    }
}

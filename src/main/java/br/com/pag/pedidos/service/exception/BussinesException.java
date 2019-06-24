package br.com.pag.pedidos.service.exception;

/**
 * Exception usada sempre que acontecer algum erro de negocio na aplicação.
 * 
 * @author Saulo Machado
 * @see RuntimeException
 *
 */
public class BussinesException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public BussinesException(String msg, Exception ex) {
		super(msg, ex);
	}

	public BussinesException(String msg) {
		super(msg);
	}

}

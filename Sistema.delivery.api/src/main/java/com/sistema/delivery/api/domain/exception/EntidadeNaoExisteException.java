package com.sistema.delivery.api.domain.exception;

public class EntidadeNaoExisteException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public EntidadeNaoExisteException(String mensagem)
	{
		super(mensagem);
	}
}

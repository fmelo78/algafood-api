package com.algawoks.algafood.domain.exception;

public class EstadoNaoEncontradoException extends EntidadeNaoEncontradaException{

	private static final long serialVersionUID = 1L;
	
	public EstadoNaoEncontradoException (String mensagem) {
		super(mensagem);
	}

	public EstadoNaoEncontradoException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public EstadoNaoEncontradoException (Long estadoId) {
		this(String.format("Estado de código %d não encontrado", estadoId));
	}
	
}

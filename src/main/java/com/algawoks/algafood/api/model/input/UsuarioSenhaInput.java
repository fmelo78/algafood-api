package com.algawoks.algafood.api.model.input;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class UsuarioSenhaInput {

	@NotBlank
	private String senhaAtual;
	
	@NotBlank
	private String novaSenha;
}

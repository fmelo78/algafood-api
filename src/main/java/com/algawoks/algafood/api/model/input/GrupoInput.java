package com.algawoks.algafood.api.model.input;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class GrupoInput {
	
	@NotBlank
	private String nome;

}
package com.algawoks.algafood.api.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.algawoks.algafood.api.converter.PermissaoConverter;
import com.algawoks.algafood.api.model.output.PermissaoOutput;
import com.algawoks.algafood.domain.model.Grupo;
import com.algawoks.algafood.domain.model.Permissao;
import com.algawoks.algafood.domain.service.GrupoService;

@RestController
@RequestMapping ("/grupos/{grupoId}/permissoes")
public class GrupoPermissaoController {
	
	@Autowired
	private GrupoService grupoService;
	
	@Autowired
	private PermissaoConverter permissaoConverter;
	
	@GetMapping
	public ResponseEntity<?> listar (@PathVariable Long grupoId){
		Grupo grupo = grupoService.buscarOuFalhar(grupoId);
		Set<Permissao> permissoes = grupo.getPermissoes();
		List<Permissao> listaPermissoes = new ArrayList<>(permissoes);
		List<PermissaoOutput> permissoesOut = permissaoConverter.toPermissaoListOutput(listaPermissoes);
		return ResponseEntity.status(HttpStatus.OK).body(permissoesOut);
	}
	
	@DeleteMapping ("/{permissaoId}")
	public ResponseEntity<?> desassociarPermissao (@PathVariable Long grupoId, @PathVariable Long permissaoId){
		grupoService.desassociarPermissao(grupoId, permissaoId);		
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
	
	@PutMapping ("/{permissaoId}")
	public ResponseEntity<?> associarPermissao (@PathVariable Long grupoId, @PathVariable Long permissaoId){
		grupoService.associarPermissao(grupoId, permissaoId);		
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}


}

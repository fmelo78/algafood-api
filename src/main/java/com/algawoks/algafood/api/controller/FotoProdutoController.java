package com.algawoks.algafood.api.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.algawoks.algafood.api.converter.FotoProdutoConverter;
import com.algawoks.algafood.api.model.input.FotoProdutoInput;
import com.algawoks.algafood.api.model.output.FotoProdutoOutput;
import com.algawoks.algafood.domain.exception.EntidadeNaoEncontradaException;
import com.algawoks.algafood.domain.model.FotoProduto;
import com.algawoks.algafood.domain.model.Produto;
import com.algawoks.algafood.domain.service.FotoProdutoService;
import com.algawoks.algafood.domain.service.FotoStorageService.FotoRetorno;
import com.algawoks.algafood.domain.service.RestauranteService;

@RestController
@RequestMapping ("/restaurantes/{restauranteId}/produtos/{produtoId}/foto")
public class FotoProdutoController {
	
	@Autowired
	private RestauranteService restauranteService;
	
	@Autowired
	private FotoProdutoConverter fotoProdutoConverter;
	
	@Autowired
	private FotoProdutoService fotoProdutoService;
	
	
	@PutMapping 
	public ResponseEntity<?> atualizarFoto(@PathVariable Long restauranteId, @PathVariable Long produtoId
			, @Valid FotoProdutoInput fotoProdutoInput){
		
		Produto produto = restauranteService.buscarProdutoRestaurante(restauranteId, produtoId);
		FotoProduto metadados = fotoProdutoConverter.toFotoProduto(fotoProdutoInput, produto);
		fotoProdutoService.salvarFoto(metadados, fotoProdutoInput);
		FotoProdutoOutput fotoProdutoOut = fotoProdutoConverter.toFotoProdutoOutput(metadados);
		
		return ResponseEntity.status(HttpStatus.OK).body(fotoProdutoOut);
	}
	
	@GetMapping (produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> buscarMetadados (@PathVariable Long restauranteId, @PathVariable Long produtoId){
		restauranteService.buscarProdutoRestaurante(restauranteId, produtoId);
		FotoProduto metadados = fotoProdutoService.buscarMetadados(produtoId);
		FotoProdutoOutput fotoProdutoOut = fotoProdutoConverter.toFotoProdutoOutput(metadados);
		return ResponseEntity.status(HttpStatus.OK).body(fotoProdutoOut);
	}
	
	@GetMapping 
	public ResponseEntity<?> buscarFoto (@PathVariable Long restauranteId, @PathVariable Long produtoId,
			@RequestHeader(name = "accept") String acceptHeader) throws HttpMediaTypeNotAcceptableException{
		try {
			restauranteService.buscarProdutoRestaurante(restauranteId, produtoId);
			FotoProduto metadados = fotoProdutoService.buscarMetadados(produtoId);
			MediaType mediaType = MediaType.parseMediaType(metadados.getContentType());
			FotoRetorno foto = fotoProdutoService.buscarFoto(metadados, acceptHeader);
			if (foto.getUrl() != null) {
				return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, foto.getUrl()).build();
			}
			else {
				return ResponseEntity.status(HttpStatus.OK).contentType(mediaType).body(new InputStreamResource(foto.getInputStream()));
			}
		}
		catch (EntidadeNaoEncontradaException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();			
		}
	}
	
	@DeleteMapping
	public ResponseEntity<?> removerFoto (@PathVariable Long restauranteId, @PathVariable Long produtoId){
		restauranteService.buscarProdutoRestaurante(restauranteId, produtoId);
		FotoProduto metadados = fotoProdutoService.buscarMetadados(produtoId);
		fotoProdutoService.removerFoto(metadados);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
	
}
















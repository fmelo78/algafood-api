package com.algawoks.algafood.api.controller;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.algawoks.algafood.api.converter.RestauranteConverter;
import com.algawoks.algafood.api.model.input.RestauranteInput;
import com.algawoks.algafood.api.model.output.RestauranteOutput;
import com.algawoks.algafood.api.model.view.RestauranteView;
import com.algawoks.algafood.domain.exception.CidadeNaoEncontradaException;
import com.algawoks.algafood.domain.exception.CozinhaNaoEncontradaException;
import com.algawoks.algafood.domain.exception.NegocioException;
import com.algawoks.algafood.domain.model.Restaurante;
import com.algawoks.algafood.domain.repository.RestauranteRepository;
import com.algawoks.algafood.domain.service.RestauranteService;
import com.algawoks.algafood.infrastructure.spec.RestauranteSpecs;
import com.fasterxml.jackson.annotation.JsonView;

@Controller
@RequestMapping("/restaurantes")
public class RestauranteController {
	
	@Autowired
	private RestauranteRepository restauranteRepository;
	
	@Autowired
	private RestauranteService restauranteService;
	
	@Autowired
	private RestauranteConverter restauranteConverter;
	
	@JsonView (RestauranteView.Resumo.class)
	@GetMapping
	public ResponseEntity<?> listar(){
		List<Restaurante> restaurantes = restauranteRepository.findAll();
		List<RestauranteOutput> restaurantesOut = restauranteConverter.toRestauranteOutList(restaurantes);
		return ResponseEntity.status(HttpStatus.OK).body(restaurantesOut);
	}
	
	@JsonView (RestauranteView.ApenasNome.class)
	@GetMapping (params = "projecao=apenas-nome")
	public ResponseEntity<?> listarApenasNomes(){
		return listar();
	}
	
	@GetMapping("/{restauranteId}")
	public ResponseEntity<?> buscar(@PathVariable Long restauranteId){
		Restaurante restauranteTemp1 = restauranteService.buscarOuFalhar(restauranteId);
		RestauranteOutput restauranteOut = restauranteConverter.toRestauranteOut(restauranteTemp1);
		return ResponseEntity.status(HttpStatus.OK).body(restauranteOut);
	}
	
	@PostMapping
	public ResponseEntity<?> adicionar(@RequestBody @Valid RestauranteInput restauranteIn){
		try {
			Restaurante restaurante = restauranteConverter.toRestaurante(restauranteIn);
			Restaurante restauranteT1 = restauranteService.salvar(restaurante);
			RestauranteOutput restauranteOut = restauranteConverter.toRestauranteOut(restauranteT1);
			return ResponseEntity.status(HttpStatus.CREATED).body(restauranteOut);
		} catch (CozinhaNaoEncontradaException | CidadeNaoEncontradaException ex) {
			throw new NegocioException(ex.getMessage(), ex);
		}
	}
	
	@PutMapping("/{restauranteId}")
	public ResponseEntity<?> alterar(@PathVariable Long restauranteId, @RequestBody @Valid RestauranteInput restauranteIn) {
		Restaurante restauranteT1 = restauranteService.buscarOuFalhar(restauranteId);
		restauranteConverter.copyToRestaurante(restauranteIn, restauranteT1);
		try {
			Restaurante restauranteT2 = restauranteService.salvar(restauranteT1);
			RestauranteOutput restauranteOut = restauranteConverter.toRestauranteOut(restauranteT2);
			return ResponseEntity.status(HttpStatus.OK).body(restauranteOut);
		}
		catch (CozinhaNaoEncontradaException | CidadeNaoEncontradaException ex) {
			throw new NegocioException(ex.getMessage(), ex);
		}
	}
	
	@DeleteMapping ("/{restauranteId}")
	public ResponseEntity<?> remover(@PathVariable Long restauranteId){
		restauranteService.remover(restauranteId);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	@PutMapping ("/{restauranteId}/ativo")
	private ResponseEntity<?> ativar (@PathVariable Long restauranteId){
		restauranteService.ativar(restauranteId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
	
	@DeleteMapping ("/{restauranteId}/ativo")
	private ResponseEntity<?> inativar (@PathVariable Long restauranteId){
		restauranteService.inativar(restauranteId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
	
	@PutMapping("/{restauranteId}/abertura")
	private ResponseEntity<?> abrir (@PathVariable Long restauranteId){
		Restaurante restaurante = restauranteService.buscarOuFalhar(restauranteId);
		restauranteService.abrir(restaurante);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
	
	@PutMapping("/{restauranteId}/fechamento")
	private ResponseEntity<?> fechar (@PathVariable Long restauranteId){
		Restaurante restaurante = restauranteService.buscarOuFalhar(restauranteId);
		restauranteService.fechar(restaurante);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
	
	@PutMapping("/ativacoes")
	private ResponseEntity<?> ativarVarios (@RequestBody List<Long> restaurantesId){
		restauranteService.ativarVarios(restaurantesId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
	
	@DeleteMapping("/ativacoes")
	private ResponseEntity<?> desativarVarios (@RequestBody List<Long> restaurantesId){
		restauranteService.desativarVarios(restaurantesId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
	
	
	
	
	
	
	
	
	
//	Trecho de código para uso com classe RestauranteSpecs
	@GetMapping ("/nome-frete-gratis")
	public ResponseEntity<?> listarComFreteGratis(String nome){
		List<Restaurante> restaurantes = restauranteRepository
				.findAll(RestauranteSpecs.freteGratis()
						.and(RestauranteSpecs.nomeSemelhante(nome)));
		return ResponseEntity.status(HttpStatus.OK).body(restaurantes);
	}	
	
//	@GetMapping ("/nome-frete-gratis")
//	public ResponseEntity<?> listarComFreteGratis(String nome){
//		Specification<Restaurante> s1 = new RestauranteFreteGratisSpec();
//		Specification<Restaurante> s2 = new RestauranteNomeSemelhanteSpec(nome);
//		// Aplica as especificações s1 e s2 como cláusulas de restrição da busca
//		List<Restaurante> restaurantes = restauranteRepository.findAll(s1.and(s2));
//		return ResponseEntity.status(HttpStatus.OK).body(restaurantes);
//	}
	
	
	@GetMapping ("/taxa-frete")
	public ResponseEntity<?> listarTaxaFrete(BigDecimal taxa1, BigDecimal taxa2){
		List<Restaurante> restaurantes = restauranteRepository.findByTaxaFreteBetween(taxa1, taxa2);
		return ResponseEntity.status(HttpStatus.OK).body(restaurantes);
	}
	
	@GetMapping ("/existe")
	public ResponseEntity<?> verificarNome(String nome){
		boolean existe = restauranteRepository.existsByNomeContaining(nome);
		return ResponseEntity.status(HttpStatus.OK).body(existe);
	}
	
	@GetMapping ("/conta-cozinha")
	public ResponseEntity<?> contarCozinha(Long cozinhaId){
		int contagem = restauranteRepository.countByCozinhaId(cozinhaId);
		return ResponseEntity.status(HttpStatus.OK).body(contagem);
	}
	
	@GetMapping ("/nome-e-cozinhaId")
	public ResponseEntity<?> buscarNomeCozinhaId(String nome, Long cozinhaId){
		List<Restaurante> restaurantes = restauranteRepository.buscarNomeCozinhaId(nome, cozinhaId);
		return ResponseEntity.status(HttpStatus.OK).body(restaurantes);
	}
	
	@GetMapping ("/nome-frete")
	public ResponseEntity<?> buscarNomeFrete(String nome, BigDecimal taxaInicial, BigDecimal taxaFinal){
		List<Restaurante> restaurantes = restauranteRepository.buscarNomeTaxa(nome, taxaInicial, taxaFinal);
		return ResponseEntity.status(HttpStatus.OK).body(restaurantes);
	}

}

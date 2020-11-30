package com.algawoks.algafood.api.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.algawoks.algafood.api.converter.EstadoConverter;
import com.algawoks.algafood.api.model.input.EstadoInput;
import com.algawoks.algafood.api.model.output.EstadoOutput;
import com.algawoks.algafood.domain.model.Estado;
import com.algawoks.algafood.domain.repository.EstadoRepository;
import com.algawoks.algafood.domain.service.EstadoService;

@RestController
@RequestMapping("/estados")
public class EstadoController {
	
	@Autowired
	private EstadoRepository estadoRepository;
	
	@Autowired
	private EstadoService estadoService;
	
	@Autowired
	private EstadoConverter estadoConverter;
	
	@GetMapping
	public ResponseEntity<?> listar(){
		List<Estado> estados = estadoRepository.findAll();
		List<EstadoOutput> estadosOut = estadoConverter.toEstadoOutList(estados);
		return ResponseEntity.status(HttpStatus.OK).body(estadosOut);
	}
	
	@GetMapping ("/{estadoId}")
	public ResponseEntity<?> buscar(@PathVariable Long estadoId){
		Estado estado = estadoService.buscarOuFalhar(estadoId);
		EstadoOutput estadoOut = estadoConverter.toEstadoOut(estado);
		return ResponseEntity.status(HttpStatus.OK).body(estadoOut);
	}
	
	@PostMapping
	public ResponseEntity<?> adicionar(@RequestBody @Valid EstadoInput estadoIn){
		Estado estado = estadoConverter.toEstado(estadoIn);
		Estado estadoT1 = estadoService.salvar(estado);
		EstadoOutput estadoOut = estadoConverter.toEstadoOut(estadoT1);
		return ResponseEntity.status(HttpStatus.CREATED).body(estadoOut);
	}
	
	@PutMapping("/{estadoId}")
	public ResponseEntity<?> atualizar(@PathVariable Long estadoId, @RequestBody @Valid EstadoInput estadoIn){
		Estado estadoT1 = estadoService.buscarOuFalhar(estadoId);
		estadoConverter.copyToEstado(estadoIn, estadoT1);
		Estado estadoT2 = estadoService.salvar(estadoT1);
		EstadoOutput estadoOut = estadoConverter.toEstadoOut(estadoT2);
		return ResponseEntity.status(HttpStatus.OK).body(estadoOut);
	}
	
	@DeleteMapping("/{estadoId}")
	public ResponseEntity<?> remover (@PathVariable Long estadoId){
		estadoService.remover(estadoId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

}

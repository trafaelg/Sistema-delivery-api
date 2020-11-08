package com.sistema.delivery.api.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
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

import com.sistema.delivery.api.domain.exception.EntidadeEmUsoException;
import com.sistema.delivery.api.domain.exception.EntidadeNaoExisteException;
import com.sistema.delivery.api.domain.model.Cozinha;
import com.sistema.delivery.api.domain.repository.CozinhaRepository;
import com.sistema.delivery.api.domain.service.CozinhaRepositoryService;

@RestController
@RequestMapping("/cozinha/")
public class CozinhaController {

	@Autowired
	private CozinhaRepository cozinhaRepository;
	
	@Autowired
	private CozinhaRepositoryService cozinhaService;
	
	@GetMapping
	public List<Cozinha> listar()
	{
		return cozinhaRepository.findAll();
	}
	
	@GetMapping("{id}")
	public ResponseEntity<?> retornaId(@PathVariable Long id)
	{
		Optional<Cozinha> cozinha = cozinhaRepository.findById(id);
		if(cozinha.isPresent())
		{
			return ResponseEntity.status(HttpStatus.CREATED).body(cozinha);
		}
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); 
	}
	
	@PostMapping
	public ResponseEntity<?> adicionar(@RequestBody Cozinha cozinha)
	{
		cozinha =  cozinhaService.adiciona(cozinha);
		return ResponseEntity.status(HttpStatus.CREATED).body(cozinha);
	}
	
	@DeleteMapping("{id}")
	public ResponseEntity<?> remova(@PathVariable Long id)
	{
		try {
			
			cozinhaService.remova(id);
			return ResponseEntity.status(HttpStatus.CREATED).build();
			
		} catch (EntidadeNaoExisteException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
			
		} catch (EntidadeEmUsoException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
	}
	
	@PutMapping("{id}")
	public ResponseEntity<?> atualiza(@PathVariable Long id, @RequestBody Cozinha cozinha)
	{

		Optional<Cozinha> cozinhaAtual = cozinhaRepository.findById(id);
		if(cozinhaAtual.isPresent())
		{
		BeanUtils.copyProperties(cozinha, cozinhaAtual.get(), "id");
		Cozinha cozinhanova = cozinhaService.adiciona(cozinhaAtual.get());
			
		return ResponseEntity.status(HttpStatus.CREATED).body(cozinhanova);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}
	
	@GetMapping("consultar-por-nome")
	public List<Cozinha> listarPorNome(String nome)
	{
		return cozinhaRepository.consultaPorNome(nome);
	}
	
}

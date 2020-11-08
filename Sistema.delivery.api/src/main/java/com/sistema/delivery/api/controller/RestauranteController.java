package com.sistema.delivery.api.controller;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sistema.delivery.api.domain.exception.EntidadeEmUsoException;
import com.sistema.delivery.api.domain.exception.EntidadeNaoExisteException;
import com.sistema.delivery.api.domain.model.Restaurante;
import com.sistema.delivery.api.domain.repository.RestauranteRepository;
import com.sistema.delivery.api.domain.service.RestauranteRepositoryServive;

@RestController
@RequestMapping("/restaurante/")
public class RestauranteController {

	 /*
	 * Injeções de dependencias 
	 */
	@Autowired
	private RestauranteRepository restauranteRepository;
	
	/*
	 * Classe de service onde está todas os metodos que altera dados
	 * Também todas as verificações cabiveis são feitas por lá*/
	@Autowired
	private RestauranteRepositoryServive restauranteService;
	
	@GetMapping("por-nome-taxa-frete")
	public List<Restaurante> listarNomeFrete(
			String nome, 
			BigDecimal taxaFreteInicial, 
			BigDecimal taxaFreteFinal)
	{
		return restauranteRepository.listarNomeTaxaFrete(nome, 
				taxaFreteInicial,  
				taxaFreteFinal);
	}
	
	@GetMapping("por-nome-semelhante-taxa-frete")
	public List<Restaurante> porNomeSemelhanteFreteGratis(
			String nome)
	{
		return restauranteRepository.porNomeSemelhanteFreteGratis(nome);
	}
	
	@GetMapping("por-nome-primeiro")
	public Optional<Restaurante> porPrimeiroNaList()
	{
		return restauranteRepository.listarPrimeiroNome();
	}
	
	/*Retornando um restaurante pelo ID*/
	@GetMapping("{id}")
	public ResponseEntity<?> listarPorId(@PathVariable Long id)
	{
		Optional<Restaurante> restaurante = restauranteRepository.findById(id);
		if(restaurante.isPresent())
		{
			return ResponseEntity.status(HttpStatus.CREATED).body(restaurante);
		}
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); 
	}
	
	/*Retorna todos os restaurantes*/
	@GetMapping
	public List<Restaurante> listar()
	{
		return restauranteRepository.findAll();
	}
	
	/*Adiciona novo restaurante*/
	@PostMapping
	public ResponseEntity<?> adiciona(@RequestBody Restaurante restaurante)
	{
		try {
			restaurante = restauranteService.adiciona(restaurante);
			return ResponseEntity.status(HttpStatus.CREATED).body(restaurante);
		} catch (EntidadeNaoExisteException e) {
			/*Mandando a respota para o consumidor da API que não existe o ID informado da cozinha
			  com o status HTTP 404 como respota*/
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
		
	}
	
	/*Remove Restaurante pelo seu ID
	 *Já está programado para quando essa Entidade tiver relacionada em manutenções futuras
	 *para dar um tratamento de exceção e uma resposta para o consumidor da API*/
	@DeleteMapping("{id}")
	public ResponseEntity<?> remova(@PathVariable Long id)
	{
		try {
			restauranteService.remova(id);
			return ResponseEntity.status(HttpStatus.CREATED).build();
		} catch (EntidadeNaoExisteException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
			
		} catch (EntidadeEmUsoException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
	}
	
	/*Alterando de forma não parcial o registro*/
	@PutMapping("{id}")
	public ResponseEntity<?> alteraNaoParcial(
			@PathVariable Long id, 
			@RequestBody Restaurante restaurante)
	{
		try {
			Optional<Restaurante> restauranteAtual = restauranteRepository.findById(id);
			if(restauranteAtual.isPresent())
			{
				BeanUtils.copyProperties(restaurante, restauranteAtual.get(), 
						"id");
				restauranteService.adiciona(restauranteAtual.get());
				return ResponseEntity.status(HttpStatus.CREATED).body(restauranteAtual);
			} 
			
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			
		} catch (EntidadeNaoExisteException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
	
	/*Alterando parcialmente usando o ReflectionUtils API*/
	@PatchMapping("{id}")
	public ResponseEntity<?> alterarPrcial(@PathVariable Long id, 
			@RequestBody Map<String, Object> campos)
	{
		Restaurante restauranteAtual = restauranteRepository
				.findById(id).orElse(null);
		
		if (restauranteAtual == null) { 
			return ResponseEntity.notFound().build();
		}
		
		campos.forEach((nomePropriedade, valorPropriedade) ->{
			ObjectMapper objectMapper = new ObjectMapper();
			Restaurante restauranteOrigem = objectMapper.convertValue(campos, Restaurante.class);
			
			Field field = ReflectionUtils.findField(Restaurante.class, nomePropriedade);
			field.setAccessible(true);
			
			Object novoValor = ReflectionUtils.getField(field, restauranteOrigem);
			
			ReflectionUtils.setField(field, restauranteAtual, novoValor);
		});
		
		return alteraNaoParcial(id, restauranteAtual);
	}
	
}

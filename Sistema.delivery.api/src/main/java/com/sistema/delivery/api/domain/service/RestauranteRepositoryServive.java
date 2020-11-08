package com.sistema.delivery.api.domain.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.sistema.delivery.api.domain.exception.EntidadeEmUsoException;
import com.sistema.delivery.api.domain.exception.EntidadeNaoExisteException;
import com.sistema.delivery.api.domain.model.Cozinha;
import com.sistema.delivery.api.domain.model.Restaurante;
import com.sistema.delivery.api.domain.repository.CozinhaRepository;
import com.sistema.delivery.api.domain.repository.RestauranteRepository;

@Service
public class RestauranteRepositoryServive {

	@Autowired
	private RestauranteRepository restauranteRepository;
	
	@Autowired
	private CozinhaRepository cozinhaRepository;
	
	public Restaurante adiciona(Restaurante restaurante)
	{
		/*Realizando a verificação da existencia da Cozinha a qual o consumidor da API informou*/
		Long cozinhaId = restaurante.getCozinha().getId();
		Optional<Cozinha> cozinha = cozinhaRepository.findById(cozinhaId);
		if(cozinha.isEmpty())
		{
			throw new EntidadeNaoExisteException(
					String.format("A Cozinha do ID: %d não existe. Favor informa uma Cozinha existente.", cozinhaId));
		}
		/*Inserindo uma nova cozinha*/
		restaurante.setCozinha(cozinha.get()); 
		
		/*Depois de ter realizado as verificações necessárias
		  Iremos realizar a inserção na base de dados*/
		return restauranteRepository.save(restaurante);
	}
	
	public void remova(Long id)
	{
		try {
			restauranteRepository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new EntidadeNaoExisteException(
					String.format("O Restaurante do ID %d não existe. Favor informe um Restaurante existente.", id));
			 
		} catch (DataIntegrityViolationException e) {
			throw new EntidadeEmUsoException(
					String.format("O Restaurante do ID %d está em suo. Favor informe um Restaurante que não esteja em uso.", id));
		}
	}
}

package com.sistema.delivery.api.domain.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.sistema.delivery.api.domain.exception.EntidadeEmUsoException;
import com.sistema.delivery.api.domain.exception.EntidadeNaoExisteException;
import com.sistema.delivery.api.domain.model.Cozinha;
import com.sistema.delivery.api.domain.repository.CozinhaRepository;

@Service
public class CozinhaRepositoryService {

	@Autowired
	private CozinhaRepository cozinhaRepository;
	
	public Cozinha adiciona(Cozinha cozinha)
	{
		return cozinhaRepository.save(cozinha); 
	}
	
	public void remova(Long id)
	{
		try {
			cozinhaRepository.deleteById(id); 
		} catch (EmptyResultDataAccessException e) {
			throw new EntidadeNaoExisteException(
					String.format("A Cozinha do ID: %d não existe...", id));
			
		} catch (DataIntegrityViolationException e) {
			throw new EntidadeEmUsoException(
					String.format("A Cozinha do ID: %d está em uso, e não pode ser executado essa ação...", id));
		}
	}
}

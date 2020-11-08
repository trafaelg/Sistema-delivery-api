package com.sistema.delivery.api.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sistema.delivery.api.domain.model.Pagamento;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long>{

}

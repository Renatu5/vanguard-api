package com.renato.vanguard_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.renato.vanguard_api.model.card.Card;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findByProductId(Long productId);

}
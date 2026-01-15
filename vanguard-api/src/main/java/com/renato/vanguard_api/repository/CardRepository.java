package com.renato.vanguard_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.renato.vanguard_api.model.card.Card;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

}
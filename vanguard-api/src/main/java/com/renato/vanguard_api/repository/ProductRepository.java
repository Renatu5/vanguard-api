package com.renato.vanguard_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.renato.vanguard_api.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

}

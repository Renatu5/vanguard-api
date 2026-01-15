package com.renato.vanguard_api.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.renato.vanguard_api.exceptions.ResourceNotFoundException;
import com.renato.vanguard_api.model.Product;
import com.renato.vanguard_api.repository.ProductRepository;

@Service
public class ProductService {
    private final ProductRepository produtoRepository;

    public ProductService(ProductRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public List<Product> listAllProducts() {
        return produtoRepository.findAll();
    }

    public Product searchById(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with the given ID not found: " + id));
    }

    public Product saveProduct(Product product) {
        return produtoRepository.save(product);
    }

    public void deleteProduct(Long id) {
        if (!produtoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product with the given ID not found: " + id);
        }
        produtoRepository.deleteById(id);
    }

}

package com.renato.vanguard_api.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.renato.vanguard_api.model.PaginationResponse;
import com.renato.vanguard_api.model.Product;
import com.renato.vanguard_api.model.card.Card;
import com.renato.vanguard_api.repository.CardRepository;
import com.renato.vanguard_api.services.ProductService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;
    private final CardRepository cardRepository;

    public ProductController(ProductService productsService, CardRepository cardRepository) {
        this.productService = productsService;
        this.cardRepository = cardRepository;
    }

    @GetMapping()
    public ResponseEntity<PaginationResponse<Product>> listProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "true") boolean ascending) {
        Sort sort = ascending ? Sort.by("id").ascending() : Sort.by("id").descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Product> pageResult = productService.listAllProducts(pageable);

        String baseUrl = "/api/products";
        String next = pageResult.hasNext()
                ? String.format("%s?page=%d&size=%d&ascending=%b", baseUrl, page + 1, size, ascending)
                : null;
        String previous = pageResult.hasPrevious()
                ? String.format("%s?page=%d&size=%d&ascending=%b", baseUrl, page - 1, size, ascending)
                : null;
        String url = String.format("%s?page=%d&size=%d&ascending=%b", baseUrl, page, size, ascending);

        return ResponseEntity.ok(new PaginationResponse<Product>(previous, next, url, pageResult.getContent()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> searchProduct(@PathVariable Long id) {
        Product product = productService.searchById(id);
        return ResponseEntity.ok(product);

    }

    @GetMapping("/{id}/cards")
    public ResponseEntity<List<Card>> getCardsByProduct(@PathVariable Long id) {
        List<Card> cards = cardRepository.findByProductId(id);
        return ResponseEntity.ok(cards);
    }

    @PostMapping("")
    public Product createProduct(@RequestBody Product product) {
        return productService.saveProduct(product);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

}

package com.renato.vanguard_api.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.renato.vanguard_api.model.card.Card;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ProductCode;
    private String name;
    private Integer quantity;
    @JsonIgnore
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Card> cards;
    private String cardList;
    private LocalDate releaseDate;

    public Product() {
    }

    public Product(String productCode, String name, String cardList, LocalDate releaseDate) {
        ProductCode = productCode;
        this.name = name;
        this.quantity = 0;
        this.cards = new ArrayList<>();
        this.cardList = cardList;
        this.releaseDate = releaseDate;
    }

    public Product(Product product) {
        this.ProductCode = product.getProductCode();
        this.name = product.getName();
        this.quantity = product.getQuantity();
        this.cards = product.getCards();
        this.cardList = product.getCardList();
        this.releaseDate = product.getReleaseDate();
    }

    public Long getId() {
        return id;
    }

    public String getProductCode() {
        return ProductCode;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public List<Card> getCards() {
        return cards;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public String getCardList() {
        return cardList;
    }

    public int compare(Product p) {
        if (releaseDate.compareTo(p.getReleaseDate()) == 0) {
            System.out.println(ProductCode.replaceAll("\\D+", ""));
            return ProductCode.replaceAll("\\D+", "").compareTo(p.getProductCode().replaceAll("\\D+", ""));
        }
        // System.out.println("Sairam em dias diferetens");
        return releaseDate.compareTo(p.getReleaseDate());
    }
}

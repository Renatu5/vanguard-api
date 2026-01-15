package com.renato.vanguard_api.model;

import java.sql.Date;
import java.util.List;

import com.renato.vanguard_api.model.card.Card;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
    private int quantity;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private List<Card> cards;
    
    private Date releaseDate;

    public Product() {
    }

    public Product(String productCode, String name, int quantity, List<Card> cards, Date releaseDate) {
        ProductCode = productCode;
        this.name = name;
        this.quantity = quantity;
        this.cards = cards;
        this.releaseDate = releaseDate;
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

    public Date getReleaseDate() {
        return releaseDate;
    }

}

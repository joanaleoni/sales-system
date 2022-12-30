package model.domain;

import java.math.BigDecimal;

/**
 *
 * @author joana
 */
public class Product {
    private int id;
    private String name;
    private String description;
    private BigDecimal price;
    private Category category;
    private Stock stock;
    private Supplier supplier;

    public Product() {
        createStock();
    }

    public Product(String name, String description, BigDecimal price, Category category, Stock stock, Supplier supplier) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.stock = stock;
        this.supplier = supplier;
    }
    
    private void createStock(){
        this.stock = new Stock();
        this.stock.setProduct(this);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }
    
    @Override
    public String toString() {
        return name;
    }    
}
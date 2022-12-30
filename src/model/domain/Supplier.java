package model.domain;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author joana
 */
public abstract class Supplier {
    protected int id;
    protected String name;
    protected String email;
    protected String phone;
    private List<Product> products;

    public Supplier() {}

    public Supplier(String name, String email, String phone, List<Product> products) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.products = products;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void add(Product product){
        if(products == null)
            products = new ArrayList<>();
        products.add(product);
    }
    
    public void remove(Product product){
        products.remove(product);
    }

    @Override
    public String toString() {
        return name;
    }
}
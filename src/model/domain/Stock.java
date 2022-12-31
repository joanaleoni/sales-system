package model.domain;

/**
 *
 * @author joana
 */
public class Stock {
    private int quantity;
    private int maxQuantity;
    private int minQuantity;
    private Product product;
    private Situation situation;
    
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(int maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

    public int getMinQuantity() {
        return minQuantity;
    }

    public void setMinQuantity(int minQuantity) {
        this.minQuantity = minQuantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Situation getSituation() {
        return situation;
    }

    public void setSituation(Situation situation) {
        this.situation = situation;
    }
    
    public void replace(int quantity){
        this.quantity += quantity;
    }
    
    public void remove(int quantity){
        this.quantity -= quantity;
    }

    @Override
    public String toString() {
        return "" + quantity;
    }
}
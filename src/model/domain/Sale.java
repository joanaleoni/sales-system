package model.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author joana
 */
public class Sale {
    private int id;
    private LocalDate date;
    private BigDecimal total;
    private boolean paid;
    private double discountFee;
    private static String company;
    private Client client;
    
    private List<SaleItem> saleItems;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getTotal() {
        calculateTotalSale();
        return total;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public double getDiscountFee() {
        return discountFee;
    }

    public void setDiscountFee(double discountFee) {
        this.discountFee = discountFee;
    }

    public static String getCompany() {
        return company;
    }

    public static void setCompany(String company) {
        Sale.company = company;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public List<SaleItem> getSaleItems() {
        return saleItems;
    }
    
    public void add(SaleItem saleItem){
        if(saleItems == null)
            saleItems = new ArrayList<>();
        saleItems.add(saleItem);
        saleItem.setSale(this);
    }
    
    public void remove(SaleItem saleItem){
        saleItems.remove(saleItem);
    }
    
    public void calculateTotalSale(){
        total = new BigDecimal(0.0);
        for(SaleItem item: this.getSaleItems()) {
            total = total.add(item.getTotal());
        }
        
        if(discountFee > 0) {
            BigDecimal discount = new BigDecimal(total.doubleValue() * discountFee / 100.0);
            total = total.subtract(discount);    
        }
    }   
}
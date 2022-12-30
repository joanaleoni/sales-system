package model.domain;

/**
 *
 * @author joana
 */
public class InternationalSupplier extends Supplier {
    private String country;
    private String nif;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }    
}
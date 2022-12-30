package model.domain;

/**
 *
 * @author joana
 */
public class NationalSupplier extends Supplier {
    private String cnpj;

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }
}
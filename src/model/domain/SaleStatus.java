package model.domain;

/**
 *
 * @author joana
 */
public enum SaleStatus {
    COMPLETED(1, "Finalizada"),
    CANCELED(2, "Cancelada"),
    OPEN(3, "Aberta");
    
    private int id;
    private String description;

    private SaleStatus(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }   

    @Override
    public String toString() {
        return description;
    }
}
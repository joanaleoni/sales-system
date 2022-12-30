package model.domain;

/**
 *
 * @author joana
 */
public enum SaleStatus {
    COMPLETED(1, "Completed"),
    CANCELED(2, "Canceled"),
    OPEN(3, "Open");
    
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
}
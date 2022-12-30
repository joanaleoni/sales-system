package model.domain;

/**
 *
 * @author joana
 */
public class Category {
    private int id;
    private String description;

    public Category() {}

    public Category(String description) {
        this.description = description;
    }   

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return description;
    }
}
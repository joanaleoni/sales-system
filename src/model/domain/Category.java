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

    @Override
    public String toString() {
        return description;
    }
}
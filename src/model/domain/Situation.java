package model.domain;

/**
 *
 * @author joana
 */
public enum Situation {
    ACTIVE(1, "Active"), 
    INACTIVE(2, "Inactive"), 
    BLOCKED(3, "Blocked");
    
    private int id;
    private String description;

    private Situation(int id, String description) {
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
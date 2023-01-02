package model.domain;

/**
 *
 * @author joana
 */
public enum Situation {
    ACTIVE(1, "Ativo"), 
    INACTIVE(2, "Inativo"), 
    BLOCKED(3, "Bloqueado");
    
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
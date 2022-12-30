package model.dao;

import exception.SaleException;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.domain.Category;

/**
 *
 * @author joana
 */
public class CategoryDAO {
    private Connection connection;

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void insert(Category category) throws SaleException {
        String sql = "INSERT INTO category(description) VALUES(?)";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, category.getDescription());
            stmt.execute();
        } catch (SQLException ex) {
            Logger.getLogger(CategoryDAO.class.getName()).log(Level.SEVERE, null, ex);
            throw new SaleException("Não foi possível salvar o registro no banco de dados!");
        }
    }

    public void update(Category category) throws SaleException {
        String sql = "UPDATE category SET description=? WHERE id=?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, category.getDescription());
            stmt.setInt(2, category.getId());
            stmt.execute();
        } catch (SQLException ex) {
            Logger.getLogger(CategoryDAO.class.getName()).log(Level.SEVERE, null, ex);
            throw new SaleException("Não foi possível atualizar o registro no banco de dados.");
        }
    }

    public void delete(Category category) throws SaleException {
        String sql = "DELETE FROM category WHERE id=?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, category.getId());
            stmt.execute();
        } catch (SQLException ex) {
            Logger.getLogger(CategoryDAO.class.getName()).log(Level.SEVERE, null, ex);
            throw new SaleException("Não foi possível excluir  o registro do banco de dados.");
        }
    }

    public List<Category> list() throws SaleException {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM category";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Category c = new Category();
                c.setId(rs.getInt("id"));
                c.setDescription(rs.getString("description"));
                categories.add(c);
            }

        } catch (SQLException ex) {
            Logger.getLogger(CategoryDAO.class.getName()).log(Level.SEVERE, null, ex);
            throw new SaleException("Não foi possível realizar a pesquisa no banco de dados");
        }
        return categories;
    }

    public Category search(Category category) throws SaleException {
        String sql = "SELECT * FROM category WHERE id=?";
        Category aux = new Category();
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, category.getId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                category.setDescription(rs.getString("description"));
                aux = category;
            }
        } catch (SQLException ex) {
            Logger.getLogger(CategoryDAO.class.getName()).log(Level.SEVERE, null, ex);
            throw new SaleException("Não foi possível realizar a pesquisa no banco de dados");
        }
        return aux;
    }
}

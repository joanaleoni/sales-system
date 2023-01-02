package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.domain.Product;
import model.domain.Situation;
import model.domain.Stock;

/**
 * 
 * @author joana
 */
public class StockDAO{
    private Connection connection;

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public boolean update(Stock stock) {
        String sql = "UPDATE stock SET quantity=?, min_quantity=?, max_quantity=?, situation=? WHERE id_product=?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, stock.getQuantity());
            stmt.setInt(2, stock.getMinQuantity());
            stmt.setInt(3, stock.getMaxQuantity());
            stmt.setString(4, stock.getSituation().name());
            stmt.setInt(5, stock.getProduct().getId());
            stmt.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(StockDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public List<Product> list() {
        String sql = "SELECT * FROM stock INNER JOIN product ON product.id = stock.id_product";
        List<Product> products = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                Product product = populateVO(rs);
                products.add(product);
            }
        } catch (SQLException ex) {
            Logger.getLogger(StockDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return products;
    }
    
    public List<Product> listByStock(Stock stock) {
        String sql = "SELECT * FROM stock INNER JOIN product ON product.id = stock.id_product WHERE stock.id_product = ?";
        List<Product> products = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, stock.getProduct().getId());
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                Product product = populateVO(rs);
                products.add(product);
            }
        } catch (SQLException ex) {
            Logger.getLogger(StockDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return products;
    }
    
    public Product getStock(Product product) {
        String sql = "SELECT * FROM stock INNER JOIN product ON product.id = stock.id_product WHERE stock.id_product = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, product.getId());
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                return populateVO(rs);
            }
        } catch (SQLException ex) {
            Logger.getLogger(StockDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private Product populateVO(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setId(rs.getInt("id"));
        product.setName(rs.getString("name"));
        product.setDescription(rs.getString("description"));
        product.setPrice(rs.getBigDecimal("price"));
        product.getStock().setQuantity(rs.getInt("quantity"));
        product.getStock().setMinQuantity(rs.getInt("min_quantity"));
        product.getStock().setMaxQuantity(rs.getInt("max_quantity"));
        product.getStock().setSituation(Enum.valueOf(Situation.class, rs.getString("situation")));
        return product;
    }
}
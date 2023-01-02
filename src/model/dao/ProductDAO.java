package model.dao;

import exception.SaleException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.domain.Category;
import model.domain.Product;
import model.domain.Situation;
import model.domain.Supplier;

/**
 * 
 * @author joana
 */
public class ProductDAO{
    private Connection connection;

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public boolean insert(Product product) {
        final String sql = "INSERT INTO product(name, description, price, id_category, id_supplier) VALUES(?,?,?,?,?)";
        final String sqlStock = "INSERT INTO stock(id_product) (SELECT max(id) FROM product)";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getDescription());
            stmt.setBigDecimal(3, product.getPrice());
            stmt.setInt(4, product.getCategory().getId());
            stmt.setInt(5, product.getSupplier().getId());
            stmt.execute();

            stmt = connection.prepareStatement(sqlStock);
            stmt.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean update(Product product) {
        String sql = "UPDATE product SET name=?, description=?, price=?, id_category=?, id_supplier=? WHERE id=?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getDescription());
            stmt.setBigDecimal(3, product.getPrice());
            stmt.setInt(4, product.getCategory().getId());
            stmt.setInt(5, product.getSupplier().getId());
            stmt.setInt(6, product.getId());
            stmt.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean delete(Product product) {
        String sql = "DELETE FROM product WHERE id=?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, product.getId());
            stmt.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public List<Product> list() {
        String sql =  "SELECT * FROM product";
        List<Product> products = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                Product product = populateSingleVO(rs);
                products.add(product);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return products;
    }

    public List<Product> list(Situation situation) {
        String sql =  "SELECT * FROM product p INNER JOIN stock s ON p.id = s.id_product WHERE s.situation = ?";
        List<Product> products = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, situation.name());
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                Product product = populateSingleVO(rs);
                products.add(product);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return products;
    }    
    
    public List<Product> listByStock() throws SaleException {
        String sql =  "SELECT * FROM product p INNER JOIN stock s ON p.id = s.id_product";
        List<Product> products = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            while(rs.next()) {
                Product product = populateFullVO(rs);
                products.add(product);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SaleException ex) {
            throw new SaleException("Não foi possível buscar os produtos.");
        }
        return products;
    }
    
    public List<Product> listByCategory(Category category) {
        String sql =  "SELECT p.id AS product_id, p.name AS product_name, p.description AS product_description, p.price AS product_price, "
                + "c.id AS category_id, c.description AS category_description "
                + "FROM product p INNER JOIN category c ON c.id = p.id_category WHERE c.id = ?";
        List<Product> products = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, category.getId());
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                Product product = populateFullVO(rs);
                products.add(product);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SaleException ex) {
            Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return products;
    }

    public Product search(Product product) {
        String sql =  "SELECT * FROM product p INNER JOIN stock s ON p.id = s.id_product WHERE p.id = ?";
        Product aux = new Product();
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, product.getId());
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                aux = populateFullVO(rs);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SaleException ex) {
            Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return aux;
    }
    
    private Product populateFullVO(ResultSet rs) throws SQLException, SaleException {
        Product product = new Product();
        Category category = new Category();
        
        product.setId(rs.getInt("id"));
        product.setName(rs.getString("name"));
        product.setDescription(rs.getString("description"));
        product.setPrice(rs.getBigDecimal("price"));
        
        category.setId(rs.getInt("id_category"));
        CategoryDAO categoryDAO = new CategoryDAO();
        categoryDAO.setConnection(connection);
        
        try {
            category = categoryDAO.search(category);
        } catch (SaleException ex) {
            throw new SaleException("A categoria do produto não foi encontrada.");
        }
        
        product.setCategory(category);
        
        int idSupplier = rs.getInt("id_supplier");
        SupplierDAO supplierDAO = new SupplierDAO();
        supplierDAO.setConnection(connection);
        Supplier supplier = supplierDAO.search(idSupplier);
        product.setSupplier(supplier);
        
        product.getStock().setProduct(product);
        product.getStock().setSituation(Enum.valueOf(Situation.class, rs.getString("situation")));
        product.getStock().setQuantity(rs.getInt("quantity"));
        product.getStock().setMinQuantity(rs.getInt("min_quantity")); 
        product.getStock().setMaxQuantity(rs.getInt("max_quantity")); 
        
        return product;
    }  
    
    private Product populateSingleVO(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setId(rs.getInt("id"));
        product.setName(rs.getString("name"));
        product.setDescription(rs.getString("description"));
        product.setPrice(rs.getBigDecimal("price"));   
        return product;        
    }    
}
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
import model.domain.Sale;
import model.domain.SaleItem;

/**
 *
 * @author joana
 */
public class SaleItemDAO {
    private Connection connection;

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public boolean insert(SaleItem salteItem) {
        String sql = "INSERT INTO sale_item(quantity, total, id_product, id_sale) VALUES(?,?,?,?)";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, salteItem.getQuantity());
            stmt.setBigDecimal(2, salteItem.getTotal());
            stmt.setInt(3, salteItem.getProduct().getId());
            stmt.setInt(4, salteItem.getSale().getId());
            stmt.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(SaleItemDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean delete(SaleItem saleItem) {
        String sql = "DELETE FROM sale_item WHERE id=?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, saleItem.getId());
            stmt.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(SaleItemDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public List<SaleItem> list() {
        String sql = "SELECT * FROM sale_item";
        List<SaleItem> saleItems = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            while(rs.next()) {
                SaleItem saleItem = new SaleItem();
                Product product = new Product();
                Sale sale = new Sale();
                
                saleItem.setId(rs.getInt("id"));
                saleItem.setQuantity(rs.getInt("quantity"));
                saleItem.setTotal(rs.getBigDecimal("total"));

                product.setId(rs.getInt("id_product"));
                sale.setId(rs.getInt("id_sale"));

                ProductDAO productDAO = new ProductDAO();
                productDAO.setConnection(connection);
                product = productDAO.search(product);
                saleItem.setProduct(product);
                
                SaleDAO saleDAO = new SaleDAO();
                saleDAO.setConnection(connection);
                sale = saleDAO.search(sale);
                saleItem.setSale(sale);
                
                saleItems.add(saleItem);
            }
        } catch (SQLException ex) {
            Logger.getLogger(SaleItemDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return saleItems;
    }

    public List<SaleItem> listBySale(Sale sale) {
        String sql = "SELECT * FROM sale_item WHERE id_sale=?";
        List<SaleItem> saleItems = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, sale.getId());
            ResultSet rs = stmt.executeQuery();
            
            while(rs.next()) {
                SaleItem saleItem = new SaleItem();
                Product product = new Product();
                Sale s = new Sale();
                
                saleItem.setId(rs.getInt("id"));
                saleItem.setQuantity(rs.getInt("quantity"));
                saleItem.setTotal(rs.getBigDecimal("total"));

                product.setId(rs.getInt("id_product"));
                s.setId(rs.getInt("id_sale"));

                ProductDAO productDAO = new ProductDAO();
                productDAO.setConnection(connection);
                product = productDAO.search(product);
                saleItem.setProduct(product);
                
                SaleDAO saleDAO = new SaleDAO();
                saleDAO.setConnection(connection);
                s = saleDAO.search(s);               
                saleItem.setSale(s);

                saleItems.add(saleItem);
            }
        } catch (SQLException ex) {
            Logger.getLogger(SaleItemDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return saleItems;
    }

    public SaleItem search(SaleItem saleItem) {
        String sql = "SELECT * FROM sale_item WHERE id=?";
        SaleItem aux = new SaleItem();
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, saleItem.getId());
            ResultSet rs = stmt.executeQuery();

            if(rs.next()) {
                Product product = new Product();
                Sale sale = new Sale();
                
                saleItem.setId(rs.getInt("id"));
                saleItem.setQuantity(rs.getInt("quantity"));
                saleItem.setTotal(rs.getBigDecimal("total"));

                product.setId(rs.getInt("id_product"));
                sale.setId(rs.getInt("id_sale"));

                ProductDAO productDAO = new ProductDAO();
                productDAO.setConnection(connection);
                product = productDAO.search(product);

                SaleDAO saleDAO = new SaleDAO();
                saleDAO.setConnection(connection);
                sale = saleDAO.search(sale);

                saleItem.setProduct(product);
                saleItem.setSale(sale);

                aux = saleItem;
            }
        } catch (SQLException ex) {
            Logger.getLogger(SaleDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return aux;
    }
}
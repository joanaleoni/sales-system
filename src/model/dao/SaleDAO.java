package model.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import model.domain.Client;
import model.domain.Product;
import model.domain.Sale;
import model.domain.SaleItem;
import model.domain.SaleStatus;

/**
 *
 * @author joana
 */
public class SaleDAO {
    private Connection connection;
    
    public Connection getConnection(){
        return connection;
    }
    
    public void setConnection(Connection connection){
        this.connection = connection;
    }
    
    public boolean insert(Sale sale){
        String sql = "INSERT INTO sale(date, total, paid, discount_fee, company, status, id_client) VALUES(?,?,?,?,?,?,?)";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            connection.setAutoCommit(false);
            stmt.setDate(1, Date.valueOf(sale.getDate()));
            stmt.setBigDecimal(2, sale.getTotal());
            stmt.setBoolean(3, sale.isPaid());
            stmt.setDouble(4, sale.getDiscountFee());
            stmt.setString(5, Sale.getCompany());
            
            if(sale.getSaleStatus() != null) stmt.setString(6, sale.getSaleStatus().name());
            else stmt.setString(6, SaleStatus.OPEN.name());
            
            stmt.setInt(7, sale.getClient().getId());
            stmt.execute();
            
            SaleItemDAO saleItemDAO = new SaleItemDAO();
            ProductDAO productDAO = new ProductDAO();
            StockDAO stockDAO = new StockDAO();
            saleItemDAO.setConnection(connection);            
            productDAO.setConnection(connection);
            stockDAO.setConnection(connection);
            
            for(SaleItem item : sale.getSaleItems()) {
                Product product = item.getProduct();
                item.setSale(this.searchLastSale());
                saleItemDAO.insert(item);
                product.getStock().remove(item.getQuantity());
                stockDAO.update(product.getStock());
            }
            connection.commit();
            connection.setAutoCommit(true);
            return true;
        } catch (SQLException ex) {
            try {
                connection.rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(SaleDAO.class.getName()).log(Level.SEVERE, null, ex1);
            }
            Logger.getLogger(SaleDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }         
    }
    
    public boolean update(Sale sale) {
        String sql = "UPDATE sale SET date=?, total=?, paid=?, discount_fee=?, company=?, status=?, id_client=? WHERE id=?";
        try {
            connection.setAutoCommit(false);
            SaleItemDAO saleItemDAO = new SaleItemDAO();
            ProductDAO productDAO = new ProductDAO();
            StockDAO stockDAO = new StockDAO();
            saleItemDAO.setConnection(connection);
            productDAO.setConnection(connection);            
            stockDAO.setConnection(connection);
            
            Sale previousSale = search(sale);
            List<SaleItem> saleItems = saleItemDAO.listBySale(previousSale);
            for (SaleItem i : saleItems) {
                Product p = stockDAO.getStock(i.getProduct());
                p.getStock().replace(i.getQuantity());
                stockDAO.update(p.getStock());
                saleItemDAO.delete(i);
            }

            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setDate(1, Date.valueOf(sale.getDate()));
            stmt.setBigDecimal(2, sale.getTotal());
            stmt.setBoolean(3, sale.isPaid());
            stmt.setDouble(4, sale.getDiscountFee());
            stmt.setString(5, Sale.getCompany());
            
            if(sale.getSaleStatus() != null) stmt.setString(6, sale.getSaleStatus().name());
            else stmt.setString(6, SaleStatus.OPEN.name());
            
            stmt.setInt(7, sale.getClient().getId());
            stmt.setInt(8, sale.getId());
            stmt.execute();
            
            for(SaleItem i : sale.getSaleItems()) {
                Product p = stockDAO.getStock(i.getProduct());
                p.getStock().remove(i.getQuantity());
                stockDAO.update(p.getStock());
                saleItemDAO.insert(i);
            }
            connection.commit();
            return true;
        } catch (SQLException ex) {
                try {
                    connection.rollback();
                } catch (SQLException exc1) {
                    Logger.getLogger(SaleDAO.class.getName()).log(Level.SEVERE, null, exc1);
                }
            Logger.getLogger(SaleDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean delete(Sale sale) {
        String sql = "DELETE FROM sale WHERE id=?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            try {
                connection.setAutoCommit(false);
                SaleItemDAO saleItemDAO = new SaleItemDAO();
                ProductDAO productDAO = new ProductDAO();
                StockDAO stockDAO = new StockDAO();
                saleItemDAO.setConnection(connection);                
                productDAO.setConnection(connection);                
                stockDAO.setConnection(connection);
                
                for(SaleItem i : sale.getSaleItems()) {
                    Product product = i.getProduct();
                    product.getStock().replace(i.getQuantity());
                    stockDAO.update(product.getStock());
                    saleItemDAO.delete(i);
                }
                
                stmt.setInt(1, sale.getId());
                stmt.execute();
                connection.commit();
            } catch (SQLException exc) {
                try {
                    connection.rollback();
                } catch (SQLException exc1) {
                    Logger.getLogger(SaleDAO.class.getName()).log(Level.SEVERE, null, exc1);
                }
                Logger.getLogger(SaleDAO.class.getName()).log(Level.SEVERE, null, exc);
            }            
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(SaleDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public List<Sale> list() {
        String sql = "SELECT * FROM sale";
        List<Sale> sales = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                Sale sale = new Sale();
                Client client = new Client();
                List<SaleItem> saleItems;

                sale.setId(rs.getInt("id"));
                sale.setDate(rs.getDate("date").toLocalDate());
                sale.setPaid(rs.getBoolean("paid"));
                sale.setDiscountFee(rs.getDouble("discount_fee"));
                sale.setSaleStatus(Enum.valueOf(SaleStatus.class, rs.getString("status")));
                Sale.setCompany(rs.getString("company"));
     
                client.setId(rs.getInt("id_client"));
                ClientDAO clientDAO = new ClientDAO();
                clientDAO.setConnection(connection);
                client = clientDAO.search(client);

                SaleItemDAO saleItemDAO = new SaleItemDAO();
                saleItemDAO.setConnection(connection);
                saleItems = saleItemDAO.listBySale(sale);

                sale.setClient(client);
                sale.setSaleItems(saleItems);
                sales.add(sale);
            }
        } catch (SQLException ex) {
            Logger.getLogger(SaleDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sales;
    }

    public Sale search(Sale sale) {
        String sql = "SELECT * FROM sale WHERE id=?";
        Sale aux = new Sale();
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, sale.getId());
            ResultSet rs = stmt.executeQuery();
            
            if(rs.next()) {
                Client client = new Client();
                aux.setId(rs.getInt("id"));
                aux.setDate(rs.getDate("date").toLocalDate());
                aux.setSaleStatus(Enum.valueOf(SaleStatus.class, rs.getString("status")));
                aux.setPaid(rs.getBoolean("paid"));
                aux.setDiscountFee(rs.getDouble("discount_fee"));
                client.setId(rs.getInt("id_client"));
                aux.setClient(client);
            }
        } catch (SQLException ex) {
            Logger.getLogger(SaleDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return aux;
    }
    
    public Sale search(int id) {
        String sql = "SELECT * FROM sale WHERE id=?";
        Sale aux = new Sale();
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if(rs.next()) {
                Client client = new Client();
                aux.setId(rs.getInt("id"));
                aux.setDate(rs.getDate("date").toLocalDate());
                aux.setSaleStatus(Enum.valueOf(SaleStatus.class, rs.getString("status")));
                aux.setPaid(rs.getBoolean("paid"));
                aux.setDiscountFee(rs.getDouble("discount_fee"));
                client.setId(rs.getInt("id_client"));
                aux.setClient(client);
            }
        } catch (SQLException ex) {
            Logger.getLogger(SaleDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return aux;
    }    
    public Sale searchLastSale() {
        String sql = "SELECT max(id) as max FROM sale";        
        Sale aux = new Sale();
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            if(rs.next()) {
                aux.setId(rs.getInt("max"));
                return aux;
            }
        } catch (SQLException ex) {
            Logger.getLogger(SaleDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return aux;
    }

    public Map<Integer, ArrayList> listQuantitySalesPerMonth() {
        String sql = "SELECT COUNT(id) AS count, EXTRACT(year from date) AS year, "
                   + "EXTRACT(month from date) AS month FROM sale GROUP BY year, "
                   + "month ORDER BY year, month";
        Map<Integer, ArrayList> aux = new HashMap();
        
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()) {
                ArrayList line = new ArrayList();
                if (!aux.containsKey(rs.getInt("year")))
                {
                    line.add(rs.getInt("month"));
                    line.add(rs.getInt("count"));
                    aux.put(rs.getInt("year"), line);
                }else{
                    ArrayList newLine = aux.get(rs.getInt("year"));
                    newLine.add(rs.getInt("month"));
                    newLine.add(rs.getInt("count"));
                }
            }
            
            if(aux.size() > 0) aux = order(aux);
            return aux;
            
        } catch (SQLException ex) {
            Logger.getLogger(SaleDAO.class.getName()).log(Level.SEVERE, null, ex);
        } 
        return aux;
    }
    
    private Map<Integer, ArrayList> order(Map<Integer, ArrayList> sales) {
        LinkedHashMap<Integer, ArrayList> orderedMap = sales.entrySet() 
            .stream() 
            .sorted(Map.Entry.comparingByKey()) 
                .collect(Collectors.toMap(Map.Entry::getKey, 
                    Map.Entry::getValue, //
                    (key, content) -> content, //
                    LinkedHashMap::new));
        return orderedMap;
    }   
}
package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.domain.InternationalSupplier;
import model.domain.NationalSupplier;
import model.domain.Supplier;

/**
 * 
 * @author joana
 */
public class SupplierDAO {
    private Connection connection;

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public boolean insert(Supplier supplier) {
        String sql = "INSERT INTO supplier(name, email, phone) VALUES(?, ?, ?)";
        String sqlFN = "INSERT INTO national_supplier(id_supplier, cnpj) VALUES((SELECT max(id) FROM supplier), ?)";
        String sqlFI = "INSERT INTO international_supplier(id_supplier, nif, country) VALUES((SELECT max(id) FROM supplier), ?, ?)";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, supplier.getName());
            stmt.setString(2, supplier.getEmail());
            stmt.setString(3, supplier.getPhone());
            stmt.execute();
            
            if(supplier instanceof NationalSupplier) {
                stmt = connection.prepareStatement(sqlFN);
                stmt.setString(1, ((NationalSupplier)supplier).getCnpj());
                stmt.execute();
            } else {
                stmt = connection.prepareStatement(sqlFI);
                stmt.setString(1, ((InternationalSupplier)supplier).getNif());
                stmt.setString(2, ((InternationalSupplier)supplier).getCountry());
                stmt.execute();
            }
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(SupplierDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean update(Supplier supplier) {
        String sql = "UPDATE supplier SET name=?, email=?, phone=? WHERE id=?";
        String sqlNS = "UPDATE national_supplier SET cnpj=? WHERE id_supplier = ?";
        String sqlIS = "UPDATE international_supplier SET nif=?, country=? WHERE id_supplier = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, supplier.getName());
            stmt.setString(2, supplier.getEmail());
            stmt.setString(3, supplier.getPhone());
            stmt.setInt(4, supplier.getId());
            stmt.execute();
            if (supplier instanceof NationalSupplier) {
                stmt = connection.prepareStatement(sqlNS);
                stmt.setString(1, ((NationalSupplier)supplier).getCnpj());
                stmt.setInt(2, supplier.getId());
                stmt.execute();
            } else {
                stmt = connection.prepareStatement(sqlIS);
                stmt.setString(1, ((InternationalSupplier)supplier).getNif());
                stmt.setString(2, ((InternationalSupplier)supplier).getCountry());
                stmt.setInt(3, supplier.getId());
                stmt.execute();
            }
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(SupplierDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean delete(Supplier supplier) {
        String sql = "DELETE FROM supplier WHERE id=?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, supplier.getId());
            stmt.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(SupplierDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public List<Supplier> list() {
        String sql = "SELECT * FROM supplier s "
                   + "LEFT JOIN national_supplier n on n.id_supplier = s.id "
                   + "LEFT JOIN international_supplier i on i.id_supplier = s.id";
        List<Supplier> suppliers = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                Supplier supplier = populateVO(rs);
                suppliers.add(supplier);
            }
        } catch (SQLException ex) {
            Logger.getLogger(SupplierDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return suppliers;
    }

    public Supplier search(Supplier supplier) {
        String sql = "SELECT * FROM supplier s "
                   + "LEFT JOIN national_supplier n on n.id_supplier = s.id "
                   + "LEFT JOIN international_supplier i on i.id_supplier = s.id WHERE id=?";
        Supplier aux = null;
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, supplier.getId());
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                aux = populateVO(rs);
            }
        } catch (SQLException ex) {
            Logger.getLogger(SupplierDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return aux;
    }
    
    public Supplier search(int id) {
        String sql = "SELECT * FROM supplier s "
                   + "LEFT JOIN national_supplier n on n.id_supplier = s.id "
                   + "LEFT JOIN international_supplier i on i.id_supplier = s.id WHERE id=?";
        Supplier aux = null;
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                aux = populateVO(rs);
            }
        } catch (SQLException ex) {
            Logger.getLogger(SupplierDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return aux;
    }    
    
    private Supplier populateVO(ResultSet rs) throws SQLException {
        Supplier supplier;
        if (rs.getString("nif") == null || rs.getString("nif").length() <= 0) {
            supplier = new NationalSupplier();
            ((NationalSupplier)supplier).setCnpj(rs.getString("cnpj"));
        } else {
            supplier = new InternationalSupplier();
            ((InternationalSupplier)supplier).setNif(rs.getString("nif"));
            ((InternationalSupplier)supplier).setCountry(rs.getString("country"));
        }
        supplier.setId(rs.getInt("id"));
        supplier.setName(rs.getString("name"));
        supplier.setEmail(rs.getString("email"));
        supplier.setPhone(rs.getString("phone"));
        return supplier;
    }
}
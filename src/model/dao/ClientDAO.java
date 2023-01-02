package model.dao;

import exception.SaleException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.domain.Client;

/**
 *
 * @author joana
 */
public class ClientDAO {
    private Connection connection;

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
    
    public void insert(Client client) throws SaleException {
        String sql = "INSERT INTO client(name, cpf, phone, address, birth_date) VALUES(?, ?, ?, ?, ?)";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, client.getName());
            stmt.setString(2, client.getCpf());
            stmt.setString(3, client.getPhone());
            stmt.setString(4, client.getAddress());
            stmt.setDate(5, Date.valueOf(client.getBirthDate()));
            stmt.execute();
        } catch (java.sql.SQLIntegrityConstraintViolationException ex) {
            Logger.getLogger(ClientDAO.class.getName()).log(Level.SEVERE, null, ex);
            throw new SaleException("Impossível registrar o cliente no banco de dados!");
        } catch (SQLException ex) {
            Logger.getLogger(ClientDAO.class.getName()).log(Level.SEVERE, null, ex);
            throw new SaleException("Impossível registrar o cliente no banco de dados!");
        } 
    }

    public boolean update(Client client) {
        String sql = "UPDATE client SET name=?, cpf=?, phone=?, address=?, birth_date=? WHERE id=?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, client.getName());
            stmt.setString(2, client.getCpf());
            stmt.setString(3, client.getPhone());
            stmt.setString(4, client.getAddress());
            stmt.setDate(5, Date.valueOf(client.getBirthDate()));
            stmt.setInt(6, client.getId());
            stmt.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(ClientDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean delete(Client client) {
        String sql = "DELETE FROM client WHERE id=?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, client.getId());
            stmt.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(ClientDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public List<Client> list() {
        String sql = "SELECT * FROM client";
        List<Client> clients = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                Client client = populateVO(rs);
                clients.add(client);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ClientDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return clients;
    }

    public Client search(Client client) {
        String sql = "SELECT * FROM client WHERE id=?";
        Client aux = new Client();
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, client.getId());
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                aux = populateVO(rs);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ClientDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return aux;
    }
    
    private Client populateVO(ResultSet rs) throws SQLException {
        Client client = new Client();
        client.setId(rs.getInt("id"));
        client.setName(rs.getString("name"));
        client.setCpf(rs.getString("cpf"));
        client.setPhone(rs.getString("phone"));
        client.setAddress(rs.getString("address"));
        client.setBirthDate(rs.getDate("birth_date").toLocalDate());
        return client;
    }
}
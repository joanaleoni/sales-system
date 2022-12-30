
package model.database;

import java.sql.Connection;


public interface Database {    
    public Connection connect();
    public void disconnect(Connection conn);
    public void commit(Connection connection);
    public void rollback(Connection connection);
}

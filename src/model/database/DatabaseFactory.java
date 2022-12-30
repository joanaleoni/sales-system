package model.database;

public class DatabaseFactory {
    public static Database getDatabase(String name){
        if(name.equals("postgresql")) return new DatabasePostgreSQL();
        else if(name.equals("mysql")) return new DatabaseMySQL();
        return null;
    }
}
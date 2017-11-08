package com.ef.Parser.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 
 * @author Silvio
 *
 */
public class ConnectionFactory {

	 private static Connection connection;
	    
	    private static String jdbcUrl = "jdbc:mysql://localhost:3306/parse_log";
	    private static String user = "root";
	    private static String pass = "root";
	 	private static String driver = "com.mysql.jdbc.Driver";
	 	
	 	private static ConnectionFactory instance;

	 	private ConnectionFactory(){
	 		try {
            	Class.forName(driver);
            }catch(Exception ex){
            	ex.getMessage();
            }
	 		 try {
	                connection = DriverManager.getConnection(jdbcUrl, user, pass);
            } catch (SQLException ex) {
                System.out.println("Houve um erro ao conectar com o Banco de Dados.");
            } 
	 	}
	 	
	    public static ConnectionFactory getInstance() {
	        if (instance == null) {
	        	instance = new ConnectionFactory();
	        }
	        return instance;
	    }
	    
	    public Connection getConnection(){
	    	return connection;
	    }
}

package com.ef.Parser.DAO;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.ef.Parser.database.ConnectionFactory;
import com.ef.Parser.entity.Log;

/**
 * 
 * @author Silvio
 *
 */
public class LogDAO {

	private ConnectionFactory connection;
	
    public LogDAO(){    
        this.connection = ConnectionFactory.getInstance();    
    }    
    public void salvar(Log log, String messageIpBlocked){    
            String sql = "INSERT INTO log_informations(date_request,ip,description_request, system_information, comments) VALUES(?,?,?,?,?)";    
            try {    
                PreparedStatement stmt = connection.getConnection().prepareStatement(sql);    
                stmt.setString(1, log.getDateRequest());    
                stmt.setString(2, log.getIp());    
                stmt.setString(3, log.getDescriptionRequest());
                stmt.setString(4, log.getSystemInformation());
                stmt.setString(5, messageIpBlocked);
                stmt.execute();    
                stmt.close();    
            } catch (SQLException u) {    
                throw new RuntimeException(u);    
        }    
    }    
}

package com.ef.Parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;

import com.ef.Parser.DAO.LogDAO;
import com.ef.Parser.entity.Log;
import com.ef.Parser.enumeration.SituationDurationEnum;
import com.ef.Parser.util.ParserUtils;

/**
 * 
 * @author Silvio
 * The class is responsilble to read log file, loads the log to MySQL and checks if a given IP makes more than a certain number of requests for the given duration
 */
public class Parser {
	private static Logger LOGGER = Logger.getLogger(Parser.class.toString());
	
	private static final Integer LINE_VALUE_PARAMETER = 5;
		
	private static final String ARG_PATH_LOG_FILE = "--accesslog";
	private static final String ARG_START_DATE = "--startDate";
	private static final String ARG_DURATION = "--duration";
	private static final String ARG_THRESHOLD = "--threshold";
	private static final String DELIMETER_EQUAL = "=";
	private static final String DELIMETER_DOTE = ".";
	
	private static final String[] DURATION_VALID = {SituationDurationEnum.DAILY.getDescription(), SituationDurationEnum.HOURLY.getDescription()};
	
	private static LocalDateTime requestEndTime = null;
	private static LocalDateTime startDate = null;
	private static String duration = null;
	private static String pathLogFile = null;
	private static Integer threshold = null;
	private static Integer numberRegister = 0;
	
	private static List<String> errosLine = new ArrayList<String>();
	
	
	/***
	 * Main method.
	 * 
	 * @param args
	 *            - Program arguments.
	 */
	public static void main(String[] args) {
		if(args.length != 4){
			LOGGER.info("Invalid number of arguments, expected 4 but currently is " + args.length);
			System.exit(0);
		}
		
		prepareInformationForProcessLogFile(args);
		
		if(ArrayUtils.contains(DURATION_VALID,duration)){
			try
	        {
				LOGGER.info("---- Starting file read.... ");
				processLogFile();
				LOGGER.info("---- Process finished!!");
	        }
	        catch (FileNotFoundException e)
	        {
	        	LOGGER.info("File not found!");
	        }
	        catch (IOException e)
	        {
	            e.printStackTrace();
	        }catch (Exception ex) {
	        	LOGGER.severe("Inconsistent Data, please verify the parameters informated.");
			}
		}else{
			LOGGER.info("Please, verify the information for argument --duration, the only valid arguments are hourly or daily, but was informated:  " + duration);
		}
	}
	
	
	/***
	 * processLogFile method.
	 * 
	 * Read file log and checks if a given IP made more than a certain number of requests for the given duration and save into MySQL database.
	 * 
	 * @exception IOException
	 */
	private static void processLogFile() throws IOException{
		 FileReader fr = new FileReader(pathLogFile);
         BufferedReader br = new BufferedReader(fr);
         String line;
         List<Log> logs = new ArrayList<Log>();
         while ((line = br.readLine()) != null)
         {
         	String[] logInformations = line.split("\\|");
         	
         	if(logInformations.length == LINE_VALUE_PARAMETER)
         	{
     			String logDate =  logInformations[0];
     			
     			LocalDateTime logDateRequestAccess = ParserUtils.formatDate(logDate);
     			
     			if((logDateRequestAccess.isEqual(startDate) || logDateRequestAccess.isAfter(startDate)) &&  (logDateRequestAccess.isEqual(requestEndTime) || logDateRequestAccess.isBefore(requestEndTime))){
     				 Log log = new Log(logInformations[0], logInformations[1],logInformations[2], Long.valueOf(logInformations[3]),logInformations[4]);
	                    logs.add(log);
     			}
         	}else{
         		errosLine.add(line);
         	}
         }
         
         br.close();
         
        Stream<Map.Entry<String,List<Log>>> listGroupingByIp = groupLogInformations(logs);
        
     	listGroupingByIp.forEach(entry -> {
     		List<Log> list = entry.getValue();
     		list.forEach(log -> {
     			
     			String messageIpBlocked = generateMessageIpBlocked(log.getIp());
     			
             	new LogDAO().salvar(log, messageIpBlocked);
             	
             	System.out.println("\nDetail: " + log.toString());
             });
         }); 
	}
	
	/***
	 * groupLogInformations method.
	 * 
	 * Groups the IPs according threshold.
	 * 
	 * @param logs
	 * 
	 * @return listGroupingByIp
	 */
	private static Stream<Map.Entry<String,List<Log>>> groupLogInformations(List<Log> logs){
		Stream<Map.Entry<String,List<Log>>> listGroupingByIp = logs.stream().collect(Collectors.groupingBy(Log::getIp)).entrySet().stream().filter(e -> e.getValue().size() >= threshold);
		
		return listGroupingByIp;
	}
	
	/**
	 * prepareInformationForProcessLogFile method.
	 * 
	 * Prepare the informations before start the process the file.
	 * 
	 * @param args
	 */
	private static void prepareInformationForProcessLogFile(String[] args){
		for (String arg : args) {

			String[] infoArgs = arg.split(DELIMETER_EQUAL);
			
			boolean isValid = ParserUtils.isValidArgument(infoArgs);
			
			if (isValid && infoArgs[0].equals(ARG_PATH_LOG_FILE)) {
				pathLogFile = infoArgs[1];
			}else if (isValid && infoArgs[0].equals(ARG_START_DATE)) {
				startDate = ParserUtils.formatDate(infoArgs[1].replace(DELIMETER_DOTE, " "));
			}else if (isValid && infoArgs[0].equals(ARG_DURATION)) {
				duration = infoArgs[1];
			}else if (isValid && infoArgs[0].equals(ARG_THRESHOLD)) {
				threshold = Integer.parseInt(infoArgs[1]);
			}
		}
		
		if(SituationDurationEnum.HOURLY.getDescription().equals(duration)){
			requestEndTime = startDate.plusHours(1l);
		}else if (SituationDurationEnum.DAILY.getDescription().equals(duration)){
			requestEndTime = startDate.plusDays(1l);
		}
	}
	
	/**
	 * generateMessageIpBlocked method.
	 * 
	 * Generate the message that contain the reason of IP has been blocked.
	 * 
	 * @param ip
	 * @return messageIpBlocled
	 */
	private static String generateMessageIpBlocked(String ip){
		StringBuilder messageIpBlocked = new StringBuilder();
		messageIpBlocked.append("IP ");
		messageIpBlocked.append(ip);
		messageIpBlocked.append(" was blocked because it reached the allowed request limit ");
		messageIpBlocked.append(duration);
		messageIpBlocked.append(" greater than ");
		messageIpBlocked.append(numberRegister);
		
		return messageIpBlocked.toString();
	}
}

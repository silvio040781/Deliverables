package com.ef.Parser.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 
 * @author Silvio
 *
 */
public class ParserUtils {

	 public static LocalDateTime formatDate(String dateArgs){
		 
		 DateTimeFormatter formatter = DateTimeFormatter.ofPattern(getPattern(dateArgs));
		 
		 LocalDateTime dateTime = LocalDateTime.parse(dateArgs, formatter);
		 
		 return dateTime;
	 }
	 
	 private static String getPattern(String dateArgs){
		 
		 String[] args = dateArgs.split(" ");
		 
		 if(args[1].contains(".")){
			 return "yyyy-MM-dd HH:mm:ss.SSS";
		 }else{
			 return "yyyy-MM-dd HH:mm:ss";
		 }
	 }
	 
	 public static boolean isValidArgument(String argument[]){
		 return argument.length == 2? true : false;
	 }
}

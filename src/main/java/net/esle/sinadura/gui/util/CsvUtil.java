/*
 * # Copyright 2008 zylk.net 
 * # 
 * # This file is part of Sinadura. 
 * # 
 * # Sinadura is free software: you can redistribute it and/or modify 
 * # it under the terms of the GNU General Public License as published by 
 * # the Free Software Foundation, either version 2 of the License, or 
 * # (at your option) any later version. 
 * # 
 * # Sinadura is distributed in the hope that it will be useful, 
 * # but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
 * # GNU General Public License for more details. 
 * # 
 * # You should have received a copy of the GNU General Public License 
 * # along with Sinadura. If not, see <http://www.gnu.org/licenses/>. [^] 
 * # 
 * # See COPYRIGHT.txt for copyright notices and details. 
 * #
 */
package net.esle.sinadura.gui.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CsvUtil {

	private static Log log = LogFactory.getLog(CsvUtil.class);

	
	public static List<List<String>> importCSV(String fileName) {
		
		List<List<String>> list = new ArrayList<List<String>>();
		
		try {
			FileInputStream fstream = new FileInputStream(fileName);
			list = parseCSV(fstream);
			
	    // El tratamiento de excepciones no esta bien, la excepcion deberia propagarse y tratarse fuera.
		} catch (FileNotFoundException e) {
			String m = MessageFormat.format(LanguageUtil.getLanguage().getString("error.importing_csv"), fileName);
			log.error(m, e);
			LoggingDesktopController.printError(m);
			
		} catch (IOException e) {
			String m = MessageFormat.format(LanguageUtil.getLanguage().getString("error.importing_csv"), fileName);
			log.error(m, e);
			LoggingDesktopController.printError(m);
		}
		
		return list;
	}
	
	
	public static List<List<String>> parseCSV(InputStream fstream) throws IOException {

		List<List<String>> list = new ArrayList<List<String>>();

		// Get the object of DataInputStream
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line;

		boolean header = true;
		int max_size = 0;
		// Read File Line By Line
		while ((line = br.readLine()) != null) {

			List<String> l = new ArrayList<String>();

			String[] splitLine = line.split(";");

			for (String string : splitLine) {
				l.add(string);
			}

			if (header) {
				// Supongo que es la primera linea y que es la cabecera
				max_size = l.size();
				header = false;
				list.add(l);
			} else if (l.size() == max_size) {
				list.add(l);
			} else {
				log.error("El registro no cumple el patron, no se añadirá (size) " + l.size() + " Tamaño " + max_size);
			}

		}
		// Close the input stream
		in.close();

		return list;
	}

	public static void exportCSV(String fileName, List<List<String>> array) {

		try {
			FileWriter writer = new FileWriter(fileName);

			for (List<String> list : array) {
				for (String s : list) {
					writer.append(s);
					writer.append(';');
				}
				writer.append('\n');
			}
			writer.flush();
			writer.close();

	    // El tratamiento de excepciones no esta bien, la excepcion deberia propagarse y tratarse fuera.
		} catch (IOException e) {
			String m = MessageFormat.format(LanguageUtil.getLanguage().getString("error.exporting_csv"), fileName);
			log.error(m, e);
			LoggingDesktopController.printError(m);
		}
	}
	
	
	/**
	 * Nuevo metodo mediante la libreria CSV de apache (de momento solo para las preferencias de los pdf-profiles).
	 * 
	 * @param fileName
	 * @return
	 */
	public static List<CSVRecord> importCSVNew(String fileName) {
		
		List<CSVRecord> list = new ArrayList<CSVRecord>();
		
		try {
			InputStream is = new FileInputStream(fileName);
		    Reader reader = new InputStreamReader(is);
	        CSVParser csvParser = CSVFormat.EXCEL.parse(reader);
	        list = csvParser.getRecords();
	        
	    // El tratamiento de excepciones no esta bien, la excepcion deberia propagarse y tratarse fuera.
		} catch (FileNotFoundException e) {
			String m = MessageFormat.format(LanguageUtil.getLanguage().getString("error.importing_csv"), fileName);
			log.error(m, e);
			LoggingDesktopController.printError(m);
			
		} catch (IOException e) {
			String m = MessageFormat.format(LanguageUtil.getLanguage().getString("error.importing_csv"), fileName);
			log.error(m, e);
			LoggingDesktopController.printError(m);
		}

		return list; 
	}
	
	/**
	 * Nuevo metodo mediante la libreria CSV de apache (de momento solo para las preferencias de los pdf-profiles).
	 * 
	 * @param fileName
	 * @param array
	 */
	public static void exportCSVNew(String fileName, List<List<String>> array) {

		try {
		
			OutputStream os = new FileOutputStream(fileName);
			PrintWriter outputWriter = new PrintWriter(os);
			CSVPrinter printer = new CSVPrinter(outputWriter, CSVFormat.EXCEL);
        	for (List<String> line : array) {
	            printer.printRecord(line);
	        }
	        printer.close();

	    // El tratamiento de excepciones no esta bien, la excepcion deberia propagarse y tratarse fuera.
		} catch (FileNotFoundException e) {
			String m = MessageFormat.format(LanguageUtil.getLanguage().getString("error.exporting_csv"), fileName);
			log.error(m, e);
			LoggingDesktopController.printError(m);
		} catch (IOException e) {
			String m = MessageFormat.format(LanguageUtil.getLanguage().getString("error.exporting_csv"), fileName);
			log.error(m, e);
			LoggingDesktopController.printError(m);
		}
		
	}
	

}

package com.accenture.sfdc.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Clase para analizar ficheros CSV con el formato
 * nombreCampo1;nombreCampo2;...;nombreCampoN
 * valorCampo1;valorCampo2;...;valorCamporN
 * valorCampo1;valorCampo2;...;valorCamporN
 * valorCampo1;valorCampo2;...;valorCampor3
 * 
 * Sin delimitadores de texto y separados por ";"
 */
public class CSVAnalizer {

	public static final String EMAIL = "Email";
	public static final String NAME = "Name";
	public static final String ZIP = "Zip";
	public static final String TOWN = "Town";
	public static final String FILE = "File";

	/**
	 * Método que obtiene la información contenida en el csv.
	 * 
	 * @return Devuelve una lista que contiene una fila del CSV en cada una de sus
	 *         posiciones.
	 * @throws IOException
	 */
	public static ArrayList<TreeMap<String, String>> analizeCSV(String csvContent) throws IOException {
		csvContent = csvContent.replace("\r\n", "\n");
		ArrayList<TreeMap<String, String>> csvResult = new ArrayList<TreeMap<String, String>>();
		String[] lines = csvContent.split("\n");
		boolean onTitle = true;
		String[] columnNames = null;
		for (String line : lines) {
			if (onTitle) {
				onTitle = false;
				columnNames = line.split(";");
			} else {
				csvResult.add(getCSVRow(line, columnNames));
			}
		}
		return csvResult;
	}

	/**
	 * Método que obtiene cada una de las filas del CSV asociando el nombre de cada
	 * columna con su valor
	 * 
	 * @param line
	 * @param columnNames
	 * @return Devuelve el equivalente a una fila del CSV, teniendo como clave el
	 *         nombre de cada columna.
	 */
	private static TreeMap<String, String> getCSVRow(String line, String[] columnNames) {
		TreeMap<String, String> row = new TreeMap<String, String>();
		String[] rowValues = line.split(";");
		for (int i = 0; i < columnNames.length; i++) {
			row.put(columnNames[i], rowValues[i]);
		}
		return row;
	}

}

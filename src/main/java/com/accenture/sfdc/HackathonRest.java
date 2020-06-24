package com.accenture.sfdc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.accenture.sfdc.utils.CSVAnalizer;
import com.accenture.sfdc.utils.Encoding;
import com.accenture.sfdc.utils.FTPConnection;
import com.accenture.sfdc.utils.QRGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.WriterException;

/**
 * Clase que controla las peticiones REST de la aplicación
 *
 */
@RestController
@RequestMapping("/")
public class HackathonRest {

	private static String FTP_HOST = "nclsistemas.ddns.net";
	private static int FTP_PORT = 55536;
	private static String FTP_USER = "FTPUser";
	private static String FTP_PASS = "Bucefalo123";
	private static String FTP_WORKING_DIR = "/FTPTest";
	private static String CSV_RESOURCE_FILE = "oms_export_suz58.csv";

	/**
	 * Método para gestionar las peticiones entrantes de tipo GET a la URL /qrcode
	 * 
	 * @param text
	 * @param width
	 * @param height
	 * @return Devuelve un String con la imagen del código QR codificada en Base64
	 * @throws WriterException
	 * @throws IOException
	 */
	@RequestMapping(value = "qrcode", method = RequestMethod.GET)
	public static String generateQRCode(@RequestParam String text, @RequestParam int width, @RequestParam int height)
			throws WriterException, IOException {
		QRGenerator qrGenerator = new QRGenerator(text, width, height);
		return Encoding.base64Encode(Encoding.getBytes(qrGenerator.generate(), "png"));
	}

	/**
	 * Método para gestionar la conexion con un servidor FTP. Obtiene un fichero CSV
	 * para analizarlo, obtener los datos y la imagen que se debe descargar.
	 * 
	 * @return Se devuelve un objeto JSON con los datos del CSV y el contenido del
	 *         fichero asociado en Base64
	 * @throws WriterException
	 * @throws IOException
	 */
	@RequestMapping(value = "ftpserver", method = RequestMethod.GET)
	public static String ftpServerConnection() throws WriterException, IOException {
		String response = "";
		FTPConnection connection = null;
		try {
			connection = FTPConnection.getInstance(FTP_HOST, FTP_PORT);
			connection.setCredentials(FTP_USER, FTP_PASS);
			if (connection.connect()) {
				connection.changeWorkingDirectory(FTP_WORKING_DIR);
				byte[] csvBytes = connection.retrieveFile(CSV_RESOURCE_FILE);
				if (csvBytes != null) {
					ArrayList<TreeMap<String, String>> csvData = CSVAnalizer.analizeCSV(new String(csvBytes));
					if (csvData.size() > 0) {
						for (TreeMap<String, String> csvRow : csvData) {
							String filePath = csvRow.get(CSVAnalizer.FILE);
							byte[] fileBytes = connection.retrieveFile(filePath);
							csvRow.put(CSVAnalizer.FILE, Encoding.base64Encode(fileBytes));
						}
						response = new ObjectMapper().writeValueAsString(csvData);
					}
				}
			}
		} catch (Exception ioe) {
			ioe.printStackTrace();
		} finally {
			if (connection != null)
				connection.disconnect();
		}

		return response;
	}

}

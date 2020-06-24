package com.example.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

/**
 * Clase para gestionar la conexion con el FTP. Implementa el patrón Singleton para garantizar sólo una instancia de conexión con el FTP en toda la aplicación
 *
 */
public class FTPConnection {

	private static FTPConnection singleton;

	private FTPClient connection;
	private String host;
	private int port;
	private String user;
	private String password;

	/**
	 * Constructor privado. Únicamente puede ser llamado por esta misma clase, no pudiendo construirse un objeto FTPConnection desde ningun otro lugar de la aplicacion.
	 */
	private FTPConnection() {
		connection = new FTPClient();
	}

	/**
	 * Método que obtiene la instancia de la conexion FTP
	 * @param host
	 * @return
	 */
	public static FTPConnection getInstance(String host, int port) {
		if (singleton == null)
			singleton = new FTPConnection();
		singleton.setHost(host);
		singleton.setPort(port);
		return singleton;
	}

	/**
	 * Método para establecer el host al que se conectará la aplicación
	 * @param host
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * Método para establecer el puerto de conexión al servidor ftp
	 * @param port
	 */
	public void setPort(int port) {
		this.port = port;
	}
	
	/**
	 * Método para establecer las credenciales de conexión con el FTP
	 * @param user
	 * @param password
	 */
	public void setCredentials(String user, String password) {
		this.user = user;
		this.password = password;
	}

	public boolean changeWorkingDirectory(String dirPath) throws IOException {
		if(connection != null && connection.isConnected()) {
			connection.changeWorkingDirectory(dirPath);
			return dirPath.equals(connection.printWorkingDirectory());
		}
		return false;
	}
	
	/**
	 * Método para conectar con el servidor FTP
	 * @return
	 * @throws IOException
	 */
	public boolean connect() throws IOException {
		if (host == null || host.isEmpty()) {
			throw new IOException("You must set host before connect to to FTP Server");
		}
		if ((user == null || user.isEmpty()) && (password == null || password.isEmpty())) {
			throw new IOException("You must set credentials before connec to to FTP Server");
		}
		boolean connected = false;
		connection.connect(host, port);
		if(connection.login(user, password)) {
            connected = FTPReply.isPositiveCompletion(connection.getReplyCode());
		}
		return connected;
	}
	
	/**
	 * Método para obtener ficheros del servidor FTP. Obtiene los bytes del fichero indicado, permitiendo así obtener cualquier tipo de fichero.
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public byte[] retrieveFile(String filePath) throws IOException {
		if(connection == null || !connection.isConnected())
			throw new IOException("Connection not open. Connect to FTP Server before retrieve files");
		InputStream stream = null;
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try {
			connection.enterLocalPassiveMode();
			connection.setFileType(FTP.BINARY_FILE_TYPE);
			connection.setRemoteVerificationEnabled(false);
			stream = connection.retrieveFileStream(filePath);
			int nRead;
		    byte[] data = new byte[4096];
		    while ((nRead = stream.read(data, 0, data.length)) != -1) {
		        buffer.write(data, 0, nRead);
		    }
		    stream.close();
		    while(!connection.completePendingCommand());
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] bytes = buffer.toByteArray();
		buffer.close();
		return bytes;
	}
	
	/**
	 * Método para desconectar del servidor FTP
	 */
	public void disconnect() {
		if(connection != null && connection.isConnected()) {
			try {
				connection.logout();
				connection.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
	}

}

package com.accenture.sfdc.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;

/**
 * Clase para gestionar la codificación de los datos
 *
 */
public class Encoding {

	/**
	 * Método para obtener los bytes de una imagen
	 * 
	 * @param image
	 * @param type
	 * @return Devuelve el conjunto de bytes que forman la imagen
	 * @throws IOException
	 */
	public static byte[] getBytes(BufferedImage image, String type) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ImageIO.write(image, type, bos);
		byte[] imageBytes = bos.toByteArray();
		bos.close();
		return imageBytes;
	}

	/**
	 * Método que condifica un conjunto de bytes en Base64
	 * 
	 * @param bytes
	 * @return Devuelve los bytes recibidos tranformados en Base64
	 */
	public static String base64Encode(byte[] bytes) {
		Base64.Encoder encoder = Base64.getEncoder();
		return encoder.encodeToString(bytes);
	}

}

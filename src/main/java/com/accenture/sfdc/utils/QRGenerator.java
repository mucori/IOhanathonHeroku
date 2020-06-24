package com.accenture.sfdc.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.glxn.qrgen.javase.QRCode;

/**
 * Clase para generar códigos QR
 *
 */
public class QRGenerator {

	private String content;
	private int width;
	private int height;

	/**
	 * Constructor que recibe el contenido, alto y ancho del código que se desea
	 * generar
	 * 
	 * @param content
	 * @param width
	 * @param height
	 */
	public QRGenerator(String content, int width, int height) {
		this.content = content;
		this.width = width;
		this.height = height;
	}

	/**
	 * Método que genera el código QR indicado al construir el objeto
	 * 
	 * @return Devuelve una imagen con el código QR generado.
	 * @throws IOException
	 */
	public BufferedImage generate() throws IOException {
		ByteArrayOutputStream stream = QRCode.from(content).withSize(width, height).stream();

		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(stream.toByteArray());
		BufferedImage result = ImageIO.read(byteArrayInputStream);
		stream.close();
		return result;
	}

}

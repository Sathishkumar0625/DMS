/*
 * package com.proflaut.dms.helper;
 * 
 * import java.io.File;
 * 
 * import net.sourceforge.tess4j.Tesseract; import
 * net.sourceforge.tess4j.util.LoadLibs;
 * 
 * public class OCRServiceImpl {
 * 
 * public static void main(String[] args) {
 * 
 * String result = ""; File imageFile = new
 * File("C:/Users/BILLPC01/Downloads/testimage.jpeg"); Tesseract instance = new
 * Tesseract(); // In case you don't have your own tessdata, let it also be
 * extracted for you File tessDataFolder = LoadLibs.extractTessResources(
 * "C:\\Users\\BILLPC01\\Downloads\\Tess4J-3.4.8-src\\Tess4J\\tessdata\\eng.traineddata"
 * );
 * 
 * // Set the tessdata path
 * instance.setDatapath(tessDataFolder.getAbsolutePath());
 * System.err.println(instance.getClass().getName().toString()); try { result =
 * instance.doOCR(imageFile); System.err.println(result); } catch (Exception e)
 * { e.printStackTrace(); } } }
 */
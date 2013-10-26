package backend;

//package com.sample.excel;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * @author giftsam
 */
public class ExcelSheetReader {
	
	/**
	 * This method is used to read the data's from an excel file.
	 * 
	 * @param fileName
	 *            - Name of the excel file.
	 */
	public List<Auto> readExcelFile(String fileName) {
		/**
		 * Create a new instance for cellDataList
		 */
		List cellDataList = new ArrayList();
		try {
			/**
			 * Create a new instance for FileInputStream class
			 */
			FileInputStream fileInputStream = new FileInputStream(fileName);
			/**
			 * Create a new instance for POIFSFileSystem class
			 */
			POIFSFileSystem fsFileSystem = new POIFSFileSystem(fileInputStream);
			/*
			 * Create a new instance for HSSFWorkBook Class
			 */
			HSSFWorkbook workBook = new HSSFWorkbook(fsFileSystem);
			HSSFSheet hssfSheet = workBook.getSheetAt(0);
			/**
			 * Iterate the rows and cells of the spreadsheet to get all the
			 * datas.
			 */
			Iterator rowIterator = hssfSheet.rowIterator();
			while (rowIterator.hasNext()) {
				HSSFRow hssfRow = (HSSFRow) rowIterator.next();
				Iterator iterator = hssfRow.cellIterator();
				List cellTempList = new ArrayList();
				while (iterator.hasNext()) {
					HSSFCell hssfCell = (HSSFCell) iterator.next();
					cellTempList.add(hssfCell);
				}
				cellDataList.add(cellTempList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		/**
		 *
		 * LLamamos al metodos leerCelas para instanciar autos y guardarlos
		 */
		return leerCeldas(cellDataList);
	}

	/**
	 * This method is used to print the cell data to the console.
	 * 
	 * @param cellDataList
	 *            - List of the data's in the spreadsheet.
	 */
	private List<Auto> leerCeldas(List cellDataList) {
		
		List <Auto> autos = new ArrayList<Auto>();
		
		//recorremos las filas, la primera no nos sirve, ni las ultimas 6
		for (int i = 1; i < cellDataList.size()-6; i++) {
			List cellTempList = (List) cellDataList.get(i);
			
			String OT="";
			String fechaAutorizacion="";
			String requiereMecanica="";
			String tipoSiniestro="";
			
			//recorremos las columnas
			for (int j = 0; j < cellTempList.size(); j++) {
				HSSFCell hssfCell = (HSSFCell) cellTempList.get(j);
				String stringCellValue = hssfCell.toString();
				//System.out.print(stringCellValue + "\t");
				
				
				if(j==0)
					OT = stringCellValue;
				if(j==7)
					fechaAutorizacion = stringCellValue;
				if(j==9)
					requiereMecanica = stringCellValue;
				if(j==10)
				{	
					tipoSiniestro = stringCellValue;
					break;
				}
			}
			
			
			//transformamos la fecha en formato (dd-mm-aaaa) a numero
			String [] fechaAut = fechaAutorizacion.split("-");
			int fecha = Integer.parseInt(fechaAut[0]);
			
			if(fechaAut[1].equals("feb"))
				fecha+=31;
			else if(fechaAut[1].equals("mar"))
				fecha+=31+28;
			else if(fechaAut[1].equals("abr"))
				fecha+=31+28+31;
			else if(fechaAut[1].equals("may"))
				fecha+=31+28+31+30;
			else if(fechaAut[1].equals("jun"))
				fecha+=31+28+31+30+31;
			else if(fechaAut[1].equals("jul"))
				fecha+=31+28+31+30+31+30;
			else if(fechaAut[1].equals("ago"))
				fecha+=31+28+31+30+31+30+31;
			else if(fechaAut[1].equals("sep"))
				fecha+=31+28+31+30+31+30+31+31;
			if(fechaAut[1].equals("oct"))
				fecha+=31+28+31+30+31+30+31+31+30;
			if(fechaAut[1].equals("nov"))
				fecha+=31+28+31+30+31+30+31+31+30+31;
			if(fechaAut[1].equals("dic"))
				fecha+=31+28+31+30+31+30+31+31+30+31+30;
			
			
			
			if(fechaAut[2].equals("2012"))
				fecha+=365;
			
			
			//instanciamos el auto
			Auto auto = new Auto(OT, fecha, requiereMecanica+"", tipoSiniestro);
			autos.add(auto);
			
			//System.out.println();
		}
		return autos;
	}

}
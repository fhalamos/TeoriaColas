package backend;


//paquetes para manipular excel
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





public class Simulacion {

	int cantidadPintores;
	int cantidadDesabolladores;
	int cantidadMecanicos;
	
	List<Trabajador> pintores;
	List<Trabajador> desabolladores;
	List<Trabajador> mecanicos;
	
	
	//autos que aun no "han llegado" segun la simulacion
	List<Auto> autosPendientes;
	
	List<Auto> colaDesabolladura;
	List<Auto> colaPintura;
	List<Auto> colaArmado;
	List<Auto> colaPulido;
	List<Auto> colaAutosListos;
	
	
	public Simulacion(int cP, int cD, int cM)
	{
		cantidadPintores=cP;
		cantidadDesabolladores=cD;
		cantidadMecanicos= cM;
		
		pintores = new ArrayList<Trabajador>();
		desabolladores = new ArrayList<Trabajador>();
		mecanicos = new ArrayList<Trabajador>();
		
		autosPendientes= new ArrayList<Auto>();
		
		colaDesabolladura= new ArrayList<Auto>();
		colaPintura= new ArrayList<Auto>();
		colaArmado= new ArrayList<Auto>();
		colaPulido= new ArrayList<Auto>();
		
		colaAutosListos= new ArrayList<Auto>();
		
		
	}
	
	public void correr()
	{
		
		instanciarPersonal();
		
		cargarAutosDeExcel();
		
		//imprimirAutos();
		
		
		
		//simulamos los 2 años
		for(int i=0; i<365*2; i++)
		{
			//vemos todos los autos que llegan el dia i llego un auto nuevo
			int fechaLlegada = autosPendientes.get(0).getFechaAutorizacion();
			while(i==fechaLlegada)
			{
				colaDesabolladura.add(autosPendientes.get(0));
				reordenarColaDesabolladura(colaDesabolladura);
				autosPendientes.remove(0);
				fechaLlegada=autosPendientes.get(0).getFechaAutorizacion();
			}
			
			
			
			
			
			//revisamos la situacion de cada trabajador
			
			//desabolladores...
			for(Trabajador t : desabolladores)
			{
				//si esta desocupado, le tratamos de asignar trabajo
				if (t.ocupado(i)==false)
				{
					if(colaDesabolladura.size()!=0)
					{
						t.asignarTrabajo(colaDesabolladura.get(0), i);
						colaDesabolladura.remove(0);
					}
				}
				
				//si esta ocupado, pero es su ultimo dia de trabajo, tiene que mover su trabajo a la siguiente cola del proceso
				if(t.ocupado(i)==true && t.ocupado(i+1)==false)
				{
					//agregamos el trabajo a la siguiente cola del proceso
					colaPintura.add(t.getTrabajoActual());
					
					//le quitamos al trabajador ese trabajo
					t.setTrabajoActual(null);
				}

			}
			
			
			//pintores...
			for(Trabajador t : pintores)
			{
				//si esta desocupado, le tratamos de asignar trabajo
				if (t.ocupado(i)==false)
				{
					if(colaPintura.size()!=0)
					{
						t.asignarTrabajo(colaPintura.get(0), i);
						colaPintura.remove(0);
					}
				}
				
				//si esta ocupado, pero es su ultimo dia de trabajo, tiene que mover su trabajo a la siguiente cola del proceso
				if(t.ocupado(i)==true && t.ocupado(i+1)==false)
				{
					//agregamos el trabajo a la siguiente cola del proceso
					colaArmado.add(t.getTrabajoActual());
					
					//le quitamos al trabajador ese trabajo
					t.setTrabajoActual(null);
				}

			}
			
			//mecanicos...
			for(Trabajador t : mecanicos)
			{
				//si esta desocupado, le tratamos de asignar trabajo
				if (t.ocupado(i)==false)
				{
					if(colaArmado.size()!=0)
					{
						t.asignarTrabajo(colaArmado.get(0), i);
						colaArmado.remove(0);
					}
					
					else if(colaPulido.size()!=0)
					{
						t.asignarTrabajo(colaPulido.get(0), i);
						colaPulido.remove(0);
					}
				}
				
				//si esta ocupado, pero es su ultimo dia de trabajo, tiene que mover su trabajo a la siguiente cola del proceso
				if(t.ocupado(i)==true && t.ocupado(i+1)==false)
				{
					//agregamos el trabajo a la siguiente cola del proceso
					//si era la etapa final...
					if(t.getTrabajoActual().getEtapa()==etapa.pulido)
						colaAutosListos.add(t.getTrabajoActual());
					
					//si era la etapa final...
					if(t.getTrabajoActual().getEtapa()==etapa.armado)
						colaPulido.add(t.getTrabajoActual());
					          
					//le quitamos al trabajador ese trabajo
					t.setTrabajoActual(null);
				}

			}
			
			
			
		}
		
		
	}
	
	private List<Auto> getColaTotalDesabolladura(){
	
		List<Auto> trabajosActuales= new ArrayList<Auto>();
		for(int i=0; i<desabolladores.size(); i++)
		{
			for(int j=0; j<desabolladores.get(i).trabajoActual.size(); j++)
			{
				trabajosActuales.add(desabolladores.get(i).trabajoActual.get(j));
			}
		}
		
	
	}
	
	

	private void reordenarColaDesabolladura(List<Auto> colaDesabolladura) {

		//AQUI REORDENAMOS LA COLA SEGUN F.O.
		
		
		
	}

	

	

	private void imprimirAutos() {
		
		for(int i=0; i<autosPendientes.size(); i++)
		{

			
			System.out.print(autosPendientes.get(i).imprimir());
			System.out.println();	
		}
		
		
	}

	private void cargarAutosDeExcel() {


		ExcelSheetReader ExcelReader = new ExcelSheetReader();
		autosPendientes = ExcelReader.readExcelFile("INPUT.xls");
		
		
	}

	private void instanciarPersonal() {
		
		//instanciamos pintores
		for(int i=0; i<cantidadPintores;i++)
			pintores.add(new Trabajador(i, 'p'));

		// instanciamos desabolladore
		for (int i = 0; i < cantidadDesabolladores; i++) 
			desabolladores.add(new Trabajador(i, 'd'));

		// instanciamos mecanicos
		for (int i = 0; i < cantidadMecanicos; i++) 
			mecanicos.add(new Trabajador(i, 'm'));
		
	}
	
}

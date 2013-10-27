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
	
	//List<Auto> autosEnDesabolladura;
	//List<Auto> autosEncolaPintura;
	//List<Auto> autosEncolaArmado;
	//List<Auto> autosEncolaPulido;
	//List<Auto> autosEncolaAutosListos;
	
	
	
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
		for(int i=0; i<365*2*8; i++)
		{
			//vemos todos los autos que llegan el dia i llego un auto nuevo
			if(autosPendientes.size()!=0)
			{	
				int llegada = autosPendientes.get(0).getTiempoAutorizacion();
				while(i==llegada)
				{
					colaDesabolladura.add(autosPendientes.get(0));
					reordenarColaDesabolladura(colaDesabolladura);
					autosPendientes.remove(0);
					llegada=autosPendientes.get(0).getTiempoAutorizacion();
				}
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
					reordenarColaPintura();
					
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
					reordenarColaArmado();
					
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
					//si era la etapa armado
					if(t.getTrabajoActual().getEtapa()==etapa.armado)
						colaPulido.add(t.getTrabajoActual());
						reordenarColaPulido();
					
					
					//si era la etapa final...
					if(t.getTrabajoActual().getEtapa()==etapa.pulido)
						colaAutosListos.add(t.getTrabajoActual());
					
					
					          
					//le quitamos al trabajador ese trabajo
					t.setTrabajoActual(null);
				}

			}
			
			
			
		}
		
		
	}
	
	private void reordenarColaPulido() {
		// PENDIENTE
		
	}

	private void reordenarColaArmado() {
		// PENDIENTE
		
	}

	private void reordenarColaPintura() {
		// PENDIENTE
		
	}

	private List<Auto> calcularDemoras(
			List<Auto> procesoSiguiente, List<Auto> procesoActual, int hora){
		
		boolean seguir=true;
		
		while(seguir)
		{
			
			
			
			

			//revisamos la situacion de cada trabajador
			
			//desabolladores...
			for(Trabajador t : desabolladores)
			{
				//si esta desocupado, le tratamos de asignar trabajo
				if (t.ocupado(hora)==false)
				{
					if(colaDesabolladura.size()!=0)
					{
						t.asignarTrabajo(colaDesabolladura.get(0), hora);
						t.trabajoActual.llegadaDesabolladura=hora;
						colaDesabolladura.remove(0);
					}
				}
				
				//si esta ocupado, pero es su ultimo dia de trabajo, tiene que mover su trabajo a la siguiente cola del proceso
				if(t.ocupado(hora)==true && t.ocupado(hora+1)==false)
				{
					//agregamos el trabajo a la siguiente cola del proceso
					t.trabajoActual.salidaDesabolladura=hora;
					
					colaPintura.add(t.getTrabajoActual());
					
					
					
					
					//le quitamos al trabajador ese trabajo
					t.setTrabajoActual(null);
				}

			}
			
			
			//pintores...
			for(Trabajador t : pintores)
			{
				//si esta desocupado, le tratamos de asignar trabajo
				if (t.ocupado(hora)==false)
				{
					if(colaPintura.size()!=0)
					{
						t.trabajoActual.llegadaPintura=hora;
						t.asignarTrabajo(colaPintura.get(0), hora);
						colaPintura.remove(0);
					}
				}
				
				//si esta ocupado, pero es su ultimo dia de trabajo, tiene que mover su trabajo a la siguiente cola del proceso
				if(t.ocupado(hora)==true && t.ocupado(hora+1)==false)
				{
					//agregamos el trabajo a la siguiente cola del proceso
					colaArmado.add(t.getTrabajoActual());
					t.trabajoActual.salidaPintura=hora;
					
					
					//le quitamos al trabajador ese trabajo
					t.setTrabajoActual(null);
				}

			}
			
			//mecanicos...
			for(Trabajador t : mecanicos)
			{
				//si esta desocupado, le tratamos de asignar trabajo
				if (t.ocupado(hora)==false)
				{
					if(colaArmado.size()!=0)
					{
						t.asignarTrabajo(colaArmado.get(0), hora);
						t.trabajoActual.llegadaArmado=hora;
						colaArmado.remove(0);
					}
					
					else if(colaPulido.size()!=0)
					{
						t.asignarTrabajo(colaPulido.get(0), hora);
						t.trabajoActual.llegadaPulido=hora;
						colaPulido.remove(0);
					}
				}
				
				//si esta ocupado, pero es su ultimo dia de trabajo, tiene que mover su trabajo a la siguiente cola del proceso
				if(t.ocupado(hora)==true && t.ocupado(hora+1)==false)
				{
					//agregamos el trabajo a la siguiente cola del proceso
					//si era la etapa armado
					if(t.getTrabajoActual().getEtapa()==etapa.armado)
						colaPulido.add(t.getTrabajoActual());
						t.trabajoActual.salidaArmado=hora;
						
					
					
					//si era la etapa final...
					if(t.getTrabajoActual().getEtapa()==etapa.pulido)
					{
						t.getTrabajoActual().salidaPulido=hora;
						colaAutosListos.add(t.getTrabajoActual());
					}
					
					
					          
					//le quitamos al trabajador ese trabajo
					t.setTrabajoActual(null);
					
					
					
					
				}
				i++;
				//si no queda ningun vehículo en el sistema se para
				for(int k=0; k<desabolladores.size();k++)
				{
				if(desabolladores.get(k)!=null)
					continue;
				}
				
				for(int k=0; k<pintores.size();k++)
				{
				if(pintores.get(k)!=null)
					continue;
				}
				
				for(int k=0; k<mecanicos.size();k++)
				{
				if(mecanicos.get(k)!=null)
					continue;
				}
				
				seguir=false;

			}
			
			
			
		}
	
		        
		
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

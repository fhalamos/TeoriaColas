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

import java.io.File;
import java.io.FileWriter;


public class Simulacion {

	int cantidadPintores;
	int cantidadDesabolladores;
	int cantidadMecanicos;
	List demoras;

	List<Trabajador> pintores;
	List<Trabajador> desabolladores;
	List<Trabajador> mecanicos;
	

	// autos que aun no "han llegado" segun la simulacion
	List<Auto> autosPendientes;
	
	List<Auto> colaDesabolladura;
	List<Auto> colaPintura;
	List<Auto> colaArmado;
	List<Auto> colaPulido;
	List<Auto> colaAutosListos;

	// List<Auto> autosEnDesabolladura;
	// List<Auto> autosEncolaPintura;
	// List<Auto> autosEncolaArmado;
	// List<Auto> autosEncolaPulido;
	// List<Auto> autosEncolaAutosListos;

	public Simulacion(int cP, int cD, int cM) {
		cantidadPintores = cP;
		cantidadDesabolladores = cD;
		cantidadMecanicos = cM;

		pintores = new ArrayList<Trabajador>();
		desabolladores = new ArrayList<Trabajador>();
		mecanicos = new ArrayList<Trabajador>();

		autosPendientes = new ArrayList<Auto>();

		colaDesabolladura = new ArrayList<Auto>();
		colaPintura = new ArrayList<Auto>();
		colaArmado = new ArrayList<Auto>();
		colaPulido = new ArrayList<Auto>();

		colaAutosListos = new ArrayList<Auto>();
		
		instanciarPersonal();
		cargarAutosDeExcel();

	}

	public void correr() {

		

		// imprimirAutos();

		// simulamos los 2 años (en horas)
		for (int i = 0; i < 365 * 2 * 8; i++) {
			// vemos todos los autos que llegan el dia i llego un auto nuevo
			if (autosPendientes.size() != 0) {
				int llegada = autosPendientes.get(0).getTiempoAutorizacion();
				while (i == llegada) {
					colaDesabolladura.add(autosPendientes.get(0));
					System.out.print("Llego el auto "
							+ autosPendientes.get(0).getOT() + " en t= " + i
							+ " al taller.");
					int tiempoDesabolladoListoAproximado = proximoTrabajadorLibre(
							tipoTrabajador.desabollador, i)
							+ autosPendientes.get(0).tiempoDesabolladura;
					int tiempoPintadoListoAproximado = proximoTrabajadorLibre(
							tipoTrabajador.pintor,
							tiempoDesabolladoListoAproximado)
							+ autosPendientes.get(0).tiempoPintura;
					int tiempoMecanicoListoAproximado = proximoTrabajadorLibre(
							tipoTrabajador.mecanico,
							tiempoPintadoListoAproximado)
							+ autosPendientes.get(0).tiempoArmado
							+ autosPendientes.get(0).tiempoPulido;

					// System.out.print("Estara listo el "+tiempoMecanicoListoAproximado+" (en horas)");
					// System.out.println();

					System.out.print("Ingresamos a cola desabolladura a "
							+ autosPendientes.get(0).getOT() + " en t= " + i);
					System.out.println();

					reordenarColaDesabolladura(colaDesabolladura);
					autosPendientes.remove(0);
					if (autosPendientes.size() != 0)
						llegada = autosPendientes.get(0)
								.getTiempoAutorizacion();
					else
						break;
				}
			}

			// revisamos la situacion de cada trabajador

			// desabolladores...
			for (Trabajador t : desabolladores) {
				// si esta desocupado, le tratamos de asignar trabajo
				if (t.ocupado(i) == false) {
					if (colaDesabolladura.size() != 0) {

						colaDesabolladura.get(0).fijarTiemposTrabajo(
								etapa.desabolladura, i);
						t.asignarTrabajo(colaDesabolladura.get(0), i);
						System.out.print("Le asignamos el auto "
								+ colaDesabolladura.get(0).getOT()
								+ " al desabollador " + t.id + " en t= " + i);
						System.out.println();
						colaDesabolladura.remove(0);
					}
				}

				// si esta ocupado, pero es su ultimo dia de trabajo, tiene que
				// mover su trabajo a la siguiente cola del proceso
				if (t.ocupado(i) == true && t.ocupado(i + 1) == false) {
					// agregamos el trabajo a la siguiente cola del proceso
					colaPintura.add(t.getTrabajoActual());
					reordenarColaPintura();

					// le quitamos al trabajador ese trabajo
					t.setTrabajoActual(null);
				}

			}

			// pintores...
			for (Trabajador t : pintores) {
				// si esta desocupado, le tratamos de asignar trabajo
				if (t.ocupado(i) == false) {
					if (colaPintura.size() != 0) {
						colaPintura.get(0)
								.fijarTiemposTrabajo(etapa.pintura, i);
						t.asignarTrabajo(colaPintura.get(0), i);
						System.out.print("Le asignamos el auto "
								+ colaPintura.get(0).getOT() + " al pintor "
								+ t.id + " en t= " + i);
						System.out.println();

						colaPintura.remove(0);
					}
				}

				// si esta ocupado, pero es su ultimo dia de trabajo, tiene que
				// mover su trabajo a la siguiente cola del proceso
				if (t.ocupado(i) == true && t.ocupado(i + 1) == false) {
					// agregamos el trabajo a la siguiente cola del proceso
					colaArmado.add(t.getTrabajoActual());
					reordenarColaArmado();

					// le quitamos al trabajador ese trabajo
					t.setTrabajoActual(null);
				}

			}

			// mecanicos...
			for (Trabajador t : mecanicos) {
				// si esta desocupado, le tratamos de asignar trabajo
				if (t.ocupado(i) == false) {
					if (colaArmado.size() != 0) {
						colaArmado.get(0).fijarTiemposTrabajo(etapa.armado, i);
						t.asignarTrabajo(colaArmado.get(0), i);
						System.out.print("Le asignamos el auto "
								+ colaArmado.get(0).getOT() + " al mecanico "
								+ t.id + " para armado, en t= " + i);
						System.out.println();
						colaArmado.remove(0);
					}

					else if (colaPulido.size() != 0) {
						colaPulido.get(0).fijarTiemposTrabajo(etapa.pulido, i);
						t.asignarTrabajo(colaPulido.get(0), i);
						System.out.print("Le asignamos el auto "
								+ colaPulido.get(0).getOT() + " al mecanico "
								+ t.id + " para pulido, en t= " + i);
						System.out.println();

						colaPulido.remove(0);
					}
				}

				// si esta ocupado, pero es su ultimo dia de trabajo, tiene que
				// mover su trabajo a la siguiente cola del proceso
				if (t.ocupado(i) == true && t.ocupado(i + 1) == false) {
					// agregamos el trabajo a la siguiente cola del proceso
					// si era la etapa armado
					if (t.getTrabajoActual().getEtapa() == etapa.armado) {
						colaPulido.add(t.getTrabajoActual());
						reordenarColaPulido();
					}

					// si era la etapa final...
					if (t.getTrabajoActual().getEtapa() == etapa.pulido)
						colaAutosListos.add(t.getTrabajoActual());

					// le quitamos al trabajador ese trabajo
					t.setTrabajoActual(null);
				}

			}

		}

	}
	//posicion empieza de 0
	
	
	

	private int proximoTrabajadorLibre(tipoTrabajador tipoTrabajador, int i) {
		// TODO Auto-generated method stub
		int tiempoMinimo = Integer.MAX_VALUE;

		if (tipoTrabajador == tipoTrabajador.desabollador) {
			for (Trabajador d : desabolladores) {
				int tiempoLiberacion = d.proximoTiempoLibreDesde(i);
				if (tiempoLiberacion < tiempoMinimo)
					tiempoMinimo = tiempoLiberacion;
			}
			return tiempoMinimo;
		}

		if (tipoTrabajador == tipoTrabajador.pintor) {
			for (Trabajador d : pintores) {
				int tiempoLiberacion = d.proximoTiempoLibreDesde(i);
				if (tiempoLiberacion < tiempoMinimo)
					tiempoMinimo = tiempoLiberacion;
			}
			return tiempoMinimo;
		}

		if (tipoTrabajador == tipoTrabajador.mecanico) {
			for (Trabajador d : mecanicos) {
				int tiempoLiberacion = d.proximoTiempoLibreDesde(i);
				if (tiempoLiberacion < tiempoMinimo)
					tiempoMinimo = tiempoLiberacion;
			}
			return tiempoMinimo;
		}

		return 0;

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
	
	private void ordenarCola(List<Auto> cola, int posicion, Auto aOrdenar)
	{
		List<Auto> nuevo= new ArrayList<Auto>();
		for(int i=posicion; i<cola.size();i++)
		{
			nuevo.add(cola.get(i));
			cola.remove(i);
		}
		cola.add(aOrdenar);
		for(int i=0; i<nuevo.size(); i++)
		{
		cola.add(nuevo.get(i));	
		}
		//ver si retorna el arreglo
		
	}
	

	private List<Auto> calcularDemoras(int hora, List<Auto> copiaColaDesabolladura, List<Auto>copiaColaPintura,
			List<Auto> copiaColaArmado, List<Auto> copiaColaPulido) {

		List<Trabajador> copiaDesabolladores= new ArrayList<Trabajador>();
		List<Trabajador> copiaPintores= new ArrayList<Trabajador>();
		List<Trabajador> copiaMecanicos= new ArrayList<Trabajador>();
		
		for(int i=0; i< desabolladores.size();i++)
		{
		copiaDesabolladores.add(Clone(desabolladores.get(i)));
		}
		
		for(int i=0; i< pintores.size();i++)
		{
		copiaPintores.add(Clone(pintores.get(i)));
		}
		
		for(int i=0; i< mecanicos.size();i++)
		{
		copiaMecanicos.add(Clone(pintores.get(i)));
		}
		
		//ya está la nueva situacion a simular para que no se cambie la original (instancias nuevas)
		
		
		boolean seguir = true;

		while (seguir) {

			// revisamos la situacion de cada trabajador

			// desabolladores...
			for (Trabajador t : copiaDesabolladores) {
				// si esta desocupado, le tratamos de asignar trabajo
				if (t.ocupado(hora) == false) {
					if (copiaColaDesabolladura.size() != 0) {
						t.asignarTrabajo(copiaColaDesabolladura.get(0), hora);
						t.trabajoActual.llegadaDesabolladura = hora;
						copiaColaDesabolladura.remove(0);
					}
				}

				// si esta ocupado, pero es su ultimo dia de trabajo, tiene que
				// mover su trabajo a la siguiente cola del proceso
				if (t.ocupado(hora) == true && t.ocupado(hora + 1) == false) {
					// agregamos el trabajo a la siguiente cola del proceso
					t.trabajoActual.salidaDesabolladura = hora;

					copiaColaPintura.add(t.getTrabajoActual());

					// le quitamos al trabajador ese trabajo
					t.setTrabajoActual(null);
				}

			}

			// pintores...
			for (Trabajador t : copiaPintores) {
				// si esta desocupado, le tratamos de asignar trabajo
				if (t.ocupado(hora) == false) {
					if (copiaColaPintura.size() != 0) {
						t.trabajoActual.llegadaPintura = hora;
						t.asignarTrabajo(copiaColaPintura.get(0), hora);
						copiaColaPintura.remove(0);
					}
				}

				// si esta ocupado, pero es su ultimo dia de trabajo, tiene que
				// mover su trabajo a la siguiente cola del proceso
				if (t.ocupado(hora) == true && t.ocupado(hora + 1) == false) {
					// agregamos el trabajo a la siguiente cola del proceso
					copiaColaArmado.add(t.getTrabajoActual());
					t.trabajoActual.salidaPintura = hora;

					// le quitamos al trabajador ese trabajo
					t.setTrabajoActual(null);
				}

			}

			// mecanicos...
			for (Trabajador t : copiaMecanicos) {
				// si esta desocupado, le tratamos de asignar trabajo
				if (t.ocupado(hora) == false) {
					if (copiaColaArmado.size() != 0) {
						t.asignarTrabajo(copiaColaArmado.get(0), hora);
						t.trabajoActual.llegadaArmado = hora;
						copiaColaArmado.remove(0);
					}

					else if (copiaColaPulido.size() != 0) {
						t.asignarTrabajo(copiaColaPulido.get(0), hora);
						t.trabajoActual.llegadaPulido = hora;
						copiaColaPulido.remove(0);
					}
				}

				// si esta ocupado, pero es su ultimo dia de trabajo, tiene que
				// mover su trabajo a la siguiente cola del proceso
				if (t.ocupado(hora) == true && t.ocupado(hora + 1) == false) {
					// agregamos el trabajo a la siguiente cola del proceso
					// si era la etapa armado
					if (t.getTrabajoActual().getEtapa() == etapa.armado)
						copiaColaPulido.add(t.getTrabajoActual());
					t.trabajoActual.salidaArmado = hora;

					// si era la etapa final...
					if (t.getTrabajoActual().getEtapa() == etapa.pulido) {
						t.getTrabajoActual().salidaPulido = hora;
						demoras.add(t.getTrabajoActual().salidaPulido);
						
					}

					// le quitamos al trabajador ese trabajo
					t.setTrabajoActual(null);

				}
				hora++;
				// si no queda ningun vehículo en el sistema se para
				for (int k = 0; k < copiaDesabolladores.size(); k++) {
					if (copiaDesabolladores.get(k) != null)
						continue;
				}

				for (int k = 0; k < copiaPintores.size(); k++) {
					if (copiaPintores.get(k) != null)
						continue;
				}

				for (int k = 0; k < copiaMecanicos.size(); k++) {
					if (copiaMecanicos.get(k) != null)
						continue;
				}
				
				if( copiaColaPintura.size()!=0 || copiaColaDesabolladura.size()!=0 || copiaColaPulido.size()!=0
						|| copiaColaArmado.size()!=0)
					continue;

				seguir = false;

			}

		}

		imprimirRegistro();

	}

	private void imprimirRegistro() {
		// TODO Auto-generated method stub
		/* Clase que permite escribir en un archivo de texto */

		try {
			// Crear un objeto File se encarga de crear o abrir acceso a un
			// archivo que se especifica en su constructor
			File archivo = new File("output.txt");

			// Crear objeto FileWriter que sera el que nos ayude a escribir
			// sobre archivo
			FileWriter escribir = new FileWriter(archivo, true);

			// Escribimos en el archivo con el metodo write
			for (int i = 0; i < lineas.size(); i++)
				escribir.write(lineas.get(i));

			// Cerramos la conexion
			escribir.close();
		}

		// Si existe un problema al escribir cae aqui
		catch (Exception e) {
			System.out.println("Error al escribir");
		}
	}

	private void reordenarColaDesabolladura( Auto aIngresar, int posicion,int hora) {

		if( posicion==colaDesabolladura.size()+1)
			return;
		List<Auto> copiaColaDesabolladura=new ArrayList<Auto>();
		List<Auto> copiaColaPintura=new ArrayList<Auto>();
		List<Auto> copiaColaArmado=new ArrayList<Auto>();
		List<Auto> copiaColaPulido=new ArrayList<Auto>();
		Auto a = Clone(aIngresar);
		 
		for(int i=0; i<colaDesabolladura.size();i++)
		{
			copiaColaDesabolladura.add(Clone(colaDesabolladura.get(i)));	
		}
		for(int i=0; i<colaPintura.size();i++)
		{
			copiaColaPintura.add(Clone(colaDesabolladura.get(i)));	
		}
		for(int i=0; i<colaArmado.size();i++)
		{
			copiaColaArmado.add(Clone(colaPintura.get(i)));
		}
		
		for(int i=0; i<colaPulido.size();i++)
		{
			copiaColaPulido.add(Clone(colaPulido.get(i)));
		}
		
		
		
		ordenarCola(copiaColaDesabolladura, posicion, a);
		
		calcularDemoras(hora, copiaColaDesabolladura, copiaColaPintura, copiaColaArmado, copiaColaPulido);
		
		reordenarColaDesabolladura(aIngresar,  posicion++,hora); 

		
		}
	
	 
	
	
		
		

	

	private void imprimirAutos() {

		for (int i = 0; i < autosPendientes.size(); i++) {

			System.out.print(autosPendientes.get(i).imprimir());
			System.out.println();
		}

	}

	private void cargarAutosDeExcel() {

		ExcelSheetReader ExcelReader = new ExcelSheetReader();
		autosPendientes = ExcelReader.readExcelFile("INPUT.xls");

	}

	private void instanciarPersonal() {

		// instanciamos pintores
		for (int i = 0; i < cantidadPintores; i++)
			pintores.add(new Trabajador("Pintor" + i, 'p'));

		// instanciamos desabolladore
		for (int i = 0; i < cantidadDesabolladores; i++)
			desabolladores.add(new Trabajador("Desabollador" + i, 'd'));

		// instanciamos mecanicos
		for (int i = 0; i < cantidadMecanicos; i++)
			mecanicos.add(new Trabajador("Mecanico" + i, 'm'));

	}
	
	public Auto Clone(Auto a)
	{
		Auto b = new Auto(a.OT,  a.tiempoAutorizacion,a.requiereMecanico, a.tipoSiniestro);
		return b;
	
	}
	
	public Trabajador Clone(Trabajador a)
	{
		char l='a';
		if(a.tipo==tipoTrabajador.pintor)
			l='p';
		else if(a.tipo==tipoTrabajador.desabollador)
				l='d';
		else l='m';
		
		
		Trabajador b = new Trabajador (a.id, l);
		b.trabajoActual=Clone(a.trabajoActual);
		return b;
	
	}

}

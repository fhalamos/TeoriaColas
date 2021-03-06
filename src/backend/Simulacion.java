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
//com.sun.org.apache.xpath.internal.operations.

import java.io.File;
import java.io.FileWriter;

public class Simulacion {

	int cantidadPintores;
	int cantidadDesabolladores;
	int cantidadMecanicos;
	int iteracion;
	int [] demoras;
	int[] salidasEsperadas;

	
	List<Trabajador> pintores;
	List<Trabajador> desabolladores;
	List<Trabajador> mecanicos;

	// lineas dnd se imprime el historial
	List<String> lineas;
	
	//lineas dnd guardamos los tiempos de salidas que le infomamos al cliente
	List<String> tiemposSalidasInformadosCliente;

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
		iteracion = 0;

		instanciarPersonal();
		cargarAutosDeExcel();

		// lineas dnd se imprime el historial
		lineas = new ArrayList<String>();
		//lineas dnd se imprimen los avisos a clientes de cuando estarn listos sus autos
		tiemposSalidasInformadosCliente=new ArrayList<String>();
		

	}

	public void correr() {

		// imprimirAutos();

		// simulamos los 2 a�os (en horas)
		for (int i = 0; i < 365 * 2 * 8; i++) {

			// vemos todos los autos que llegan el dia i al taller
			if (autosPendientes.size() != 0) {
				int llegada = autosPendientes.get(0).getTiempoAutorizacion();
				while (i == llegada) {

					// colaDesabolladura.add(autosPendientes.get(0));
					boolean a = hayDesabolladorDisponible(i);
					if(!hayDesabolladorDisponible(i))
					{
					demoras= new int [colaDesabolladura.size()+1];
					salidasEsperadas= new int [colaDesabolladura.size()+1];
					reordenarColaDesabolladura(autosPendientes.get(0), 0, i);
					iteracion = 0;
					
					asignarAcola(autosPendientes.get(0));
					
					}
					
					else 
					{
						demoras= new int [colaDesabolladura.size()+1];
						salidasEsperadas= new int [colaDesabolladura.size()+1];
						reordenarColaDesabolladura(autosPendientes.get(0), 0, i);
						iteracion=0;
						autosPendientes.get(0).salidaEsperada=salidasEsperadas[0];
						colaDesabolladura.add(autosPendientes.get(0));
						
					
					}
					
					

					lineas.add("Llego el auto "
							+ autosPendientes.get(0).getOT() + " en t= " + i
							+ " al taller.");

					// int tiempoDesabolladoListoAproximado =
					// proximoTrabajadorLibre(
					// tipoTrabajador.desabollador, i)
					// + autosPendientes.get(0).tiempoDesabolladura;
					// int tiempoPintadoListoAproximado =
					// proximoTrabajadorLibre(
					// tipoTrabajador.pintor,
					// tiempoDesabolladoListoAproximado)
					// + autosPendientes.get(0).tiempoPintura;
					// int tiempoMecanicoListoAproximado =
					// proximoTrabajadorLibre(
					// tipoTrabajador.mecanico,
					// tiempoPintadoListoAproximado)
					// + autosPendientes.get(0).tiempoArmado
					// + autosPendientes.get(0).tiempoPulido;

					// System.out.print("Estara listo el "+tiempoMecanicoListoAproximado+" (en horas)");
					// System.out.println();

					lineas.add("Ingresamos a cola desabolladura a "
							+ autosPendientes.get(0).getOT() + " en t= " + i);
					// System.out.println();

					// reordenarColaDesabolladura(autosPendientes.get(0), 0, i);
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
						t.asignarTrabajo(colaDesabolladura.get(0), i,
								etapa.desabolladura);
						t.trabajoActual.llegadaDesabolladura=i; 
						
						lineas.add("Le asignamos el auto "
								+ colaDesabolladura.get(0).getOT()
								+ " al desabollador " + t.id + " en t= " + i);
						System.out.print("Le asignamos el auto "
								+ colaDesabolladura.get(0).getOT()
								+ " al desabollador " + t.id + " en t= " + i);
						System.out.println();
						colaDesabolladura.remove(0);
					}
				}

				// si esta ocupado, pero es su ultimo dia de trabajo, tiene que
				// mover su trabajo a la siguiente cola del proceso
				if (i + 1 < 365 * 2 * 8)
					if (t.ocupado(i) == true && t.ocupado(i + 1) == false) {
						// agregamos el trabajo a la siguiente cola del proceso
						t.trabajoActual.salidaDesabolladura=i;
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
						t.asignarTrabajo(colaPintura.get(0), i, etapa.pintura);
						t.trabajoActual.llegadaPintura=i;
						System.out.print("Le asignamos el auto "
								+ colaPintura.get(0).getOT() + " al pintor "
								+ t.id + " en t= " + i);
						lineas.add("Le asignamos el auto "
								+ colaPintura.get(0).getOT() + " al pintor "
								+ t.id + " en t= " + i);
						System.out.println();

						colaPintura.remove(0);
					}
				}

				// si esta ocupado, pero es su ultimo dia de trabajo, tiene que
				// mover su trabajo a la siguiente cola del proceso
				if (i + 1 < 365 * 2 * 8)
					if (t.ocupado(i) == true && t.ocupado(i + 1) == false) {
						// agregamos el trabajo a la siguiente cola del proceso
						t.trabajoActual.salidaPintura=i;
						colaArmado.add(t.getTrabajoActual());
						// reordenarColaArmado();

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
						t.asignarTrabajo(colaArmado.get(0), i, etapa.armado);
						t.trabajoActual.llegadaArmado=i;
						System.out.print("Le asignamos el auto "
								+ colaArmado.get(0).getOT() + " al mecanico "
								+ t.id + " para armado, en t= " + i);
						lineas.add("Le asignamos el auto "
								+ colaArmado.get(0).getOT() + " al mecanico "
								+ t.id + " para armado, en t= " + i);

						System.out.println();
						colaArmado.remove(0);
					}

					else if (colaPulido.size() != 0) {
						colaPulido.get(0).fijarTiemposTrabajo(etapa.pulido, i);
						t.asignarTrabajo(colaPulido.get(0), i, etapa.pulido);
						t.trabajoActual.llegadaPulido=i;
						System.out.print("Le asignamos el auto "
								+ colaPulido.get(0).getOT() + " al mecanico "
								+ t.id + " para pulido, en t= " + i);
						System.out.println();
						lineas.add("Le asignamos el auto "
								+ colaPulido.get(0).getOT() + " al mecanico "
								+ t.id + " para pulido, en t= " + i);
						colaPulido.remove(0);
					}
				}

				// si esta ocupado, pero es su ultimo dia de trabajo, tiene que
				// mover su trabajo a la siguiente cola del proceso
				if (i + 1 < 365 * 2 * 8)
					if (t.ocupado(i) == true && t.ocupado(i + 1) == false) {
						// agregamos el trabajo a la siguiente cola del proceso
						// si era la etapa armado
						if (t.getTrabajoActual().getEtapa() == etapa.armado) {
							
							t.trabajoActual.salidaArmado=i;
							colaPulido.add(t.getTrabajoActual());
							// reordenarColaPulido();
						}

						// si era la etapa final...
						if (t.getTrabajoActual().getEtapa() == etapa.pulido)
							t.trabajoActual.salidaPulido=i;
							colaAutosListos.add(t.getTrabajoActual());

						// le quitamos al trabajador ese trabajo
						t.setTrabajoActual(null);
					}

			}

		}
		imprimirCalcularDemorasTotales();
		imprimirHistorialSimulacion();
		imprimirSalidasEsperadas();
		imprimirDemorasCadaEtapa();
		

	}
	
	private void imprimirDemorasCadaEtapa(){
		
		try {
			// Crear un objeto File se encarga de crear o abrir acceso a un
			// archivo que se especifica en su constructor
			File archivo = new File("demorasCadaEtapa.txt");
			if (archivo.exists()) {
				archivo.delete();
				archivo = new File("demorasCadaEtapa.txt");
			}

			// Crear objeto FileWriter que sera el que nos ayude a escribir
			// sobre archivo
			FileWriter escribir = new FileWriter(archivo, true);
			int tiempoPromedioDesabolladura=0;
			int tiempoPromedioPintura=0;
			int tiempoPromedioArmado=0;
			int tiempoPromedioPulido=0;
			

			for(int i=0; i<colaAutosListos.size();i++)
			{
				
				tiempoPromedioDesabolladura+= (colaAutosListos.get(i).llegadaDesabolladura- 
						colaAutosListos.get(i).tiempoAutorizacion);
				tiempoPromedioPintura+= (colaAutosListos.get(i).llegadaPintura
							-colaAutosListos.get(i).salidaDesabolladura);
				tiempoPromedioArmado+= (colaAutosListos.get(i).llegadaArmado
							- colaAutosListos.get(i).salidaPintura);
				tiempoPromedioPulido+=(colaAutosListos.get(i).llegadaPulido
							- colaAutosListos.get(i).salidaArmado);
				
				/*String tiempoColaDes="";
				String tiempoColaPintura="";
				String tiempoColaArmado="";
				String tiempoColaPulido="";
				
				
				 tiempoColaDes=Integer.toString(colaAutosListos.get(i).llegadaDesabolladura- 
						colaAutosListos.get(i).tiempoAutorizacion);
				 tiempoColaPintura= Integer.toString(colaAutosListos.get(i).llegadaPintura
							-colaAutosListos.get(i).salidaDesabolladura);
				 tiempoColaArmado= Integer.toString(colaAutosListos.get(i).llegadaArmado
							- colaAutosListos.get(i).salidaPintura);
				 tiempoColaPulido= Integer.toString(colaAutosListos.get(i).llegadaPulido
							- colaAutosListos.get(i).salidaArmado);
			
				escribir.write("veh "+ colaAutosListos.get(i).OT + "\t Desabolladura: "+tiempoColaDes
						+"\t Pintura: "+tiempoColaPintura + "\t Armado: "+tiempoColaArmado
						+"\t Pulido: "+tiempoColaPulido+ " \n");*/
			

			// Cerramos la conexion
			
		}
			tiempoPromedioDesabolladura=tiempoPromedioDesabolladura/(colaAutosListos.size());
			tiempoPromedioPintura=tiempoPromedioPintura/(colaAutosListos.size());
			tiempoPromedioArmado=tiempoPromedioArmado/(colaAutosListos.size());
			tiempoPromedioPulido=tiempoPromedioPulido/(colaAutosListos.size());
			
			escribir.write("Desabolladura :" +tiempoPromedioDesabolladura
					+"\t Pintura: "+tiempoPromedioPintura + "\t Armado: "+tiempoPromedioArmado
					+"\t Pulido: "+tiempoPromedioPulido);
			
			escribir.close();
		}

		// Si existe un problema al escribir cae aqui
		catch (Exception e) {
			System.out.println("Error al escribir");
		}
		
	
	}

	private void imprimirSalidasEsperadas() {
		

		// TODO Auto-generated method stub
		/* Clase que permite escribir en un archivo de texto */

		try {
			// Crear un objeto File se encarga de crear o abrir acceso a un
			// archivo que se especifica en su constructor
			File archivo = new File("salidasEsperadas.txt");
			if (archivo.exists()) {
				archivo.delete();
				archivo = new File("salidasEsperadas.txt");
			}

			// Crear objeto FileWriter que sera el que nos ayude a escribir
			// sobre archivo
			FileWriter escribir = new FileWriter(archivo, true);

			for(int i=0; i<tiemposSalidasInformadosCliente.size();i++)
				escribir.write(tiemposSalidasInformadosCliente.get(i)+"\n");
			

			// Cerramos la conexion
			escribir.close();
		}

		// Si existe un problema al escribir cae aqui
		catch (Exception e) {
			System.out.println("Error al escribir");
		}

		
	}

	private void imprimirCalcularDemorasTotales() {

		int sumaDemorasOptimizadas = 0;
		int sumaDemorasNoOptimizadas = 0;
		for (int i = 0; i < colaAutosListos.size(); i++) {
			sumaDemorasOptimizadas += colaAutosListos.get(i).salidaPulido
					- colaAutosListos.get(i).getTiempoAutorizacion();
			sumaDemorasNoOptimizadas += colaAutosListos.get(i).tiempoDeReparacionSegunModeloActual;
		}

		int demoraPromedioOptimizadas = sumaDemorasOptimizadas
				/ colaAutosListos.size();
		int demoraPromedioNoOptimizadas = sumaDemorasNoOptimizadas
				/ colaAutosListos.size();
		
		//calculamos la cantidad de casos en que se cumplio la prediccion
		List<Integer> casosPositivos= new ArrayList<Integer>();
		
		
		
		for(int j=0; j<10; j++)	
		{
			casosPositivos.add(0);
			for (int i = 0; i < colaAutosListos.size(); i++)
			{
				int holgura = j * 10;
				int a = colaAutosListos.get(i).salidaPulido;
				int b = colaAutosListos.get(i).salidaEsperada;
				if (colaAutosListos.get(i).salidaPulido < colaAutosListos
						.get(i).salidaEsperada + 2 + holgura)
					casosPositivos.set(j, casosPositivos.get(j) + 1);
			}
		}

		// TODO Auto-generated method stub
		/* Clase que permite escribir en un archivo de texto */

		try {
			// Crear un objeto File se encarga de crear o abrir acceso a un
			// archivo que se especifica en su constructor
			File archivo = new File("resultados.txt");
			if (archivo.exists()) {
				archivo.delete();
				archivo = new File("resultados.txt");
			}

			// Crear objeto FileWriter que sera el que nos ayude a escribir
			// sobre archivo
			FileWriter escribir = new FileWriter(archivo, true);

			escribir.write("Demora promedio luego de optimizacion:");
			escribir.write(demoraPromedioOptimizadas + "horas." + "\n");

			escribir.write("Demora promedio sin optimizacion:");
			escribir.write(demoraPromedioNoOptimizadas + "horas." + "\n");

			for(int j=0; j<10; j++)
			{
				escribir.write("Porcentaje de autos que cumplieron el plazo anunciado al cliente con holgura "+j*10+" horas:");
				escribir.write(casosPositivos.get(j)*100/colaAutosListos.size() +"."+ "\n");
			}
			// Cerramos la conexion
			escribir.close();
		}

		// Si existe un problema al escribir cae aqui
		catch (Exception e) {
			System.out.println("Error al escribir");
		}

	}

	// posicion empieza de 0

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

	// poner en determinada posicion un veh
	private void ordenarCola(List<Auto> cola, int posicion, Auto aOrdenar) {
		
		cola.add(posicion, aOrdenar);

	}

	// se corre la simulacion de nuevo
	// calculo a traves de simulacion de los tiempos de salida
	private void calcularDemoras(String id, int hora, List<Auto> copiaColaDesabolladura,
			List<Auto> copiaColaPintura, List<Auto> copiaColaArmado,
			List<Auto> copiaColaPulido) {

		//copiar estado del sistema
		List<Trabajador> copiaDesabolladores = new ArrayList<Trabajador>();
		List<Trabajador> copiaPintores = new ArrayList<Trabajador>();
		List<Trabajador> copiaMecanicos = new ArrayList<Trabajador>();

		for (int i = 0; i < desabolladores.size(); i++) {
			copiaDesabolladores.add(Clone(desabolladores.get(i)));
		}

		for (int i = 0; i < pintores.size(); i++) {
			copiaPintores.add(Clone(pintores.get(i)));
		}

		for (int i = 0; i < mecanicos.size(); i++) {
			copiaMecanicos.add(Clone(mecanicos.get(i)));
		}

		// ya est� la nueva situacion a simular para que no se cambie la
		// original (instancias nuevas)

		boolean seguir = true;

		while (seguir) {

			// revisamos la situacion de cada trabajador

			// desabolladores...
			for (Trabajador t : copiaDesabolladores) {
				// si esta desocupado, le tratamos de asignar trabajo

				if (t.ocupado(hora) == false) {
					if (copiaColaDesabolladura.size() != 0) {
						t.asignarTrabajo(copiaColaDesabolladura.get(0), hora,
								etapa.desabolladura);
						t.trabajoActual.llegadaDesabolladura = hora;
						copiaColaDesabolladura.remove(0);
					}
				}

				// si esta ocupado, pero es su ultimo dia de trabajo, tiene que
				// mover su trabajo a la siguiente cola del proceso
				if (hora + 1 < 365 * 2 * 8)
					if (t.ocupado(hora) == true && t.ocupado(hora + 1) == false) {
						// agregamos el trabajo a la siguiente cola del proceso
						t.trabajoActual.salidaDesabolladura = hora;
						t.trabajoActual.setEtapa(null);

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
						t.asignarTrabajo(copiaColaPintura.get(0), hora,
								etapa.pintura);
						t.trabajoActual.llegadaPintura = hora;
						copiaColaPintura.remove(0);
					}
				}

				// si esta ocupado, pero es su ultimo dia de trabajo, tiene que
				// mover su trabajo a la siguiente cola del proceso
				if (hora + 1 < 365 * 2 * 8)
					if (t.ocupado(hora) == true && t.ocupado(hora + 1) == false) {
						// agregamos el trabajo a la siguiente cola del proceso
						t.trabajoActual.salidaPintura = hora;
						t.trabajoActual.setEtapa(null);

						copiaColaArmado.add(t.getTrabajoActual());
						
						// le quitamos al trabajador ese trabajo
						t.setTrabajoActual(null);
					}

			}

			// mecanicos...
			for (Trabajador t : copiaMecanicos) {
				// si esta desocupado, le tratamos de asignar trabajo
				if (t.ocupado(hora) == false) {
					
					if (copiaColaPulido.size() != 0) {
						t.asignarTrabajo(copiaColaPulido.get(0), hora,
								etapa.pulido);
						t.trabajoActual.llegadaPulido = hora;
						copiaColaPulido.remove(0);
					}
					
					else if (copiaColaArmado.size() != 0) 
					{
						t.asignarTrabajo(copiaColaArmado.get(0), hora,
								etapa.armado);
						t.trabajoActual.llegadaArmado = hora;
						copiaColaArmado.remove(0);
					}

					
				}

				// si esta ocupado, pero es su ultimo dia de trabajo, tiene que
				// mover su trabajo a la siguiente cola del proceso
				if (hora + 1 < 365 * 2 * 8)
					if (t.ocupado(hora) == true && t.ocupado(hora + 1) == false) {
						// agregamos el trabajo a la siguiente cola del proceso
						// si era la etapa armado
						if (t.getTrabajoActual().getEtapa() == etapa.armado) {
							t.trabajoActual.salidaArmado = hora;
							t.trabajoActual.setEtapa(null);
							copiaColaPulido.add(t.getTrabajoActual());
							
						}

						// si era la etapa final...
						if (t.getTrabajoActual().getEtapa() == etapa.pulido) {
							if(id.equals(t.getTrabajoActual().OT))
							{
								salidasEsperadas[iteracion]=hora;
							}
							
								
							
							
							t.getTrabajoActual().salidaPulido = hora;
							demoras[iteracion]+= 
							t.getTrabajoActual().salidaPulido-t.getTrabajoActual().tiempoAutorizacion;
						}

						// le quitamos al trabajador ese trabajo
						t.setTrabajoActual(null);

					}
			}
			hora++;
			/*seguir = false;

			// si no queda ningun veh�culo en el sistema se para
			for (int k = 0; k < copiaDesabolladores.size(); k++) {
				Auto aRevisar = copiaDesabolladores.get(k).trabajoActual;
				if (aRevisar != null) {
					seguir = true;
					break;
				}

			}

			if (!seguir) {
				for (int k = 0; k < copiaPintores.size(); k++) {
					Auto aRevisar = copiaPintores.get(k).trabajoActual;
					if (aRevisar != null) {
						seguir = true;
						break;
					}
				}
			}

			if (!seguir) {
				for (int k = 0; k < copiaMecanicos.size(); k++) {
					Auto aRevisar = copiaMecanicos.get(k).trabajoActual;
					if (aRevisar != null) {
						seguir = true;
						break;
					}
				}
			}

			if (copiaColaPintura.size() != 0
					|| copiaColaDesabolladura.size() != 0
					|| copiaColaPulido.size() != 0
					|| copiaColaArmado.size() != 0)
				seguir = true;
				*/
			if(hora==365*8*2-1)
				seguir=false;
		}

	}

	private void imprimirHistorialSimulacion() {
		// TODO Auto-generated method stub
		/* Clase que permite escribir en un archivo de texto */

		try {
			// Crear un objeto File se encarga de crear o abrir acceso a un
			// archivo que se especifica en su constructor
			File archivo = new File("historialSimulacion.txt");

			// Crear objeto FileWriter que sera el que nos ayude a escribir
			// sobre archivo
			FileWriter escribir = new FileWriter(archivo, true);

			// Escribimos en el archivo con el metodo write
			for (int i = 0; i < lineas.size(); i++) {
				escribir.write(lineas.get(i) + "\n");
			}

			// Cerramos la conexion
			escribir.close();
		}

		// Si existe un problema al escribir cae aqui
		catch (Exception e) {
			System.out.println("Error al escribir");
		}
	}

	// metodo recursivo para variar las posiciones en cola y calcular demoras
	private void reordenarColaDesabolladura(Auto aIngresar, int posicion,
			int hora) {

		if (posicion == colaDesabolladura.size() + 1)
			return;
		List<Auto> copiaColaDesabolladura = new ArrayList<Auto>();
		List<Auto> copiaColaPintura = new ArrayList<Auto>();
		List<Auto> copiaColaArmado = new ArrayList<Auto>();
		List<Auto> copiaColaPulido = new ArrayList<Auto>();
		Auto a = Clone(aIngresar);

		for (int i = 0; i < colaDesabolladura.size(); i++) {
			copiaColaDesabolladura.add(Clone(colaDesabolladura.get(i)));
		}
		for (int i = 0; i < colaPintura.size(); i++) {
			copiaColaPintura.add(Clone(colaPintura.get(i)));
		}
		for (int i = 0; i < colaArmado.size(); i++) {
			copiaColaArmado.add(Clone(colaArmado.get(i)));
		}

		for (int i = 0; i < colaPulido.size(); i++) {
			copiaColaPulido.add(Clone(colaPulido.get(i)));
		}

		ordenarCola(copiaColaDesabolladura, posicion, a);

		calcularDemoras(a.OT, hora, copiaColaDesabolladura, copiaColaPintura,
				copiaColaArmado, copiaColaPulido);
		iteracion++; // pendiente averiguar para que cosa
		posicion++;
		reordenarColaDesabolladura(aIngresar, posicion, hora);

	}

	// asignar a la cola una vez sabida la posicion a tomar, ver mas CRITERIOS
	// de aceptacion
	private void asignarAcola(Auto a) {
		
		int comparar =  demoras[0];
		int pos = 0;
		for (int i = 0; i < demoras.length; i++) { //i tenia valor 1
			if (comparar >  demoras[i] ){
				pos = i;
				comparar =  demoras[i];
			}
		}
		a.salidaEsperada=salidasEsperadas[pos];
		ordenarCola(colaDesabolladura, pos, a);
		
		if(a.salidaEsperada!=0)
			tiemposSalidasInformadosCliente.add("El cliente "+a.getOT()+" ingreso a las "+a.tiempoAutorizacion+", sale a las "+a.salidaEsperada+" horas.");
		else
			tiemposSalidasInformadosCliente.add("El cliente "+a.getOT()+" no alcanzar� a salir en los 2 a�os de simulacion");
		
	}

	private void imprimirAutos() {

		for (int i = 0; i < autosPendientes.size(); i++) {

			System.out.print(autosPendientes.get(i).imprimir());
			System.out.println();
		}

	}

	private void cargarAutosDeExcel() {

		ExcelSheetReader ExcelReader = new ExcelSheetReader();
		autosPendientes = ExcelReader.readExcelFile("input.xls");

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

	public Auto Clone(Auto a) {
		// ver que pasa si no tiene auto el trabajador, osea es null
		if (a != null) {
			Auto b = new Auto(a.OT, a.tiempoAutorizacion, a.requiereMecanico,
					a.tipoSiniestro, a.tiempoDeReparacionSegunModeloActual);
			return b;
		} else
			return null;
	}

	public Trabajador Clone(Trabajador a) {
		if (a != null) {
			char l = 'a';
			if (a.tipo == tipoTrabajador.pintor)
				l = 'p';
			else if (a.tipo == tipoTrabajador.desabollador)
				l = 'd';
			else
				l = 'm';

			Trabajador b = new Trabajador(a.id, l);
			b.trabajoActual = Clone(a.trabajoActual);

			// copio calendario
			for (int i = 0; i < a.calendario.length; i++) {
				b.calendario[i] = a.calendario[i];
			}

			return b;
		}

		else
			return null;

	}
	
	public boolean hayDesabolladorDisponible(int hora)
	{
		for (Trabajador t : desabolladores)
		{
			if(!t.ocupado(hora))
				return true;
				
		}
		
		return false;
		}
		
	}
	



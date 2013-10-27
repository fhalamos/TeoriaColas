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

	List <String> lineas;
	
	int cantidadPintores;
	int cantidadDesabolladores;
	int cantidadMecanicos;

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
					
					int tiempoPintadoListoAproximado=Integer.MAX_VALUE;
					if(tiempoDesabolladoListoAproximado<365*8*2)
						tiempoPintadoListoAproximado= proximoTrabajadorLibre(
							tipoTrabajador.pintor,
							tiempoDesabolladoListoAproximado)
							+ autosPendientes.get(0).tiempoPintura;
					
					int tiempoMecanicoListoAproximado=Integer.MAX_VALUE;
					if(tiempoPintadoListoAproximado<365*2*8)
						tiempoMecanicoListoAproximado= proximoTrabajadorLibre(
							tipoTrabajador.mecanico,
							tiempoPintadoListoAproximado)
							+ autosPendientes.get(0).tiempoArmado
							+ autosPendientes.get(0).tiempoPulido;

					// System.out.print("Estara listo el "+tiempoMecanicoListoAproximado+" (en horas)");
					// System.out.println();

					System.out.print("Ingresamos a cola desabolladura.");
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

	private List<Auto> calcularDemoras(List<Auto> procesoSiguiente,
			List<Auto> procesoActual, int hora) {

		Simulacion aux = new Simulacion(cantidadPintores, cantidadDesabolladores, cantidadMecanicos);
		
		boolean seguir = true;

		while (seguir) {

			// revisamos la situacion de cada trabajador

			// desabolladores...
			for (Trabajador t : aux.desabolladores) {
				// si esta desocupado, le tratamos de asignar trabajo
				if (t.ocupado(hora) == false) {
					if (aux.colaDesabolladura.size() != 0) {
						t.asignarTrabajo(aux.colaDesabolladura.get(0), hora);
						t.trabajoActual.llegadaDesabolladura = hora;
						aux.colaDesabolladura.remove(0);
					}
				}

				// si esta ocupado, pero es su ultimo dia de trabajo, tiene que
				// mover su trabajo a la siguiente cola del proceso
				if (t.ocupado(hora) == true && t.ocupado(hora + 1) == false) {
					// agregamos el trabajo a la siguiente cola del proceso
					t.trabajoActual.salidaDesabolladura = hora;

					aux.colaPintura.add(t.getTrabajoActual());

					// le quitamos al trabajador ese trabajo
					t.setTrabajoActual(null);
				}

			}

			// pintores...
			for (Trabajador t : pintores) {
				// si esta desocupado, le tratamos de asignar trabajo
				if (t.ocupado(hora) == false) {
					if (aux.colaPintura.size() != 0) {
						t.trabajoActual.llegadaPintura = hora;
						t.asignarTrabajo(aux.colaPintura.get(0), hora);
						aux.colaPintura.remove(0);
					}
				}

				// si esta ocupado, pero es su ultimo dia de trabajo, tiene que
				// mover su trabajo a la siguiente cola del proceso
				if (t.ocupado(hora) == true && t.ocupado(hora + 1) == false) {
					// agregamos el trabajo a la siguiente cola del proceso
					aux.colaArmado.add(t.getTrabajoActual());
					t.trabajoActual.salidaPintura = hora;

					// le quitamos al trabajador ese trabajo
					t.setTrabajoActual(null);
				}

			}

			// mecanicos...
			for (Trabajador t : mecanicos) {
				// si esta desocupado, le tratamos de asignar trabajo
				if (t.ocupado(hora) == false) {
					if (aux.colaArmado.size() != 0) {
						t.asignarTrabajo(aux.colaArmado.get(0), hora);
						t.trabajoActual.llegadaArmado = hora;
						aux.colaArmado.remove(0);
					}

					else if (aux.colaPulido.size() != 0) {
						t.asignarTrabajo(aux.colaPulido.get(0), hora);
						t.trabajoActual.llegadaPulido = hora;
						aux.colaPulido.remove(0);
					}
				}

				// si esta ocupado, pero es su ultimo dia de trabajo, tiene que
				// mover su trabajo a la siguiente cola del proceso
				if (t.ocupado(hora) == true && t.ocupado(hora + 1) == false) {
					// agregamos el trabajo a la siguiente cola del proceso
					// si era la etapa armado
					if (t.getTrabajoActual().getEtapa() == etapa.armado)
						aux.colaPulido.add(t.getTrabajoActual());
					t.trabajoActual.salidaArmado = hora;

					// si era la etapa final...
					if (t.getTrabajoActual().getEtapa() == etapa.pulido) {
						t.getTrabajoActual().salidaPulido = hora;
						aux.colaAutosListos.add(t.getTrabajoActual());
					}

					// le quitamos al trabajador ese trabajo
					t.setTrabajoActual(null);

				}
				hora++;
				// si no queda ningun vehículo en el sistema se para
				for (int k = 0; k < aux.desabolladores.size(); k++) {
					if (aux.desabolladores.get(k) != null)
						continue;
				}

				for (int k = 0; k < aux.pintores.size(); k++) {
					if (aux.pintores.get(k) != null)
						continue;
				}

				for (int k = 0; k < aux.mecanicos.size(); k++) {
					if (aux.mecanicos.get(k) != null)
						continue;
				}

				seguir = false;

			}

		}

		return null;

	}

	private void reordenarColaDesabolladura(List<Auto> colaDesabolladura) {

		// AQUI REORDENAMOS LA COLA SEGUN F.O.

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

}

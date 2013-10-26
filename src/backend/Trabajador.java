package backend;
import java.util.*;

enum tipoTrabajador {pintor, desabollador, mecanico};

public class Trabajador {
	
	int id;
	boolean [] calendario;
	tipoTrabajador tipo;
	Auto trabajoActual;
	
	//Date now = new Date();
	
	
	
	public Trabajador (int id, char tipo)
	{
		this.id = id;
		
		if(tipo=='p')
			this.tipo = tipoTrabajador.pintor;
		if(tipo=='d')
			this.tipo = tipoTrabajador.desabollador;
		if(tipo=='m')
			this.tipo = tipoTrabajador.mecanico;
			
		
		//creo que por defecto el arreglo viene con falses
		calendario = new boolean [365*2];
		
	}
	
	//retorna true si el trabajador esta desocupado el dia i
	public boolean ocupado (int i)
	{
		return calendario[i];
	}

	
	//le asigna al trabajador el trabajo propio de 'auto', el dia 'dia'
	public void asignarTrabajo(Auto auto, int dia) {

		//le llenamos el horario al trabajador
		trabajoActual=auto;
		
		for(int i = dia; i < dia+auto.getTiempoDesabolladura(); i++)
		{
			calendario[i]=true;
		}
		
	}
	
	public Auto getTrabajoActual()
	{
		return trabajoActual;
	}

	public void setTrabajoActual(Auto auto)
	{
		trabajoActual=auto;
	}
}

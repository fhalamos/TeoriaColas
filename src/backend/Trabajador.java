package backend;
import java.util.*;

enum tipoTrabajador {pintor, desabollador, mecanico};

public class Trabajador {
	
	String id;
	boolean [] calendario;
	tipoTrabajador tipo;
	public Auto trabajoActual;
	
	//Date now = new Date();
	
	
	
	public Trabajador (String id, char tipo)
	{
		this.id = id;
		
		if(tipo=='p')
			this.tipo = tipoTrabajador.pintor;
		if(tipo=='d')
			this.tipo = tipoTrabajador.desabollador;
		if(tipo=='m')
			this.tipo = tipoTrabajador.mecanico;
			
		
		//creo que por defecto el arreglo viene con falses
		calendario = new boolean [365*2*8];
		
	}
	

	
	//retorna true si el trabajador esta desocupado el dia i
	public boolean ocupado (int i)
	{
		return calendario[i];
	}

	
	//le asigna al trabajador el trabajo propio de 'auto', el dia 'dia'
	public void asignarTrabajo(Auto auto, int hora, etapa e) {

		//le llenamos el horario al trabajador
		trabajoActual=auto;
		//completar, se debe saber en que proceso esta...
		int tiempo=0;
		if(e==etapa.desabolladura) tiempo=auto.getTiempoDesabolladura();
		else if(e==etapa.pintura)tiempo=auto.getTiempoPintura();
		else if(e==etapa.armado)tiempo=auto.getTiempoArmado();
		else tiempo=auto.getTiempoPulido();
		for(int i = hora; i < hora+tiempo && hora+tiempo < 365*2*8; i++)
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

	public int proximoTiempoLibreDesde(int i) {
		// TODO Auto-generated method stub
		while(i<365*2*8)
		{	
			if(calendario[i]==false)
					return i;
			else
				i++;
		}
		return Integer.MAX_VALUE;
	}
	
	
}

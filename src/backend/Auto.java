package backend;

enum etapa{desabolladura, pintura, armado, pulido};

public class Auto {

	
	String OT;
	int fechaAutorizacion;
	String requiereMecanico;
	String tipoSiniestro;
	
	etapa etapa;
	
	int tiempoDesabolladura;
	int llegadaDesabolladura;
	int llegadaPintura;
	int llegadaArmado;
	int llegadaPulido;
	int salidaDesabolladura;
	int salidaPintura;
	int salidaArmado;
	int salidaPulido;
	
	
	public Auto (String OT, int fechaAutorizacion, String requiereMecanico, String tipoSiniestro){
		this.OT=OT;
		this.fechaAutorizacion=fechaAutorizacion;
		this.requiereMecanico=requiereMecanico;
		this.tipoSiniestro=tipoSiniestro;
		
		//asignarTiempoDesabolladura, segun promedio del excel
		if(tipoSiniestro.equals("G"))
			tiempoDesabolladura=7;
		if(tipoSiniestro.equals("G"))
			tiempoDesabolladura=3;
		if(tipoSiniestro.equals("G"))
			tiempoDesabolladura=2;
		
		 
		 llegadaDesabolladura=0;
		 llegadaPintura=0;
		 llegadaArmado=0;		 
		 salidaDesabolladura=0;
		 salidaPintura=0;
		 salidaArmado=0;
		 salidaPulido=0;
		 llegadaPulido=0;
	}
	
	public String imprimir()
	{
		return "Auto "+OT+": Fecha autorizacion: "+fechaAutorizacion+" Requiere Mecanico: "+requiereMecanico+" Tipo Siniestro: "+tipoSiniestro;
		
	}

	public int getFechaAutorizacion() {
		// TODO Auto-generated method stub
		return fechaAutorizacion;
	}
	
	public int getTiempoDesabolladura()
	{
		return tiempoDesabolladura;
	}
	
	public etapa getEtapa()
	{
		return etapa;
	}

}

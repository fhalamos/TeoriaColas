package backend;

enum etapa{desabolladura, pintura, armado, pulido};

public class Auto {

	
	
	String OT;
	int tiempoAutorizacion;
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
	
	
	public Auto (String OT, int tiempoAutorizacion, String requiereMecanico, String tipoSiniestro){
		this.OT=OT;
		this.tiempoAutorizacion=tiempoAutorizacion;
		this.requiereMecanico=requiereMecanico;
		this.tipoSiniestro=tipoSiniestro;
		
		//asignarTiempoDesabolladura, segun promedio del excel
		if(tipoSiniestro.equals("G"))
			tiempoDesabolladura=7*8;
		if(tipoSiniestro.equals("G"))
			tiempoDesabolladura=3*8;
		if(tipoSiniestro.equals("G"))
			tiempoDesabolladura=2*8;
		
		 
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
		return "Auto "+OT+": Fecha autorizacion: "+tiempoAutorizacion+" Requiere Mecanico: "+requiereMecanico+" Tipo Siniestro: "+tipoSiniestro;
		
	}

	public int getTiempoAutorizacion() {
		// TODO Auto-generated method stub
		return tiempoAutorizacion;
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

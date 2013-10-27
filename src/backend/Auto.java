package backend;

enum etapa{desabolladura, pintura, armado, pulido};

public class Auto {

	
	
	String OT;
	int tiempoAutorizacion;
	String requiereMecanico;
	String tipoSiniestro;
	
	etapa etapa_;
	
	int tiempoDesabolladura;
	int tiempoPintura;
	int tiempoArmado;
	int tiempoPulido;
	
	
	
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
		
		//asignarTiempos de trabajo, segun promedio del excel
		if(tipoSiniestro.equals("G"))
		{
			tiempoDesabolladura=7*8;
			tiempoPintura=6*8; //volver a fijar
			tiempoArmado=6*8; //volver a fijar
			tiempoPulido=6*8; //volver a fijar
		}
		if(tipoSiniestro.equals("M"))
		{	
			tiempoDesabolladura=3*8;
			tiempoPintura=6*8; //volver a fijar
			tiempoArmado=6*8; //volver a fijar
			tiempoPulido=6*8; //volver a fijar
		}
		
		if(tipoSiniestro.equals("L"))
		{
			tiempoDesabolladura=2*8;
			tiempoPintura=6*8; //volver a fijar
			tiempoArmado=6*8; //volver a fijar
			tiempoPulido=6*8; //volver a fijar
		}
		
		
		
		
		 
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
		return etapa_;
	}
	
	public String getOT()
	{
		return OT;
	}

	public void fijarTiemposTrabajo(etapa e, int i) {
		if(e==etapa.desabolladura)
		{
			etapa_=e;
			llegadaDesabolladura=i;
			salidaDesabolladura=i+tiempoDesabolladura;
		}
		
		if(e==etapa.pintura)
		{
			etapa_=e;
			llegadaPintura=i;
			salidaPintura=i+tiempoPintura;
		}
		
		if(e==etapa.armado)
		{
			etapa_=e;
			llegadaArmado=i;
			salidaArmado=i+tiempoArmado;
		}
		
		
		
		
	}

}

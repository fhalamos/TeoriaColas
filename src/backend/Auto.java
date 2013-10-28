package backend;

enum etapa{desabolladura, pintura, armado, pulido};

public class Auto {

	
	
	String OT;
	int tiempoAutorizacion;
	String requiereMecanico;
	String tipoSiniestro;
	int tiempoDeReparacionSegunModeloActual;
	
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
	
	
	public Auto (String OT, int tiempoAutorizacion, String requiereMecanico, String tipoSiniestro, 	int tiempoDeReparacionSegunModeloActual){
		this.OT=OT;
		this.tiempoAutorizacion=tiempoAutorizacion;
		this.requiereMecanico=requiereMecanico;
		this.tipoSiniestro=tipoSiniestro;
		this.tiempoDeReparacionSegunModeloActual= tiempoDeReparacionSegunModeloActual;
		
		//asignarTiempos de trabajo, segun promedio del excel
		if(tipoSiniestro.equals("G"))
		{
			tiempoDesabolladura=(int)(1.15*8);
			tiempoPintura=(int)(0.37*8); //volver a fijar
			tiempoArmado=(int)(0.51*8);; //volver a fijar
			tiempoPulido=(int)(0.21*8);; //volver a fijar
		}
		if(tipoSiniestro.equals("M"))
		{	
			tiempoDesabolladura=(int)(0.55*8);
			tiempoPintura=(int)(0.26*8);; //volver a fijar
			tiempoArmado=(int)(0.33*8); //volver a fijar
			tiempoPulido=(int)(0.18*8); //volver a fijar
		}
		
		if(tipoSiniestro.equals("L"))
		{
			tiempoDesabolladura=(int)(0.37*8);
			tiempoPintura=(int)(0.17*8); //volver a fijar
			tiempoArmado=(int)(0.2*8); //volver a fijar
			tiempoPulido=(int)(0.16*8); //volver a fijar
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
	
	public int getTiempoPintura()
	{
		return tiempoPintura;
	}
	
	public int getTiempoArmado()
	{
		return tiempoArmado;
	}
	
	public int getTiempoPulido()
	{
		return tiempoPulido;
	}
	
	public etapa getEtapa()
	{
		return etapa_;
	}
	
	public void setEtapa(etapa e)
	{
	 etapa_=e;	
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
		
		if(e==etapa.pulido)
		{
			etapa_=e;
			llegadaPulido=i;
			salidaPulido=i+tiempoPulido;
		}
		
		
	}

}

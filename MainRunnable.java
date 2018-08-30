package ar.edu.ubp.das.src.sorteo.main;

import ar.edu.ubp.das.src.sorteo.consumo.ClienteServicio;

public class MainRunnable implements Runnable {
	
	private String method;
	private String URL;
	
	public MainRunnable (String method, String url){
		
		this.method = method;
		this.URL = url;
	}

	public static void main(String []args) {
		
		Runnable test = new MainRunnable("clientes", "http://localhost:7071/ConcesionariaMaipuCXF/services?wsdl");
		Runnable test2 = new MainRunnable("clientes", "http://localhost:7071/ConcesionariaMaipuCXF/services?wsdl");
		
		new Thread(test).start();
		new Thread(test2).start();
		
	}

	@Override
	public void run() {
		try {
			ClienteServicio clienteServicio = ClienteServicio.class.cast(Class.forName(ClienteServicio.class.getPackage().getName() + ".ClienteCXF"));
			String json = clienteServicio.consumir(this.URL, this.method, "");			
			System.out.println("Respuesta: " + json);
		}
		catch (Exception e) {			
			System.out.println(e.getLocalizedMessage());
			System.out.println("# \t -- Algún error al actualizar los datos de la concesionaria" );
		}
		
	}
	
	
}

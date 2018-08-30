package ar.edu.ubp.das.src.maipu.ws;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ar.edu.ubp.das.mvc.action.DynaActionForm;
import ar.edu.ubp.das.mvc.db.DaoFactory;
import ar.edu.ubp.das.src.maipu.daos.MSClientesDao;

@WebService(targetNamespace = "http://ws.maipu.src.das.ubp.edu.ar/", portName = "ClientesWSPort", serviceName = "ClientesWSService")
public class ClientesWS {
	
	@WebMethod(operationName = "clientes", action = "urn:Clientes")
	public String clientes(@WebParam(name = "arg0") String solicitante, @WebParam(name = "arg1") String fechaSorteo) {
		List<DynaActionForm> data = new LinkedList<DynaActionForm>();
		try {
			MSClientesDao dao = (MSClientesDao)DaoFactory.getDao( "Clientes", "ar.edu.ubp.das.src.maipu" );
			
			DynaActionForm daf = new DynaActionForm();
			daf.setItem("solicitante", solicitante);
			
			data = dao.select(daf);
			
			LinkedList<Map<String,Object>> clientesForJson = new LinkedList<Map<String,Object>>();
			
			for( DynaActionForm d : data ) {
				
				Map<String,Object> cliente = new HashMap<String, Object>();
				Map<String,Object> cuota;
				LinkedList<Map<String,Object>> cuotas = new LinkedList<Map<String,Object>>();
				Map<String,Object> auto = new HashMap<String, Object>();
				
				cliente.put("nombre", d.getItem("nombre"));
				cliente.put("apellido", d.getItem("apellido"));
				cliente.put("dni", d.getItem("dni"));
				cliente.put("mail", d.getItem("mail"));
				cliente.put("provincia", d.getItem("provincia"));
				cliente.put("localidad", d.getItem("localidad"));
				cliente.put("direccion", d.getItem("direccion"));
							
				auto.put("marca", d.getItem("marca"));
				auto.put("modelo", d.getItem("modelo"));
				auto.put("version", d.getItem("version"));
				auto.put("dominio", d.getItem("dominio"));
				auto.put("numMotor", d.getItem("numMotor"));
				auto.put("numChasis", d.getItem("numChasis"));
				auto.put("color", d.getItem("color"));
				auto.put("anio", d.getItem("anio"));
				
				cliente.put("auto", auto);
				
				String firstDNI = "";
				for( DynaActionForm c : data ) {
					if(firstDNI.equals(c.getItem("dni")) || firstDNI.equals("")) {
						if(firstDNI.equals("")) {
							firstDNI = c.getItem("dni");
						}
						cuota = new HashMap<String, Object>();					
						cuota.put("idCuota", c.getItem("idCuota"));
						cuota.put("fechaVencimiento", c.getItem("fechaVencimiento"));
						cuota.put("fechaPago", c.getItem("fechaPago"));
						
						cuotas.add(cuota);						
					}
					else {						
						cliente.put("cuotas", cuotas);
						String alDia = "s";
						for(Map<String,Object> cta : cuotas) {
							if(cta.get("fechaPago").equals("null")) {
								
								Date fechaHoy = new Date();
								
							    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); // 2016-07-10

					            Date fechaVencimiento = null;
					            
								try {
									fechaVencimiento = formatter.parse((String) cta.get("fechaVencimiento"));
									
									if( fechaVencimiento.before(fechaHoy)) {
										
										alDia = "n";
									}
								}
								catch (ParseException e) {
									
									e.printStackTrace();
								}
								
								break;
							}
						}
						cliente.put("al_dia", alDia);
						cuotas =  new LinkedList<Map<String,Object>>();						
					}
				}				
				
				clientesForJson.add(cliente);
			}
			
			Gson gson = new GsonBuilder().create();
			return gson.toJson(clientesForJson);
			
		} catch (SQLException e) {

			return "Error al obtener datos";
		}
	}
}

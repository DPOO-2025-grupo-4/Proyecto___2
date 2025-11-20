package repositorios;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import Eventos.Evento;
import Usuarios.Administrador;
import Usuarios.Cliente;
import Usuarios.Promotor;
import Usuarios.Usuario;
import tiquetes.PaqueteDeluxe;
import tiquetes.Tiquete;
import tiquetes.TiqueteIndividual;
import tiquetes.TiqueteMultiple;
import util.RuntimeTypeAdapterFactory;

public class Propuestas {


	public TreeMap<Integer, Promotor> promotoresPropuestos;
	public TreeMap<Integer, Evento> eventoPropuestasCancelacion;
	//public TreeMap<String, Tiquete> reembolsoPropuesta;
	

	private static final String ARCHIVO = "Archivos_Persistidos/ARCHIVO_PROPUESTAS.json";
	
	public Propuestas(){
		promotoresPropuestos = new TreeMap<Integer, Promotor>();
		eventoPropuestasCancelacion = new TreeMap<Integer, Evento>();
	}

	public void guardar() {
	    try (Writer writer = new FileWriter(ARCHIVO)) {

	        RuntimeTypeAdapterFactory<Tiquete> tiqueteAdapter = RuntimeTypeAdapterFactory
	                .of(Tiquete.class, "type")
	                .registerSubtype(TiqueteIndividual.class, "individual")
	                .registerSubtype(TiqueteMultiple.class, "multiple")
	                .registerSubtype(PaqueteDeluxe.class, "paqueteDeluxe");

	        Gson gson = new GsonBuilder()
	                .registerTypeAdapterFactory(tiqueteAdapter)
	                .setPrettyPrinting()
	                .create();

	        gson.toJson(this, writer);
	        System.out.println("Eventos guardados correctamente.");
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

	public static Propuestas cargar() {
	    try (Reader reader = new FileReader(ARCHIVO)) {

	        RuntimeTypeAdapterFactory<Tiquete> tiqueteAdapter =
	            RuntimeTypeAdapterFactory
	                .of(Tiquete.class, "type")
	                .registerSubtype(TiqueteIndividual.class, "individual")
	                .registerSubtype(TiqueteMultiple.class, "multiple")
	                .registerSubtype(PaqueteDeluxe.class, "paqueteDeluxe");

	        Gson gson = new GsonBuilder()
	                .registerTypeAdapterFactory(tiqueteAdapter)
	                .create();

	        Propuestas repo = gson.fromJson(reader, Propuestas.class);
	        return repo != null ? repo : new Propuestas();
	    } catch (FileNotFoundException e) {
	        System.out.println("Archivo eventos.json no encontrado. Se creará uno nuevo.");
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return new Propuestas();
	}
    
    public void agregarPropuestapromotor(Promotor promotor) {
    	promotoresPropuestos.put(promotor.getId(), promotor);
    }
    public void pedirCancelacionEvento(Evento evento) {
    	System.out.println(evento.getIdEvento());
    	
    	eventoPropuestasCancelacion.put(evento.getIdEvento(), evento);
    }
    
    public void eliminarPropuestaPromotor(Integer promotor) {
    	promotoresPropuestos.remove(promotor);
    }
    public void aceptarCancelacionEvento(Integer evento) {
    	eventoPropuestasCancelacion.remove(evento);
    }
    public List<Promotor> getPromotoresPropuestos(){
    	List<Promotor> lista = new ArrayList<Promotor>(promotoresPropuestos.values());
    	return lista;
    }
    public List<Evento> getCancelacionesPropuestas(){
    	List<Evento> lista = new ArrayList<Evento>(eventoPropuestasCancelacion.values());
    	return lista;
    }
    public Promotor getPromotorPropuesto(Integer prom) {
    	return promotoresPropuestos.get(prom);
    	
    }
    public Evento getEventoPropuesto(Integer evento) {
    	return eventoPropuestasCancelacion.get(evento);
    	
    }
    
    
    
    
}

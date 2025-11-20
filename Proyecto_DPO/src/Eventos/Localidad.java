package Eventos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import tiquetes.Tiquete;
import tiquetes.TiqueteIndividual;

public class Localidad {
	private int idLocalidad = 0;
	private double precio;
	private String caracteristicas;
	private boolean esNumerada;
	private boolean disponible;
	private int vendidos = 0;
	private Map<String, Tiquete> disponibles;
	private Map<String, Tiquete> ocupados;
	private transient Evento eventoAsociado;
	private int numAsiento = 1;
	private char fila = 'A';
	private int capacidad;
	private Oferta oferta = null;
	
	public Localidad() {}
	public Localidad(Evento evento, double precio, String caracteristicas, boolean esNumerada, int capacidad, int asientoXFila) 
	{
		this.precio = precio;
		this.caracteristicas = caracteristicas;
		disponibles = new TreeMap<String, Tiquete>();
		ocupados = new TreeMap<String, Tiquete>();
		this.esNumerada = esNumerada;
		disponible = true;
		this.eventoAsociado = evento;
		this.capacidad = capacidad;
		if (esNumerada == true) {
			agregarTiquetes(asientoXFila);
		}
		else {
			agregarTiquetes();
		}
		
	}
	public void asociarOferta(Oferta oferta) {
		this.oferta = oferta;
		List<Tiquete> lista3 = getTiquetesDisponibles();
		for(Tiquete t: lista3) {
			t.setDescuento(oferta.getPorcentajeDescuento());
			t.setPrecioTotal(precio);
		}
	}
	public void reservar(Tiquete tiquete) {
		String id = tiquete.getId();
		if(esNumerada == true) {
			disponibles.remove(id);
			ocupados.put(id, tiquete);
			vendidos ++;
		}
		else {
		vendidos ++;
		if (vendidos == capacidad) {
			disponible = false;
		}
		}
	}
	public void liberar(Tiquete tiquete) {
		String id = tiquete.getId();
		if(esNumerada == true) {
			ocupados.remove(id);
			disponibles.put(id, tiquete);
			vendidos --;
		}
		else {
		vendidos --;
		}
	}
	public int contarDisponibilidad() {
		if (capacidad == vendidos) {
			return 0;
		}
		else {
			return capacidad - vendidos;
		}
	}
	public boolean tieneOferta() {
		if (oferta != null) {
			return true;
		}
		else {
			return false;
		}
	}
	public boolean getEsNumerada() {
		return esNumerada;
	}
	public void agregarTiquetes(){
		
		for(int i = 0; i < capacidad; i++) {
			TiqueteIndividual nuevo = new TiqueteIndividual(eventoAsociado, this);
			disponibles.put(nuevo.getId(), nuevo);
		}
	}
	public int getVendidos() {
		return vendidos;
	}
	public void agregarTiquetes(int asientoXFila) {//segun el asiento x fila generar todos los asientos hasta llegar a una capacidad maxima y añadirlos todos en asientos disponibles
		for(int i = 0; i < capacidad; i++) {
			TiqueteIndividual nuevo = new TiqueteIndividual(eventoAsociado, this);
			nuevo.setAsiento(""+ fila + numAsiento);
			disponibles.put(nuevo.getId(), nuevo);
			numAsiento++;
			if(numAsiento == asientoXFila) {
				fila++;
				numAsiento =1;
			}

		}
	}
	public List<Tiquete> getTiquetesDisponibles(){
		List <Tiquete> rst = new ArrayList<Tiquete>(disponibles.values());
		return rst;
	}
	public Tiquete getTiqueteDisponible(String id) {
		return disponibles.get(id);
	}
	public void eliminarTiquete(String id) {
		disponibles.remove(id);
	}
	public void agregarTiquete(Tiquete tiquete) {
		disponibles.put(tiquete.getId(), tiquete);
	}
	public boolean getDisponible() {
		return disponible;
	}
	public int getIdLocalidad() {
		return idLocalidad;
	}
	public double getPrecio() {
		return precio;
	}
	public void setPrecio(double precio) {
		this.precio = precio;
	}
	public String getCaracteristicas() {
		return caracteristicas;
	}
	public void setCaracteristicas(String caracteristicas) {
		this.caracteristicas = caracteristicas;
	}
	public boolean isDisponible() {
		return disponible;
	}
	public void setDisponible(boolean disponible) {
		this.disponible = disponible;
	}
	public Evento getEventoAsociado() {
		return eventoAsociado;
	}
	public void setEventoAsociado(Evento eventoAsociado) {
		this.eventoAsociado = eventoAsociado;
	}
	public int getCapacidad() {
		return capacidad;
	}
	public void setCapacidad(int capacidad) {
		this.capacidad = capacidad;
	}
	public Oferta getOferta() {
		return oferta;
	}
	public void setOferta(Oferta oferta) {
		this.oferta = oferta;
	}
	public void setIdLocalidad(int idLocalidad){
		this.idLocalidad = idLocalidad;
	}


}

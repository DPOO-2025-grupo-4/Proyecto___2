package Sistema;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import Eventos.Evento;
import Eventos.Localidad;
import Eventos.Oferta;
import Eventos.Venue;
import Usuarios.Promotor;
import repositorios.GestorID;
import repositorios.Propuestas;
import repositorios.RepositorioEventos;
import repositorios.RepositorioVenues;
import tiquetes.PaqueteDeluxe;
import tiquetes.Tiquete;
import tiquetes.TiqueteIndividual;

public class SistemaPromotor extends SubSistema {

    private Scanner sc = new Scanner(System.in);
    private RepositorioEventos repe;
    private RepositorioVenues repv;
    private GestorID ids;
    private Propuestas repp;

    public SistemaPromotor(Promotor promotor, RepositorioEventos repe, RepositorioVenues repv, GestorID ids, Propuestas repp) {
        super(promotor);
        this.repe = repe;
        this.repv = repv;
        this.ids = ids;
        this.repp = repp;
    }

    @Override
    public void mostrarMenu() {
        int opcion = -1;
        do {
            System.out.println("\n=== MENÚ PROMOTOR ===");
            System.out.println("1. Crear evento");
            //System.out.println("2. Consultar ganancias");
            System.out.println("3. Aplicar oferta a localidad");
            System.out.println("4- Crear paquete deluxe");
            System.out.println("5- Comprar tiquete");
            System.out.println("6- Proponer Venue");
            System.out.println("7- Propone Cancelacion de evento");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");
            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1 : crearEvento(); //Probado
                break;
                case 2 : consultarGanancias();
                break;
                case 3 : aplicarOferta(); //probado
                break;
                case 4 : crearPaqueteDeluxe(); //Probado
                break;
                case 5 : comprarTiquete(); //Probado
                break;
                case 6: proponerVenue(); //Probado
                break;
                case 7: proponerCancelacion(); //probado
                break;
                case 0 : System.out.println("Saliendo del sistema de promotor...");
                salir();
                break;
                default : System.out.println("Opción inválida.");
            }

        } while (opcion != 0);
    }

    private void proponerCancelacion() {
    	boolean datosCorrectos = false;
    	do {
    	List<Evento> lista = repe.getEventosXPromotor(usuario.getLogin());
		for (Evento e : lista) {
			System.out.println("ID: " + e.getIdEvento()
			+ " | Tipo: " + e.getTipoEvento()
			+ " | Fecha: " + e.getFechaEvento()
			+ " | Capacidad: " + e.getCapacidadEvento()
			+ " | Venue Asociado" + e.getVenueAsociado().getNombreVenue());
		}
		System.out.print("Seleccione el evento, posteriormente seleccione la localidad en la que desea el paquete Deluxe");
		int idEvento = sc.nextInt();
		sc.nextLine();
		Evento evento = repe.getEventoActivo(idEvento);
		if(evento != null) {
			repp.pedirCancelacionEvento(evento);
			datosCorrectos = true;
		}
		}while(datosCorrectos == false);
    }
    private void proponerVenue() {
    	boolean datosCorrectos = false;
    	do {
    		System.out.println("Ingrese el tipo de venue: ");
    		String tipo = sc.nextLine();
    		System.out.println("Ingrese la latitud del venue: ");
    		int latitud = sc.nextInt();
    		sc.nextLine();
    		System.out.println("Ingrese la longitud del venue: ");
    		int longitud = sc.nextInt();
    		sc.nextLine();
    		System.out.println("Ingrese la capacidad maxima del venue: ");
    		int capacidad = sc.nextInt();
    		sc.nextLine();
    		System.out.println("Ingrese las restricciones del venue: ");
    		String restricciones = sc.nextLine();
    		System.out.println("Ingrese el nombre del venue: ");
    		String nombre = sc.nextLine();
    		try {
    			
    			Venue nuevo = new Venue(tipo, latitud, longitud, capacidad, restricciones, nombre, "INACTIVO");
    			datosCorrectos = true;
    			repv.agregarVenue(nuevo);
    		} catch (Exception e) {
    			System.out.println("La capacidad del venue se dio negativa, vuelva a intentar");
    		}
    	}while(datosCorrectos == false);	
	}

	private void comprarTiquete() {
		boolean datosCorrectos = false;
		do {
			List<Evento> lista = repe.getEventosActivos();
    		for (Evento e : lista) {
    			System.out.println("ID: " + e.getIdEvento()
    			+ " | Tipo: " + e.getTipoEvento()
    			+ " | Fecha: " + e.getFechaEvento()
    			+ " | Capacidad: " + e.getCapacidadEvento()
    			+ " | Venue Asociado" + e.getVenueAsociado().getNombreVenue());
    		}
    		System.out.print("Seleccione el evento, posteriormente seleccione la localidad en la que desea el paquete Deluxe");
    		int idEvento = sc.nextInt();
    		sc.nextLine();
    		Evento evento = repe.getEventoActivo(idEvento);
    		List<Localidad> lista2 = evento.getLocalidadesDisponibles();
    		for(Localidad l: lista2) {
    			System.out.println("ID: " + l.getIdLocalidad()
    			+ " | Caracteristicas: " + l.getCaracteristicas()
    			+ " | Capacidad: " + l.getCapacidad()
    			+ " | Precio: " + l.getPrecio()
    			+ " | Disponible:" + l.getDisponible());
    		}
    		System.out.print("Digite el ID de la localidad en la que crear el paquete Deluxe");
    		int idLocalidad = sc.nextInt();
    		sc.nextLine();
    		Localidad localidad = evento.getLocalidadPorID(idLocalidad);
    		if(localidad.getEsNumerada() == false) {
    			List<Tiquete> lista3 = localidad.getTiquetesDisponibles();    	
    			((Promotor) usuario).agregarTiquete(lista3.get(0));
    			localidad.reservar(lista3.get(0));
    			datosCorrectos = true;
    			
    		}
    		else {
    			List<Tiquete> lista3 = localidad.getTiquetesDisponibles();
    			for(Tiquete t: lista3) {
    	    			System.out.println("ID: " + t.getId()
    	    			+ " | Precio Tiquete: " + t.getPrecioTotal()
    	    			+ " | Asiento: " + t.getAsiento());
    	    		}
    			System.out.println("Ingrese el id del tiquete a comprar");
    			String tiquete = sc.nextLine();
    			((Promotor) usuario).agregarTiquete(localidad.getTiqueteDisponible(tiquete));
    			localidad.reservar(lista3.get(0));
    			datosCorrectos = true;
    		}
		}while (datosCorrectos == false);
		
	}

	private void crearPaqueteDeluxe() {
	    boolean datosCorrectos = false;

	    do {
	        List<Evento> lista = repe.getEventosXPromotor(usuario.getLogin());
	        for (Evento e : lista) {
	            System.out.println("ID: " + e.getIdEvento()
	                    + " | Tipo: " + e.getTipoEvento()
	                    + " | Fecha: " + e.getFechaEvento()
	                    + " | Capacidad: " + e.getCapacidadEvento()
	                    + " | Venue Asociado: " + e.getVenueAsociado().getNombreVenue());
	        }

	        System.out.print("Seleccione el ID del evento: ");
	        int idEvento = sc.nextInt();
	        sc.nextLine();

	        Evento evento = repe.getEventoActivo(idEvento);
	        if (evento == null) {
	            System.out.println("Evento inválido.");
	            continue;
	        }

	        List<Localidad> lista2 = evento.getLocalidadesDisponibles();
	        for (Localidad l : lista2) {
	            System.out.println("ID: " + l.getIdLocalidad()
	                    + " | Características: " + l.getCaracteristicas()
	                    + " | Capacidad: " + l.getCapacidad()
	                    + " | Precio: " + l.getPrecio()
	                    + " | Vendidos: " + l.getVendidos());
	        }

	        System.out.print("Digite el ID de la localidad en la que crear el paquete Deluxe: ");
	        int idLocalidad = sc.nextInt();
	        sc.nextLine();

	        Localidad localidad = evento.getLocalidadPorID(idLocalidad);
	        if (localidad == null) {
	            System.out.println("Localidad inválida.");
	            continue;
	        }

	        // 3. Mostrar tiquetes disponibles en la localidad
	        System.out.println("Se pedirán 10 IDs de tiquete para el paquete.");
	        List<Tiquete> lista3 = localidad.getTiquetesDisponibles();

	        if (lista3.isEmpty()) {
	            System.out.println("No hay tiquetes disponibles en esta localidad.");
	            continue;
	        }

	        for (Tiquete t : lista3) {
	            System.out.println("ID: " + t.getId()
	                    + " | Precio Tiquete: " + t.getPrecioTotal());
	        }

	        List<TiqueteIndividual> listaTiquetes = new ArrayList<>();
	        int i = 0;
	        while (i < 10) {
	            System.out.print("Ingrese un ID de tiquete: ");
	            String id = sc.nextLine();

	            // (Sí, esto debería estar en otra capa, pero por tiempo lo dejamos aquí)
	            Tiquete tiquete = localidad.getTiqueteDisponible(id);

	            if (tiquete instanceof TiqueteIndividual
	                    && !listaTiquetes.contains(tiquete)) {

	                listaTiquetes.add((TiqueteIndividual) tiquete);
	                localidad.eliminarTiquete(id);  // lo sacamos de la lista de disponibles
	                i++;

	            } else {
	                System.out.println("Tiquete no existe, ya se añadió o no es individual. Intente nuevamente.");
	            }
	        }

	        // 5. Datos del paquete
	        System.out.print("Ingrese el precio base del paquete: ");
	        double precio = sc.nextDouble(); // por ahora no se usa en el constructor
	        sc.nextLine();

	        System.out.println("Ingrese los beneficios añadidos de este paquete:");
	        String beneficios = sc.nextLine();

	        // 6. Crear PaqueteDeluxe (por ahora sin comprador: null)
	        PaqueteDeluxe tiqueteM =
	                new PaqueteDeluxe(evento, listaTiquetes, null, beneficios);

	        // Guardar el paquete como un tiquete disponible en la localidad
	        localidad.agregarTiquete(tiqueteM);

	        if (localidad.getTiqueteDisponible(tiqueteM.getId()) != null) {
	            System.out.println("Paquete Deluxe creado correctamente.");
	            datosCorrectos = true;
	        } else {
	            System.out.println("No se generó correctamente el Paquete Deluxe.");
	        }

	    } while (!datosCorrectos);
	}

	private void crearEvento() {
		boolean datosCorrectos = false;
		do {
		System.out.println("Seleccione la capacidad total del evento: ");
		int capacidad = sc.nextInt();
		sc.nextLine();
		System.out.println("Digite el tipo de Evento: ");
		String tipo = sc.nextLine();
		System.out.println("Digite la fecha del evento: (de la forma AAAA/MM/DD");
		String fecha = sc.nextLine();
		if (!fecha.matches("\\d{4}/\\d{2}/\\d{2}")) {
		    System.out.println("Formato de fecha inválido. Use AAAA/MM/DD.");
		    continue;
		}
		List<Venue> lista = repv.getVenuesActivos();
		for (Venue v : lista) {
            System.out.println("ID: " + v.getIdVenue()
                    + " | Tipo: " + v.getTipoVenue()
                    + " | Capacidad: " + v.getCapacidadMaxima()
                    + " | Restricciones: " + v.getRestriccionesUso()
                    + " | Nombre" + v.getNombreVenue()
                    + " | Ubicacion (lat, long)" + v.getUbicacion());
        }
		System.out.println("Digite el numero id del venue en el que desea hacer el evento, si la capacidad del venue es menor a la del evento o este no esta totalmente disponible, debe escoger otro");
		int idVenue = sc.nextInt();
		sc.nextLine();
		Venue venue = repv.getVenueActivo(idVenue);
		try {
			Evento eventoN = new Evento(capacidad, tipo, fecha, venue, (Promotor) usuario);
			System.out.println("Ingrese la cantidad de localidades que desea en el evento: ");
			int cantidad = sc.nextInt();
			sc.nextLine();
			crearLocalidad(eventoN, cantidad);
			repe.agregarEvento(eventoN);
			System.out.println("→ Evento creado correctamente.");
			datosCorrectos = true;
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
		}while(datosCorrectos == false);
        
    }
	private void crearLocalidad(Evento evento, int cantidad) throws Exception{
		boolean esNumerada = false;
		int asientoXFila = 0;
		for (int i = 0; i< cantidad; i++) {
			System.out.println("Ingrese el precio de la localidad: ");
			double precio = sc.nextDouble();
			sc.nextLine();
			System.out.println("Ingrese las caracteristicas de la localidad: ");
			String caracter = sc.nextLine();
			System.out.println("¿La localidad tiene asientos numerados?(0-Si)(1-No)");
			int opcion = sc.nextInt();
			sc.nextLine();
			switch(opcion) {
				case 0: esNumerada = true;
				break;
				case 1: esNumerada = false;
				break;
				default: System.out.println("Opcion incorrecta");
			}
			System.out.println("Ingrese la capacidad de la localidad, procure que entre todas las localidades no sobrepasen la capacidad del evento: ");
			int capacidad = sc.nextInt();
			sc.nextLine();
			if(esNumerada == true) {
				System.out.print("Digite la cantidad de asientos por fila");
				asientoXFila = sc.nextInt();
				sc.nextLine();
			}
			Localidad localidadN = new Localidad(evento, precio, caracter, esNumerada,capacidad, asientoXFila);
			try {
				evento.agregarLocalidad(localidadN);
			} catch (Exception e) {
				throw e;
			}
		}
	}

    private void consultarGanancias() {
        System.out.println("→ Mostrando ganancias de tus eventos...");
    }

    private void aplicarOferta() {
    	boolean datosCorrectos = false;
    	do {
    		List<Evento> lista = repe.getEventosXPromotor(usuario.getLogin());
    		for (Evento e : lista) {
    			System.out.println("ID: " + e.getIdEvento()
    			+ " | Tipo: " + e.getTipoEvento()
    			+ " | Fecha: " + e.getFechaEvento()
    			+ " | Capacidad: " + e.getCapacidadEvento()
    			+ " | Venue Asociado" + e.getVenueAsociado().getNombreVenue());
    		}
    		System.out.print("Seleccione el evento, posteriormente seleccione la localidad a la que le desea aplicar la oferta");
    		int idEvento = sc.nextInt();
    		sc.nextLine();
    		Evento evento = repe.getEventoActivo(idEvento);
    		List<Localidad> lista2 = evento.getLocalidadesDisponibles();
    		for(Localidad l: lista2) {
    			System.out.println("ID: " + l.getIdLocalidad()
    			+ " | Caracteristicas: " + l.getCaracteristicas()
    			+ " | Capacidad: " + l.getCapacidad()
    			+ " | Precio: " + l.getPrecio()
    			+ " | Cantidad de tiquetes vendidos" + l.getVendidos());
    		}
    		System.out.print("Digite el ID de la localidad a la que aplicar la oferta");
    		int idLocalidad = sc.nextInt();
    		sc.nextLine();
    		Localidad localidad = evento.getLocalidadPorID(idLocalidad);
    		System.out.print("Digite la fecha inicio de la Oferta");
    		String fechaInicio = sc.nextLine();
    		System.out.print("Digite la fecha fin de la Oferta");
    		String fechaFin = sc.nextLine();
    		System.out.print("Ingrese el porcentaje de descuento que desea aplicar");
    		int porcentaje = sc.nextInt();
    		sc.nextLine();
    		if (fechaInicio.matches("\\d{4}/\\d{2}/\\d{2}") && fechaFin.matches("\\d{4}/\\d{2}/\\d{2}")) {
    			datosCorrectos = true;
    			Oferta ofer = new Oferta(fechaInicio, fechaFin, porcentaje, localidad);
    			localidad.asociarOferta(ofer);
    		}
    		else {
    			System.out.println("Datos incorrectos, intente nuevamente");
    		}
    	}while(datosCorrectos == false);
    }

	public void salir() {
		repv.guardar();
		repe.guardar();
		ids.finalizarIDs(); //Chambonada para mantener id persistido
		ids.guardar();
		repp.guardar();
		
	}
}
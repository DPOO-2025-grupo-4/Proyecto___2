package Sistema;

import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import Eventos.Evento;
import Eventos.Venue;
import tiquetes.TiqueteIndividual;
import Usuarios.Administrador;
import Usuarios.Promotor;
import Usuarios.Usuario;
import repositorios.GestorID;
import repositorios.OfertaReventa;
import repositorios.Propuestas;
import repositorios.RepositorioEventos;
import repositorios.RepositorioUsuarios;
import repositorios.RepositorioVenues;

public class SistemaAdministrador extends SubSistema {

    private Scanner sc = new Scanner(System.in);
    private RepositorioUsuarios repu;
    private RepositorioEventos repe;
    private RepositorioVenues repv;
    private Propuestas repp;
    private GestorID ids;
    public SistemaAdministrador(Administrador admin, RepositorioUsuarios repu, RepositorioVenues repv, RepositorioEventos repe, Propuestas repp, GestorID ids) {
        super(admin);
        this.repu = repu;
        this.repe = repe;
        this.repv = repv;
        this.repp = repp;
        this.ids = ids;
    }

    @Override
    public void mostrarMenu() {
        int opcion;
        do {
            System.out.println("MENÚ ADMINISTRADOR");
            System.out.println("1. Cancelar evento");
            System.out.println("2. Ver finanzas tiquetera");
            System.out.println("3. Autorizar reembolso");
            System.out.println("4. Aprobar Venue");
            System.out.println("5. Bloquear Promotor");
            System.out.println("6. Añadir Promotor");
            System.out.println("7. Aceptar Promotor");
            System.out.println("8. Aprobar cancelacion evento");
            System.out.println("9. Crear Venue");
            System.out.println("10. Rechazar Promotor");
            System.out.println("11. Ver log del sistema");
            System.out.println("12. Eliminar oferta de reventa");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");
            
            opcion = sc.nextInt();
            sc.nextLine(); 

            switch (opcion) {
                case 1 : cancelarEvento(); //SemiProbado
                break;
                case 2 :verFinanzas();
                break;
                //case 3: aprobarReembolso();
                //break;
                case 4 : aprobarVenue(); //Probado
                break;
                case 5 : bloquearPromotor(); //Probado
                break;
                case 6 : añadirPromotor(); //Probado
                break;
                case 7 : aceptarPromotor();//Probado
                break;
                case 8 :aprobarCancelacionEvento(); //probado
                break;
                case 9: crearVenue(); //Probado
                break;
                case 10: rechazarPromotor(); //Probado	
                break;
                case 11:
                    verLogSistema();
                    break;

                case 12:
                    eliminarOfertaReventa();
                    break;

                case 0 : System.out.println("Saliendo del sistema de administración...");
                salir();
                break;
                default : System.out.println("Opción inválida.");
            }

        } while (opcion != 0);
    }


    private void aprobarCancelacionEvento() {
		List<Evento> lista = repp.getCancelacionesPropuestas();
		for (Evento e : lista) {
            System.out.println("ID: " + e.getIdEvento()
                    + " | Tipo: " + e.getTipoEvento()
                    + " | Fecha: " + e.getFechaEvento()
                    + " | Capacidad: " + e.getCapacidadEvento()
                    + " | Venue Asociado" + e.getVenueAsociado().getNombreVenue());
        }
		System.out.println("Ingresa el id del evento a cancelar: ");
		int id = sc.nextInt();
		Evento evento = repp.getEventoPropuesto(id);
		repp.aceptarCancelacionEvento(id);
		repe.cambiarEstadoEvento(evento, "CANCELADO");
	}

	private void aceptarPromotor() {
		List<Promotor> lista = repp.getPromotoresPropuestos();
		for (Promotor p : lista) {
            System.out.println("ID: " + p.getId()
                    + " | Login: " + p.getLogin()
                    + " | Fecha Registro: " + p.getFechaRegistro()
                    + " | NIT: " + p.getNit()
                    + " | Nombre: " + p.getNombre());
        }
		System.out.println("Ingresa el id del promotor para aceptar: ");
		int id = sc.nextInt();
		sc.nextLine();
		Promotor prom = repp.getPromotorPropuesto(id);
		repu.agregarUsuario(prom);
		repp.eliminarPropuestaPromotor(id);
		
	}
	private void rechazarPromotor() {
		List<Promotor> lista = repp.getPromotoresPropuestos();
		for (Promotor p : lista) {
            System.out.println("ID: " + p.getId()
                    + " | Login: " + p.getLogin()
                    + " | Fecha Registro: " + p.getFechaRegistro()
                    + " | NIT: " + p.getNit()
                    + " | Nombre: " + p.getNombre());
        }
		System.out.println("Ingresa el id del promotor para rechazar: ");
		int id = sc.nextInt();
		sc.nextLine();
		repp.eliminarPropuestaPromotor(id);
	}

	private void añadirPromotor() {
		System.out.println("Ingrese el nombre");
		String nombre = sc.nextLine();
		System.out.println("Ingrese el Email");
		String mail = sc.nextLine();
		System.out.println("Ingrese el usuario login");
		String login = sc.nextLine();
		System.out.println("Ingrese la contraseña");
		String password = sc.nextLine();
		System.out.println("Ingrese la reputacion conocida del promotor");
		int reputacion = sc.nextInt();
		sc.nextLine();
		System.out.println("Ingrese el NIT");
		String NIT = sc.nextLine();
		Usuario nuevoProm = new Promotor(nombre, mail, login, password, reputacion, NIT);
		repu.agregarUsuario(nuevoProm);
	}

	private void bloquearPromotor() {
		List<Promotor> lista = repu.getPromotores();
		for (Promotor p : lista) {
            System.out.println("ID: " + p.getId()
                    + " | Login: " + p.getLogin()
                    + " | Fecha Registro: " + p.getFechaRegistro()
                    + " | NIT: " + p.getNit()
                    + " | Nombre: " + p.getNombre());
        }
		System.out.println("Ingrese el login del promotor a bloquear: ");
		String login = sc.nextLine();
		repu.eliminarUsuario(login, "PROMOTOR");
		System.out.println("Promotor eliminado correctamente");
	}

	private void cancelarEvento() {
		List<Evento> lista = repe.getEventosActivos();
		for (Evento e : lista) {
            System.out.println("ID: " + e.getIdEvento()
                    + " | Tipo: " + e.getTipoEvento()
                    + " | Fecha: " + e.getFechaEvento()
                    + " | Capacidad: " + e.getCapacidadEvento());
                    //+ " | Venue Asociado" + e.getVenueAsociado().getNombreVenue());
        }
		System.out.println("Ingrese el id del evento a cancelar");
		int id = sc.nextInt();
		sc.nextLine();
		repe.getEventoActivo(id).getVenueAsociado().liberarVenue();
        repe.cambiarEstadoEvento(repe.getEventoActivo(id), "CANCELADO");
        
    }

    private void verFinanzas() {
    	//De momento no hay finanzas
    }
    private void aprobarVenue() {
    	List<Venue> lista = repv.getVenuesInactivos();
		for (Venue v : lista) {
            System.out.println("ID: " + v.getIdVenue()
                    + " | Tipo: " + v.getTipoVenue()
                    + " | Capacidad: " + v.getCapacidadMaxima()
                    + " | Restricciones: " + v.getRestriccionesUso()
                    + " | Nombre" + v.getNombreVenue()
                    + " | Ubicacion (lat, long)" + v.getUbicacion());
        }
    	System.out.println("Ingrese el id del venue a aprobar");
		int id = sc.nextInt();
		sc.nextLine();
		repv.aprobarVenue(repv.getVenueInactivo(id));
    }
    public void crearVenue() {
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
    			
    			Venue nuevo = new Venue(tipo, latitud, longitud, capacidad, restricciones, nombre, "ACTIVO");
    			datosCorrectos = true;
    			repv.agregarVenue(nuevo);
    		} catch (Exception e) {
    			System.out.println("La capacidad del venue se dio negativa, vuelva a intentar");
    		}
    	}while(datosCorrectos == false);	
    }
    private void verLogSistema() {
        System.out.println("\n=== LOG DEL SISTEMA ===");
        Administrador admin = (Administrador) usuario;

        admin.consultarLog(); 
    }
    private void eliminarOfertaReventa() {
        System.out.println("\n=== ELIMINAR OFERTA DEL MARKETPLACE ===");

        Administrador admin = (Administrador) usuario;

        // Listar ofertas activas
        List<OfertaReventa> activas = repoOfertas.obtenerActivas();
        if (activas.isEmpty()) {
            System.out.println("No hay ofertas activas en este momento.");
            return;
        }

        for (int i = 0; i < activas.size(); i++) {
            OfertaReventa o = activas.get(i);
            System.out.println((i + 1) + ". ID: " + o.getId()
                    + " | Tiquete: " + o.getTiquete().getId()
                    + " | Precio: " + o.getPrecio()
                    + " | Vendedor: " + o.getVendedor().getLogin());
        }

        System.out.print("Seleccione la oferta a eliminar: ");
        int opcion = sc.nextInt();
        sc.nextLine();

        if (opcion < 1 || opcion > activas.size()) {
            System.out.println("Opción inválida.");
            return;
        }

        OfertaReventa oferta = activas.get(opcion - 1);

        try {
            admin.eliminarOfertaReventa(oferta, repoOfertas);
            System.out.println("Oferta eliminada correctamente.");
        } catch (Exception e) {
            System.out.println("Error al eliminar: " + e.getMessage());
        }
    }


	@Override
	public void salir() {
		repu.guardar();
		repv.guardar();
		repe.guardar();
		repp.guardar();
		ids.finalizarIDs(); //Chambonada para mantener id persistido
		ids.guardar();
	}

    /*public void autorizarReembolso(Tiquete_individual tiquete, double monto) {
        Objects.requireNonNull(tiquete, "El tiquete no puede ser nulo");
        if (monto <= 0) throw new IllegalArgumentException("El monto debe ser mayor a 0");

        tiquete.autorizarReembolso(monto);

        Usuario dueno = tiquete.getDuenoActual();
        if (dueno != null) {
            dueno.acreditarSaldo(monto);
            System.out.println("💰 Reembolso autorizado y acreditado a " + dueno.getNombre());
        } else {
            System.out.println("⚠️ El tiquete no tiene un dueño actual. No se acreditó saldo.");
        }
    }*/
}
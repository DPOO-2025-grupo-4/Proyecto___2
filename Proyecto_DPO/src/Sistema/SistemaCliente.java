package Sistema;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import Eventos.Evento;
import Eventos.Localidad;
import Usuarios.Cliente;
import Usuarios.Promotor;
import Usuarios.Usuario;
import repositorios.GestorID;
import repositorios.OfertaReventa;
import repositorios.RepositorioEventos;
import repositorios.RepositorioUsuarios;
import tiquetes.PaqueteDeluxe;
import tiquetes.Tiquete;
import tiquetes.TiqueteIndividual;

public class SistemaCliente extends SubSistema {

    private Scanner sc = new Scanner(System.in);
    private RepositorioEventos repe;
    private GestorID ids;

    public SistemaCliente(Cliente usuarioActual , RepositorioEventos repe, GestorID ids) {
        super(usuarioActual);
        this.repe = repe;
        this.ids = ids;
    }

    @Override
    public void mostrarMenu() {
        int opcion = -1;
        do {
            System.out.println("\n=== MENÚ CLIENTE ===");
            System.out.println("1. Ver eventos disponibles");
            System.out.println("2. Comprar tiquete");
            System.out.println("3. Transferir tiquete");
            System.out.println("4. Consultar saldo");
            System.out.println("5. Publicar tiquete en marketplace");
            System.out.println("6. Ver ofertas activas y comprar");
            System.out.println("7. Hacer una contra oferta en MarketPlace");
            System.out.println("8. Gestionar contraofertas ");
            
            
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");
            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1 : verEventos();
                break;
                case 2 : comprarTiquete();
                break;
                case 3 : transferirTiquete();
                break;
                case 4 : consultarSaldo();
                break;
                case 5:  publicarTiqueteEnMarketplace(); 
                break;
                case 6:  comprarEnMarketplace(); 
                break;
                case 7:  hacerContraofertaMarketplace();
                    break;
                case 8:  gestionarContraofertasRecibidas();
                    break;


                case 0 : salir();
                		System.out.println("Saliendo del sistema de cliente...");
                		break;
                default : System.out.println("Opción inválida.");
            }

        } while (opcion != 0);
    }

    private void verEventos() {
        Map<Integer, Evento> eventos = repe.getEventosActivosM();

        if (eventos == null || eventos.isEmpty()) {
            System.out.println("No hay eventos disponibles en este momento.");
            return;
        }

        System.out.println("\n=== EVENTOS DISPONIBLES ===");
        for (Evento e : eventos.values()) {
            System.out.println("ID: " + e.getIdEvento()
                    + " | Tipo: " + e.getTipoEvento()
                    + " | Fecha: " + e.getFechaEvento()
                    + " | Capacidad: " + e.getCapacidadEvento());
        }
    }

    private void comprarTiquete() {
    	verEventos();
		boolean datosCorrectos = false;
		do {
    		System.out.print("Seleccione el evento, posteriormente la localidad en la que desea su tiquete");
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
		System.out.println("Tercerizando compra...");
		System.out.println("Compra Finalizada");
		
		
	}
    private void transferirTiquete() {
        Cliente clienteOrigen = (Cliente) usuario;

        if (clienteOrigen.getTiquet().isEmpty()) {
            System.out.println("No tienes tiquetes para transferir.");
            return;
        }

        System.out.println("\n=== TUS TIQUETES ===");
        int index = 1;
        Tiquete[] arreglo = new Tiquete[clienteOrigen.getTiquet().size()];
        int i = 0;
        for (Tiquete t : clienteOrigen.getTiquet()) {
            arreglo[i] = t;
            System.out.println(index + ". ID: " + t.getId()
                    + " | Evento: " + t.getEvento().getIdEvento()
                    + " | Precio total: " + t.getPrecioTotal());
            index++;
            i++;
        }

        System.out.print("Selecciona el número del tiquete que deseas transferir: ");
        int seleccion = sc.nextInt();
        sc.nextLine();

        if (seleccion < 1 || seleccion > arreglo.length) {
            System.out.println("Selección inválida.");
            return;
        }

        Tiquete tiqueteSeleccionado = arreglo[seleccion - 1];

        System.out.print("Ingresa el login del usuario destino: ");
        String loginDestino = sc.nextLine();

        System.out.print("Ingresa la contraseña del usuario destino: ");
        String passwordDestino = sc.nextLine();

        RepositorioUsuarios repoUsuarios = RepositorioUsuarios.cargar();
        Usuario usuarioDestino = repoUsuarios.getUsuario(loginDestino);

        if (usuarioDestino == null) {
            System.out.println("No existe un usuario con ese login.");
            return;
        }

        if (!usuarioDestino.getPassword().equals(passwordDestino)) {
            System.out.println("La contraseña del usuario destino es incorrecta.");
            return;
        }

        if (!(usuarioDestino instanceof Cliente)) {
            System.out.println("El usuario destino no es un cliente.");
            return;
        }

        Cliente clienteDestino = (Cliente) usuarioDestino;

        clienteOrigen.getTiquet().remove(tiqueteSeleccionado);
        clienteDestino.agregarTiquete(tiqueteSeleccionado);
        tiqueteSeleccionado.setDuenoActual(clienteDestino);

        System.out.println(" Tiquete " + tiqueteSeleccionado.getId() +" transferido correctamente a " + usuarioDestino.getNombre() + ".");
    }
    private void consultarSaldo() {
        Cliente c = (Cliente) usuario;
        System.out.println("Tu saldo actual es: $" + c.getSaldo());
    }
    private void publicarTiqueteEnMarketplace() {
        System.out.println("=== PUBLICAR TIQUETE EN MARKETPLACE ===");

        Cliente clienteActual = (Cliente) usuario;

        Set<TiqueteIndividual> setTiquetes = clienteActual.getTiquet();
        if (setTiquetes == null || setTiquetes.isEmpty()) {
            System.out.println("No tienes tiquetes para publicar.");
            return;
        }

        List<Tiquete> misTiquetes = new ArrayList<>(setTiquetes);

        for (int i = 0; i < misTiquetes.size(); i++) {
            Tiquete t = misTiquetes.get(i);
            System.out.println((i + 1) + ". ID: " + t.getId()
                    + " | Transferible: " + t.esTransferible());
        }

        System.out.print("Seleccione el número del tiquete a publicar: ");
        int opcionTiq = sc.nextInt();
        sc.nextLine();

        if (opcionTiq < 1 || opcionTiq > misTiquetes.size()) {
            System.out.println("Opción inválida.");
            return;
        }

        Tiquete tiq = misTiquetes.get(opcionTiq - 1);

        System.out.print("Ingrese el precio de reventa: ");
        double precio = sc.nextDouble();
        sc.nextLine();

        try {
            OfertaReventa oferta = clienteActual.publicarTiqueteEnMarketplace(tiq, precio, repoOfertas);
            System.out.println("Oferta publicada con ID: " + oferta.getId());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    private void comprarEnMarketplace() {
        System.out.println("=== OFERTAS DISPONIBLES EN MARKETPLACE ===");

        List<OfertaReventa> activas = repoOfertas.obtenerActivas();
        if (activas == null || activas.isEmpty()) {
            System.out.println("No hay ofertas activas.");
            return;
        }

        for (int i = 0; i < activas.size(); i++) {
            OfertaReventa o = activas.get(i);
            System.out.println((i + 1) + ". ID Oferta: " + o.getId()
                    + " | Tiquete: " + o.getTiquete().getId()
                    + " | Precio: " + o.getPrecio()
                    + " | Vendedor: " + o.getVendedor().getLogin());
        }

        System.out.print("Seleccione la oferta a comprar: ");
        int opcion = sc.nextInt();
        sc.nextLine();

        if (opcion < 1 || opcion > activas.size()) {
            System.out.println("Opción inválida.");
            return;
        }

        OfertaReventa oferta = activas.get(opcion - 1);
        Cliente clienteActual = (Cliente) usuario;

        try {
            clienteActual.comprarOfertaReventa(oferta, repoOfertas);
            System.out.println("Compra realizada con éxito.");
        } catch (Exception e) {
            System.out.println("No se pudo comprar: " + e.getMessage());
        }
    }
    private void hacerContraofertaMarketplace() {
        System.out.println("=== HACER CONTRAOFERTA EN MARKETPLACE ===");

        // Ofertas activas actuales
        List<OfertaReventa> activas = repoOfertas.obtenerActivas();
        if (activas == null || activas.isEmpty()) {
            System.out.println("No hay ofertas activas.");
            return;
        }

        for (int i = 0; i < activas.size(); i++) {
            OfertaReventa o = activas.get(i);
            System.out.println((i + 1) + ". ID Oferta: " + o.getId()
                    + " | Tiquete: " + o.getTiquete().getId()
                    + " | Precio actual: " + o.getPrecio()
                    + " | Vendedor: " + o.getVendedor().getLogin());
        }

        System.out.print("Seleccione la oferta sobre la que quiere contraofertar: ");
        int opcion = sc.nextInt();
        sc.nextLine();

        if (opcion < 1 || opcion > activas.size()) {
            System.out.println("Opción inválida.");
            return;
        }

        OfertaReventa oferta = activas.get(opcion - 1);
        Cliente clienteActual = (Cliente) usuario;

        // No dejar contraofertar sobre su propia oferta
        if (oferta.getVendedor().equals(clienteActual)) {
            System.out.println("No puedes contraofertar sobre tu propia oferta.");
            return;
        }

        System.out.print("Ingrese el nuevo precio propuesto: ");
        double nuevoPrecio = sc.nextDouble();
        sc.nextLine();

        try {
            clienteActual.hacerContraoferta(oferta, nuevoPrecio);
            System.out.println("Contraoferta registrada correctamente.");
        } catch (Exception e) {
            System.out.println("No se pudo registrar la contraoferta: " + e.getMessage());
        }
    }
    private void gestionarContraofertasRecibidas() {
        System.out.println("=== GESTIONAR CONTRAOFERTAS RECIBIDAS ===");
        Cliente vendedor = (Cliente) usuario;

        // Todas las ofertas activas del vendedor
        List<OfertaReventa> mias = repoOfertas.obtenerOfertasDeVendedor(vendedor);

        // Filtrar solo las que tienen contraoferta pendiente
        List<OfertaReventa> conContraoferta = new ArrayList<>();
        for (OfertaReventa o : mias) {
            if (o.tieneContraofertaPendiente()) {
                conContraoferta.add(o);
            }
        }

        if (conContraoferta.isEmpty()) {
            System.out.println("No tienes contraofertas pendientes.");
            return;
        }

        for (int i = 0; i < conContraoferta.size(); i++) {
            OfertaReventa o = conContraoferta.get(i);
            System.out.println((i + 1) + ". Oferta " + o.getId()
                    + " | Tiquete: " + o.getTiquete().getId()
                    + " | Precio actual: " + o.getPrecio()
                    + " | Comprador: " + o.getCompradorProponente().getLogin()
                    + " | Precio propuesto: " + o.getPrecioPropuesto());
        }

        System.out.print("Seleccione la contraoferta a gestionar: ");
        int opcion = sc.nextInt();
        sc.nextLine();

        if (opcion < 1 || opcion > conContraoferta.size()) {
            System.out.println("Opción inválida.");
            return;
        }

        OfertaReventa oferta = conContraoferta.get(opcion - 1);

        System.out.println("1. Aceptar contraoferta");
        System.out.println("2. Rechazar contraoferta");
        System.out.print("Seleccione una opción: ");
        int decision = sc.nextInt();
        sc.nextLine();

        try {
            if (decision == 1) {
                vendedor.aceptarContraoferta(oferta);
                System.out.println("Contraoferta aceptada. La venta ha sido realizada.");
            } else if (decision == 2) {
                vendedor.rechazarContraoferta(oferta);
                System.out.println("Contraoferta rechazada.");
            } else {
                System.out.println("Opción inválida.");
            }
        } catch (Exception e) {
            System.out.println("Error al gestionar la contraoferta: " + e.getMessage());
        }
    }



	@Override
	public void salir() {
		repe.guardar();
		ids.finalizarIDs(); //Chambonada para mantener id persistido
		ids.guardar();
		
	}
}
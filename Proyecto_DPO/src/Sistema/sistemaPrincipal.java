 package Sistema;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

import Usuarios.Administrador;
import Usuarios.Cliente;
import Usuarios.Promotor;
import Usuarios.Usuario;
import repositorios.GestorID;
import repositorios.Propuestas;
import repositorios.RepositorioEventos;
import repositorios.RepositorioUsuarios;
import repositorios.RepositorioVenues;

public class sistemaPrincipal {
	private static Scanner sc = new Scanner(System.in);
	private SubSistema subSistema;
	private Usuario usuarioActual;
    private RepositorioUsuarios repu;
    private RepositorioVenues repv;
    private RepositorioEventos repe;
    private Propuestas repp;
    private GestorID ids;
    
    
	public static void main(String[] args) {
		sistemaPrincipal sistema = new sistemaPrincipal();
		sistema.iniciar();
		
	}
	
	public void iniciar() {
		
		this.repu = RepositorioUsuarios.cargar();
		this.repv = RepositorioVenues.cargar();
		this.repe = RepositorioEventos.cargar();
		this.repp = Propuestas.cargar();
		this.ids = GestorID.cargar();
		ids.inicializarIDs();
		boolean opcionCorrecta = false;
		System.out.println("Bienvenido a BoletaMaster");
		
		do{
			System.out.println("Registrarse(0) o loguearse(1)");
			int opcion = sc.nextInt();
			sc.nextLine();
			if(opcion == 0) {
				registrarUsuario();
				opcionCorrecta = true;
				
			}
			else if(opcion == 1) {
				loguearUsuario();
				opcionCorrecta = true;
			}
			else {
				System.out.println("Opcion incorrecta");
			}
		}while (opcionCorrecta != true); 
		
		
	}
	public void registrarUsuario() {
	    boolean datosCorrectos = false;
	    String tipo = null;
	    System.out.println("Registro de usuario");
	    System.out.println("Seleccione el tipo de usuario");
	    System.out.println("1. Administrador");
	    System.out.println("2. Promotor");
	    System.out.println("3. Cliente");
	    int opcion = sc.nextInt();
	    sc.nextLine();
	    switch (opcion) {
	        case 1: tipo = "ADMINISTRADOR"; break;
	        case 2: tipo = "PROMOTOR"; break;
	        case 3: tipo = "CLIENTE"; break;
	        default:
	            System.out.println("Opción inválida.");
	            return; // salimos si la opción no sirve
	    }

	    while (!datosCorrectos) {

	        if (tipo.equals("ADMINISTRADOR")) {
	            System.out.print("Nombre: ");
	            String nombre = sc.nextLine();
	            System.out.print("Email: ");
	            String email = sc.nextLine();
	            System.out.print("Login: ");
	            String login = sc.nextLine();
	            System.out.print("Password: ");
	            String password = sc.nextLine();

	            if (email.contains("@") && repu.getUsuario(login) == null) {
	                datosCorrectos = true;
	                Usuario admin = new Administrador(nombre, email, login, password);
	                repu.agregarUsuario(admin);
	                usuarioActual = admin;
	                subSistema = new SistemaAdministrador((Administrador) usuarioActual, repu, repv, repe, repp, ids);

	                subSistema.mostrarMenu();
	            } else {
	                System.out.println("Datos incorrectos, intente de nuevo");
	            }
	        }

	        else if (tipo.equals("PROMOTOR")) {
	            System.out.print("Nombre: ");
	            String nombre = sc.nextLine();
	            System.out.print("Email: ");
	            String email = sc.nextLine();
	            System.out.print("Login: ");
	            String login = sc.nextLine();
	            System.out.print("Password: ");
	            String password = sc.nextLine();
	            System.out.print("NIT: ");
	            String nit = sc.nextLine();

	            if (email.contains("@") && repu.getUsuario(login) == null) {
	                datosCorrectos = true;
	                Usuario promo = new Promotor(nombre, email, login, password, 0, nit);
	                repp.agregarPropuestapromotor((Promotor) promo);
	                usuarioActual = promo;
	                System.out.println("El usuario ya quedó agregado, espera a que un administrador te acepte");
	                salir();
	            } else {
	                System.out.println("Datos incorrectos, intente de nuevo");
	            }
	        }

	        else if (tipo.equals("CLIENTE")) {
	            System.out.print("Nombre: ");
	            String nombre = sc.nextLine();
	            System.out.print("Email: ");
	            String email = sc.nextLine();
	            System.out.print("Login: ");
	            String login = sc.nextLine();
	            System.out.print("Password: ");
	            String password = sc.nextLine();
	            System.out.print("Documento: ");
	            String doc = sc.nextLine();
	            System.out.print("Telefono: ");
	            String telefono = sc.nextLine();

	            if (email.contains("@") && repu.getUsuario(login) == null) {
	                datosCorrectos = true;
	                Usuario cliente = new Cliente(nombre, email, login, password, doc, telefono);
	                repu.agregarUsuario(cliente);
	                usuarioActual = cliente;
	                subSistema = new SistemaCliente((Cliente) usuarioActual, repe, ids);
	                subSistema.mostrarMenu();
	            } else {
	                System.out.println("Datos incorrectos, intente de nuevo");
	            }
	        }
	    }
	}

	public void loguearUsuario() {
		boolean datosCorrectos = false;
		while(datosCorrectos == false) {
			System.out.println("Login de usuario");
	        System.out.println("Login: "); String login = sc.nextLine();
	        System.out.println("Password: "); String password = sc.nextLine();
	        usuarioActual = repu.getUsuario(login);
	        if(usuarioActual != null) {
	        	if(usuarioActual.getPassword().equals(password)) { //Se hace en instancias distintas pues si usuarioActual es null get pass lanzara error
	        		datosCorrectos = true;
	        		System.out.print("Paseaca");
	        	}
	        	else {
		        	System.out.println("Usuario o contraseña incorrectos, intente nuevamente!!");
		        }
	        	
	        }
	        else {
	        	System.out.println("Usuario o contraseña incorrectos, intente nuevamente!!");
	        }
		}
		String rol = usuarioActual.getRol();
		if(rol.equals("ADMINISTRADOR")) {
			subSistema = new SistemaAdministrador((Administrador) usuarioActual, repu, repv, repe,repp, ids);
		}
		else if(rol.equals("CLIENTE")) {
			subSistema = new SistemaCliente((Cliente) usuarioActual, repe,ids);
		}
		else if(rol.equals("PROMOTOR")) {
			subSistema = new SistemaPromotor((Promotor) usuarioActual, repe, repv,ids, repp);
			
		}
		subSistema.mostrarMenu();
    }
	
	public void salir() {
		repu.guardar();
		repv.guardar();
		repe.guardar();
		repp.guardar();
		ids.finalizarIDs(); 
		ids.guardar();
		
	}

    
}

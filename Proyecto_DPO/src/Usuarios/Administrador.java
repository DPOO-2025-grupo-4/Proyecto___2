package Usuarios;

import java.time.LocalDateTime;
import java.util.Objects;
import Logs.LogSystem;
import repositorios.OfertaReventa;
import repositorios.RepositorioOfertas;


import Eventos.Evento;
import Eventos.Finanzas;
import Eventos.Venue;
import tiquetes.TiqueteIndividual;

public class Administrador extends Usuario {
	public Administrador() {}
	public Administrador(String nombre, String email, String login, String password) {
		super(nombre, email, login, password,"ADMINISTRADOR");
    }

    public void autorizarReembolso(TiqueteIndividual tiquete, double monto) {
        Objects.requireNonNull(tiquete, "tiquete");
        if (monto <= 0) {
            throw new IllegalArgumentException("el monto debe ser mayor a 0");
        }

        tiquete.reembolsar();

        System.out.println("Reembolso autorizado para el tiquete " + tiquete.getId());
    }

    public double consultarFinanzas(Finanzas finanzas) {
        Objects.requireNonNull(finanzas, "finanzas");
        return finanzas.getGananciaTotalAdministrador();
    }
    public void consultarLog() {
        for (String linea : LogSystem.obtenerRegistros()) {
            System.out.println(linea);
        }
    }
    public void eliminarOfertaReventa(OfertaReventa oferta, RepositorioOfertas repo) {
        Objects.requireNonNull(oferta, "oferta");
        Objects.requireNonNull(repo, "repo");

        oferta.eliminarPorAdmin(getLogin());
        repo.eliminar(oferta);
    }

}

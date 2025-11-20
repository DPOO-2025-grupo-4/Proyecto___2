package Usuarios;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import repositorios.OfertaReventa;
import tiquetes.TiqueteIndividual;
import repositorios.OfertaReventa;
import repositorios.RepositorioOfertas;
import java.util.HashSet;
import java.util.Objects;
import tiquetes.Tiquete;

public class Cliente extends Usuario {
private String documento;
private String telefono;
private final Set<TiqueteIndividual> tiquetes = new HashSet<>();


public Cliente() {}
public Cliente (String nombre, String email, String login, String password,String documento, String telefono) {
	super(nombre,email,login,password, "CLIENTE");
	this.documento=Objects.requireNonNull(documento,"documento");
	this.telefono=Objects.requireNonNull(telefono,"telefono");
}

public String getDocumento() {
	return documento;
}

public String getTelefono() {
	return telefono;
}
public Set<TiqueteIndividual> getTiquet(){
	return tiquetes;
}
public void agregarTiquete(Tiquete tiquete) {
    Objects.requireNonNull(tiquete, "tiquete");
    tiquetes.add((TiqueteIndividual) tiquete);
}
public OfertaReventa publicarTiqueteEnMarketplace(Tiquete tiquete,
                                                  double precio,
                                                  RepositorioOfertas repoOfertas) {
    Objects.requireNonNull(tiquete, "tiquete");
    Objects.requireNonNull(repoOfertas, "repoOfertas");

    if (!tiquetes.contains(tiquete)) {
        throw new IllegalStateException("El tiquete no pertenece a este cliente");
    }

    String idOferta = UUID.randomUUID().toString();

    OfertaReventa oferta = new OfertaReventa(idOferta, tiquete, this, precio);
    repoOfertas.agregar(oferta);

    return oferta;
}

public void comprarOfertaReventa(OfertaReventa oferta, RepositorioOfertas repoOfertas) {
    Objects.requireNonNull(oferta, "oferta");
    Objects.requireNonNull(repoOfertas, "repoOfertas");

    if (!oferta.estaActiva()) {
        throw new IllegalStateException("La oferta no está activa");
    }

    oferta.ejecutarVenta(this);

    repoOfertas.eliminar(oferta);
}
public void hacerContraoferta(OfertaReventa oferta, double nuevoPrecio) {
    Objects.requireNonNull(oferta, "oferta");
    oferta.registrarContraoferta(this, nuevoPrecio);
}

public void aceptarContraoferta(OfertaReventa oferta) {
    Objects.requireNonNull(oferta, "oferta");
    if (!this.equals(oferta.getVendedor())) {
        throw new IllegalStateException("Solo el vendedor puede aceptar la contraoferta");
    }
    oferta.aceptarContraoferta();
}

public void rechazarContraoferta(OfertaReventa oferta) {
    Objects.requireNonNull(oferta, "oferta");
    if (!this.equals(oferta.getVendedor())) {
        throw new IllegalStateException("Solo el vendedor puede rechazar la contraoferta");
    }
    oferta.rechazarContraoferta();
}



}

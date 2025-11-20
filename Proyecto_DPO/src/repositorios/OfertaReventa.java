package repositorios;

import java.time.LocalDateTime;
import java.util.Objects;

import Usuarios.Cliente;
import Logs.LogSystem;
import tiquetes.Tiquete;

public class OfertaReventa {

    public enum Estado {
        ACTIVA,
        CANCELADA_POR_VENDEDOR,
        ELIMINADA_POR_ADMIN,
        VENDIDA
    }

    private final String id;
    private final Tiquete tiquete;
    private final Cliente vendedor;
    private Cliente compradorProponente;  
    private Double precioPropuesto;      

    private Cliente comprador;
    private double precio;
    private Estado estado;
    private final LocalDateTime fechaCreacion;

    public OfertaReventa(String id, Tiquete tiquete, Cliente vendedor, double precio) {
        this.id = Objects.requireNonNull(id, "id");
        this.tiquete = Objects.requireNonNull(tiquete, "tiquete");
        this.vendedor = Objects.requireNonNull(vendedor, "vendedor");
        if (precio <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor que cero");
        }
        if (!tiquete.esTransferible()) {
            throw new IllegalArgumentException("El tiquete no es transferible");
        }
        this.precio = precio;
        this.estado = Estado.ACTIVA;
        this.fechaCreacion = LocalDateTime.now();

        LogSystem.registrar(
                "CREACION_OFERTA_REVENTA",
                "Cliente " + vendedor.getLogin() +
                " publicó tiquete " + tiquete.getId() +
                " a precio " + precio +
                " (oferta " + id + ")"
        );
    }

    public String getId() {
        return id;
    }

    public Tiquete getTiquete() {
        return tiquete;
    }

    public Cliente getVendedor() {
        return vendedor;
    }

    public Cliente getComprador() {
        return comprador;
    }
    public Cliente getCompradorProponente() {
        return compradorProponente;
    }

    public Double getPrecioPropuesto() {
        return precioPropuesto;
    }

    public boolean tieneContraofertaPendiente() {
        return compradorProponente != null;
    }


    public double getPrecio() {
        return precio;
    }

    public Estado getEstado() {
        return estado;
    }

    public boolean estaActiva() {
        return estado == Estado.ACTIVA;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    /** El vendedor puede bajar o subir el precio mientras la oferta esté activa. */
    public void cambiarPrecio(double nuevoPrecio) {
        if (!estaActiva()) {
            throw new IllegalStateException("La oferta no está activa");
        }
        if (nuevoPrecio <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor que cero");
        }
        this.precio = nuevoPrecio;

        LogSystem.registrar(
                "CAMBIO_PRECIO_REVENTA",
                "Vendedor " + vendedor.getLogin() +
                " cambió precio de la oferta " + id +
                " a " + nuevoPrecio
        );
    }

    /**
     * Ejecuta la venta:
     *  - Descuenta saldo al comprador
     *  - Acredita saldo al vendedor
     *  - Transfiere el tiquete
     *  - Marca la oferta como VENDIDA
     */
    public void ejecutarVenta(Cliente comprador) {
        if (!estaActiva()) {
            throw new IllegalStateException("La oferta no está activa");
        }

        Objects.requireNonNull(comprador, "comprador");

        if (comprador.getSaldo() < precio) {
            throw new IllegalStateException("Saldo insuficiente para comprar el tiquete");
        }

        // Movemos dinero
        comprador.debitarSaldo(precio);
        vendedor.acreditarSaldo(precio);

        // Movemos el tiquete
        vendedor.getTiquet().remove(tiquete);
        comprador.agregarTiquete(tiquete);
        tiquete.setDuenoActual(comprador);

        this.comprador = comprador;
        this.estado = Estado.VENDIDA;

        LogSystem.registrar(
                "VENTA_REVENTA",
                "Tiquete " + tiquete.getId() +
                " vendido de " + vendedor.getLogin() +
                " a " + comprador.getLogin() +
                " por " + precio +
                " (oferta " + id + ")"
        );
    }

    /** El vendedor cancela la oferta. */
    public void cancelarPorVendedor() {
        if (!estaActiva()) {
            throw new IllegalStateException("La oferta no está activa");
        }
        this.estado = Estado.CANCELADA_POR_VENDEDOR;

        LogSystem.registrar(
                "CANCELACION_OFERTA_REVENTA",
                "Vendedor " + vendedor.getLogin() +
                " canceló la oferta " + id +
                " del tiquete " + tiquete.getId()
        );
    }

    /** El administrador elimina la oferta bajo su discreción. */
    public void eliminarPorAdmin(String adminLogin) {
        if (!estaActiva()) {
            throw new IllegalStateException("La oferta no está activa");
        }
        this.estado = Estado.ELIMINADA_POR_ADMIN;

        LogSystem.registrar(
                "BORRADO_OFERTA_REVENTA_ADMIN",
                "Admin " + adminLogin +
                " eliminó la oferta " + id +
                " del tiquete " + tiquete.getId() +
                " (vendedor " + vendedor.getLogin() + ")"
        );
    }
    public void registrarContraoferta(Cliente comprador, double nuevoPrecio) {
        if (!estaActiva()) {
            throw new IllegalStateException("La oferta no está activa");
        }
        if (comprador == null) {
            throw new IllegalArgumentException("Comprador no puede ser nulo");
        }
        if (comprador.equals(vendedor)) {
            throw new IllegalStateException("El vendedor no puede contraofertar su propia oferta");
        }
        if (nuevoPrecio <= 0) {
            throw new IllegalArgumentException("El precio debe ser positivo");
        }

        this.compradorProponente = comprador;
        this.precioPropuesto = nuevoPrecio;

        LogSystem.registrar(
                "CONTRAOFERTA_REGISTRADA",
                "Comprador " + comprador.getLogin() +
                " hizo contraoferta de " + nuevoPrecio +
                " en oferta " + id +
                " del tiquete " + tiquete.getId()
        );
    }
    public void aceptarContraoferta() {
        if (!estaActiva()) {
            throw new IllegalStateException("La oferta no está activa");
        }
        if (compradorProponente == null || precioPropuesto == null) {
            throw new IllegalStateException("No hay contraoferta por aceptar");
        }

        // Actualizamos el precio de la oferta al propuesto
        this.precio = precioPropuesto;

        LogSystem.registrar(
                "CONTRAOFERTA_ACEPTADA",
                "Vendedor " + vendedor.getLogin() +
                " aceptó contraoferta de " + compradorProponente.getLogin() +
                " por " + precioPropuesto +
                " en oferta " + id
        );

        ejecutarVenta(compradorProponente);

        compradorProponente = null;
        precioPropuesto = null;
    }
    public void rechazarContraoferta() {
        if (compradorProponente == null || precioPropuesto == null) {
            throw new IllegalStateException("No hay contraoferta por rechazar");
        }

        LogSystem.registrar(
                "CONTRAOFERTA_RECHAZADA",
                "Vendedor " + vendedor.getLogin() +
                " rechazó contraoferta de " + compradorProponente.getLogin() +
                " en oferta " + id +
                " (precio propuesto " + precioPropuesto + ")"
        );

        compradorProponente = null;
        precioPropuesto = null;
    }


}

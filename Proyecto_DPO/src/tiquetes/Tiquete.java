package tiquetes;

import Eventos.Evento;
import Usuarios.Usuario;
import Usuarios.Cliente;

public abstract class Tiquete {

    protected String id;
    protected Evento evento;
    protected Usuario duenoActual;
    protected boolean transferible = true;
    protected boolean reembolsado = false;
    protected double descuentoPorcentaje = 0;
    protected double precioFijo = -1;

    // 👉 Asiento opcional
    protected String asiento = null;

    public String getId() {
        return id;
    }

    public Evento getEvento() {
        return evento;
    }

    public Usuario getDuenoActual() {
        return duenoActual;
    }

    public void setDuenoActual(Usuario usuario) {
        this.duenoActual = usuario;
    }

    public boolean esTransferible() {
        return transferible;
    }

    public boolean fueReembolsado() {
        return reembolsado;
    }

    public String getAsiento() {
        return asiento;
    }

    public void setAsiento(String asiento) {
        this.asiento = asiento;
    }
    public void setDescuento(double porcentaje) {
        if (porcentaje < 0) porcentaje = 0;
        if (porcentaje > 100) porcentaje = 100;
        this.descuentoPorcentaje = porcentaje;
        this.precioFijo = -1; 
    }
    public void setPrecioTotal(double precio) {
        if (precio < 0) return;
        this.precioFijo = precio;
        this.descuentoPorcentaje = 0; 
    }

    public abstract double getPrecioTotal();

    public void reembolsar() {
        if (reembolsado) return;

        if (duenoActual instanceof Cliente cliente) {
            double monto = getPrecioTotal();
            cliente.acreditarSaldo(monto);
            cliente.getTiquet().remove(this);
        }
        duenoActual = null;
        reembolsado = true;
    }
}

package tiquetes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Eventos.Evento;
import Usuarios.Usuario;

public abstract class TiqueteMultiple extends Tiquete {

    private static int contadorIds = 1;

    protected List<TiqueteIndividual> tiquetesIncluidos;
    public TiqueteMultiple() {
        super();
    }


    public TiqueteMultiple(Evento eventoReferencia,List<TiqueteIndividual> tiquetes,Usuario comprador) {

        if (eventoReferencia == null) {
            throw new IllegalArgumentException("El evento de referencia no puede ser null");
        }
        if (tiquetes == null || tiquetes.isEmpty()) {
            throw new IllegalArgumentException("Debe incluir al menos un tiquete individual");
        }

        this.id = "TM-" + contadorIds++;
        this.evento = eventoReferencia;
        this.tiquetesIncluidos = new ArrayList<>(tiquetes);
        this.duenoActual = comprador;
    }

    public List<TiqueteIndividual> getTiquetesIncluidos() {
        return Collections.unmodifiableList(tiquetesIncluidos);
    }

    @Override
    public double getPrecioTotal() {
        double total = 0;
        for (TiqueteIndividual t : tiquetesIncluidos) {
            total += t.getPrecioTotal();
        }
        return total;
    }

    public void reembolsar() {
        if (reembolsado) {
            return;
        }

        for (TiqueteIndividual t : tiquetesIncluidos) {
            t.reembolsar();
        }

        this.reembolsado = true;
        this.duenoActual = null;
    }
}

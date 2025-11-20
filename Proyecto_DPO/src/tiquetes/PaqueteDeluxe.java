package tiquetes;

import java.util.List;

import Eventos.Evento;
import Usuarios.Usuario;

public class PaqueteDeluxe extends TiqueteMultiple {

    private String descripcionBeneficios;
    public PaqueteDeluxe() {
        super();
    }


    public PaqueteDeluxe(Evento eventoReferencia, List<TiqueteIndividual> tiquetes,Usuario comprador,String descripcionBeneficios) {
        super(eventoReferencia, tiquetes, comprador);
        this.descripcionBeneficios = descripcionBeneficios;
        this.transferible = false; 
        this.tiquetesIncluidos = tiquetes;
    }

    public String getDescripcionBeneficios() {
        return descripcionBeneficios;
    }
}

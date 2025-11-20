package tiquetes;

import java.util.List;

import Eventos.Evento;
import Usuarios.Usuario;

public class PaseTemporada extends TiqueteMultiple {
	public PaseTemporada() {
	    super();
	}


    public PaseTemporada(Evento eventoReferencia,
                         List<TiqueteIndividual> tiquetes,
                         Usuario comprador) {
        super(eventoReferencia, tiquetes, comprador);
    }
}

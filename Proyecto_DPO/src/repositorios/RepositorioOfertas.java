package repositorios;
import Usuarios.Cliente;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RepositorioOfertas {

    private final Map<String, OfertaReventa> ofertasPorId = new HashMap<>();

    public void agregar(OfertaReventa oferta) {
        ofertasPorId.put(oferta.getId(), oferta);
    }

    public OfertaReventa buscarPorId(String id) {
        return ofertasPorId.get(id);
    }

    public void eliminar(OfertaReventa oferta) {
        if (oferta != null) {
            ofertasPorId.remove(oferta.getId());
        }
    }

    public List<OfertaReventa> obtenerTodas() {
        return new ArrayList<>(ofertasPorId.values());
    }

    public List<OfertaReventa> obtenerActivas() {
        List<OfertaReventa> activas = new ArrayList<>();
        for (OfertaReventa o : ofertasPorId.values()) {
            if (o.estaActiva()) {
                activas.add(o);
            }
        }
        return activas;
    }
    /**
     * Devuelve todas las ofertas activas cuyo vendedor es el cliente dado.
     */
    public List<OfertaReventa> obtenerOfertasDeVendedor(Cliente vendedor) {
        List<OfertaReventa> resultado = new ArrayList<>();
        for (OfertaReventa o : ofertasPorId.values()) {
            if (o.getVendedor().equals(vendedor) && o.estaActiva()) {
                resultado.add(o);
            }
        }
        return resultado;
    }

}

package Eventos;

import java.util.HashMap;
import java.util.Map;
import tiquetes.TiqueteIndividual;

public class Finanzas {

    public static class RegistroFinanciero {
        private double gananciaAdministrador = 0;
        private double gananciaPromotor = 0;

        public void sumarGanancias(double admin, double promotor) {
            gananciaAdministrador += admin;
            gananciaPromotor += promotor;
        }

        public void restarGanancias(double admin, double promotor) {
            gananciaAdministrador -= admin;
            gananciaPromotor -= promotor;
        }

        public double getGananciaAdministrador() {
            return gananciaAdministrador;
        }

        public double getGananciaPromotor() {
            return gananciaPromotor;
        }
    }

    private static Map<Evento, RegistroFinanciero> finanzasPorEvento = new HashMap<>();

    public static void registrarVenta(TiqueteIndividual t) {

        Evento evento = t.getEvento();
        if (evento == null) return;

        finanzasPorEvento.putIfAbsent(evento, new RegistroFinanciero());
        RegistroFinanciero reg = finanzasPorEvento.get(evento);

        double precioBase = t.getLocalidad().getPrecio();
        double porcentaje = evento.getPorcentajeServicio();
        double emision = evento.getCobroEmision();

        double admin = precioBase * porcentaje / 100.0 + emision;
        double promotor = precioBase;

        reg.sumarGanancias(admin, promotor);
    }

    public static void registrarReembolso(TiqueteIndividual t) {

        Evento evento = t.getEvento();
        if (evento == null) return;

        finanzasPorEvento.putIfAbsent(evento, new RegistroFinanciero());
        RegistroFinanciero reg = finanzasPorEvento.get(evento);

        double precioBase = t.getLocalidad().getPrecio();
        double porcentaje = evento.getPorcentajeServicio();
        double emision = evento.getCobroEmision();

        double admin = precioBase * porcentaje / 100.0 + emision;
        double promotor = precioBase;

        reg.restarGanancias(admin, promotor);
    }

    // Reportes:

    public static double getGananciaAdministrador(Evento evento) {
        RegistroFinanciero reg = finanzasPorEvento.get(evento);
        return (reg != null) ? reg.getGananciaAdministrador() : 0.0;
    }

    public static double getGananciaPromotor(Evento evento) {
        RegistroFinanciero reg = finanzasPorEvento.get(evento);
        return (reg != null) ? reg.getGananciaPromotor() : 0.0;
    }

    public static double getGananciaTotalAdministrador() {
        return finanzasPorEvento.values().stream()
                .mapToDouble(RegistroFinanciero::getGananciaAdministrador)
                .sum();
    }

    public static double getGananciaTotalPromotor() {
        return finanzasPorEvento.values().stream()
                .mapToDouble(RegistroFinanciero::getGananciaPromotor)
                .sum();
    }
}

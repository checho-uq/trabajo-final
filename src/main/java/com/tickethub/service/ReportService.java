package com.tickethub.service;

import com.tickethub.model.*;
import com.tickethub.model.decorator.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ReportService {
    private List<Compra> compras;

    public ReportService(List<Compra> compras) {
        this.compras = compras;
    }

    public Map<String, Double> obtenerVentasPorPeriodo(LocalDate inicio, LocalDate fin) {
        Map<String, Double> resultado = new LinkedHashMap<>();
        for (Compra c : compras) {
            LocalDate fc = c.getFechaCreacion().toLocalDate();
            if (!fc.isBefore(inicio) && !fc.isAfter(fin)) {
                String key = fc.toString();
                resultado.merge(key, c.getTotal(), Double::sum);
            }
        }
        return resultado;
    }

    public Map<String, Double> obtenerOcupacionPorZona(Evento e) {
        Map<String, Double> resultado = new LinkedHashMap<>();
        if (e.getRecintoAsociado() != null) {
            for (Zona z : e.getRecintoAsociado().getZonas()) {
                resultado.put(z.getNombre(), z.consultarOcupacion());
            }
        }
        return resultado;
    }

    public Map<String, Double> obtenerIngresosServiciosAdicionales(Evento e) {
        Map<String, Double> resultado = new LinkedHashMap<>();
        double ingresoVip = 0, ingresoSeguro = 0, ingresoMerch = 0, ingresoParqueadero = 0, ingresoAccesoPref = 0;
        for (Compra c : compras) {
            if (c.getEventoAsociado().getIdEvento().equals(e.getIdEvento())) {
                for (Entrada ent : c.getItemsCompra()) {
                    // Walk decorator chain to detect all services
                    Entrada current = ent;
                    while (current instanceof ServicioDecorator) {
                        ServicioDecorator sd = (ServicioDecorator) current;
                        if (sd instanceof AccesoVipDecorator) ingresoVip += sd.getCostoAdicional();
                        if (sd instanceof SeguroCancelacionDecorator) ingresoSeguro += sd.getCostoAdicional();
                        if (sd instanceof MerchandisingDecorator) ingresoMerch += sd.getCostoAdicional();
                        if (sd instanceof ParqueaderoDecorator) ingresoParqueadero += sd.getCostoAdicional();
                        if (sd instanceof AccesoPreferencialDecorator) ingresoAccesoPref += sd.getCostoAdicional();
                        current = sd.getEntradaDecorada();
                    }
                }
            }
        }
        resultado.put("Acceso VIP", ingresoVip);
        resultado.put("Seguro Cancelación", ingresoSeguro);
        resultado.put("Merchandising", ingresoMerch);
        resultado.put("Parqueadero", ingresoParqueadero);
        resultado.put("Acceso Preferencial", ingresoAccesoPref);
        return resultado;
    }

    public double obtenerTasaCancelacion() {
        if (compras.isEmpty()) return 0;
        long canceladas = compras.stream().filter(c -> c.getEstadoActual().getNombreEstado().equals("CANCELADA") ||
                c.getEstadoActual().getNombreEstado().equals("REEMBOLSADA")).count();
        return (double) canceladas / compras.size() * 100;
    }

    public List<Evento> obtenerTopEventos() {
        Map<Evento, Long> conteo = new HashMap<>();
        for (Compra c : compras) {
            conteo.merge(c.getEventoAsociado(), 1L, Long::sum);
        }
        return conteo.entrySet().stream()
                .sorted(Map.Entry.<Evento, Long>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public double obtenerTotalIngresos() {
        return compras.stream().mapToDouble(Compra::getTotal).sum();
    }
}
